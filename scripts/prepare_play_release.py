#!/usr/bin/env python3
"""Build and verify signed files for Play internal testing; never uploads them."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import secrets
import shutil
import ssl
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CONFIG = Path.home() / ".config/moment-camera/release.properties"
OUTPUT = ROOT / "app/build/outputs/play"
PACKAGE = "studio.moment.camera"


def run(command, env, capture=False):
    result = subprocess.run([str(part) for part in command], cwd=ROOT, env=env,
                            text=True, stdout=subprocess.PIPE if capture else None,
                            stderr=subprocess.STDOUT if capture else None)
    if result.returncode:
        # Keep captured signing-tool output out of logs; it may reference private configuration.
        raise RuntimeError(f"{Path(str(command[0])).name} failed (exit {result.returncode}).")
    return result.stdout or ""


def build_environment():
    env = os.environ.copy()
    studio_java = Path("/Applications/Android Studio.app/Contents/jbr/Contents/Home")
    if not env.get("JAVA_HOME") and studio_java.is_dir():
        env["JAVA_HOME"] = str(studio_java)
    if not env.get("JAVA_HOME"):
        raise RuntimeError("Set JAVA_HOME to JDK 17 or newer.")
    sdk = env.get("ANDROID_HOME") or env.get("ANDROID_SDK_ROOT")
    if not sdk and (Path.home() / "Library/Android/sdk").is_dir():
        sdk = str(Path.home() / "Library/Android/sdk")
    if not sdk:
        raise RuntimeError("Set ANDROID_HOME to the Android SDK directory.")
    env["ANDROID_HOME"] = sdk
    return env


def java_property(value):
    return str(value).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r")


def create_upload_key(config, env):
    if config.exists():
        print("Existing upload signing configuration will be used; no key was replaced.")
        return
    if config != DEFAULT_CONFIG:
        raise RuntimeError("Create new keys at the default private location. Use MOMENT_SIGNING_PROPERTIES only for an existing key configuration.")
    config.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    key = config.parent / "upload-key.p12"
    if key.exists():
        raise RuntimeError("An upload key already exists without its configuration. Restore the configuration; do not replace the key.")
    password = secrets.token_urlsafe(48)
    key_env = env | {"MOMENT_NEW_UPLOAD_PASSWORD": password}
    with tempfile.TemporaryDirectory(prefix="keygen-", dir=config.parent) as temporary:
        staged_key = Path(temporary) / key.name
        run([Path(env["JAVA_HOME"]) / "bin/keytool", "-genkeypair", "-noprompt",
             "-keystore", staged_key, "-storetype", "PKCS12", "-alias", "moment-upload",
             "-keyalg", "RSA", "-keysize", "3072", "-validity", "10000",
             "-dname", "CN=Moment Upload", "-storepass:env", "MOMENT_NEW_UPLOAD_PASSWORD",
             "-keypass:env", "MOMENT_NEW_UPLOAD_PASSWORD"], key_env, capture=True)
        staged_key.chmod(0o600)
        # Publish the password before the key so an interrupted move cannot orphan the key.
        fd = os.open(config, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
        try:
            with os.fdopen(fd, "w", encoding="utf-8") as handle:
                handle.write(f"storeFile={java_property(key)}\nstorePassword={password}\n"
                             f"keyAlias=moment-upload\nkeyPassword={password}\n")
                handle.flush()
                os.fsync(handle.fileno())
            # Hard-link without replacing any key that appeared during generation.
            os.link(staged_key, key)
        except BaseException:
            config.unlink(missing_ok=True)
            raise
    print(f"Created an upload key for a NEW Play app at {key}.")
    print(f"Signing configuration: {config} (private; do not commit or paste its contents).")


def sha256(path):
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--create-upload-key", action="store_true",
                        help="Generate a private upload key for a new Play app only; never overwrite an existing key.")
    args = parser.parse_args()
    env = build_environment()
    config = Path(env.get("MOMENT_SIGNING_PROPERTIES", DEFAULT_CONFIG)).expanduser().resolve()
    if args.create_upload_key:
        create_upload_key(config, env)
    if not config.is_file():
        raise RuntimeError("No upload signing configuration. For a new app use --create-upload-key; for an existing app set MOMENT_SIGNING_PROPERTIES.")
    env["MOMENT_SIGNING_PROPERTIES"] = str(config)
    env["MOMENT_REQUIRE_SIGNING"] = "true"
    run([ROOT / "gradlew", ":app:bundleRelease", ":app:assembleRelease"], env)

    bundle = ROOT / "app/build/outputs/bundle/release/app-release.aab"
    apk = ROOT / "app/build/outputs/apk/release/app-release.apk"
    java_bin = Path(env["JAVA_HOME"]) / "bin"
    verification = run([java_bin / "jarsigner", "-J-Duser.language=en", "-J-Duser.country=US",
                        "-verify", bundle], env, capture=True)
    if "jar verified." not in verification or "unsigned entries" in verification:
        raise RuntimeError("The AAB is not signed or its signature could not be verified.")
    certificate = run([java_bin / "keytool", "-printcert", "-rfc", "-jarfile", bundle], env, capture=True)
    certificate_match = re.search(r"-----BEGIN CERTIFICATE-----.*?-----END CERTIFICATE-----", certificate, re.DOTALL)
    if not certificate_match:
        raise RuntimeError("The bundle signing certificate could not be read.")
    certificate = certificate_match[0] + "\n"
    bundle_cert_digest = hashlib.sha256(ssl.PEM_cert_to_DER_cert(certificate)).hexdigest()
    # Use the SDK build tools already installed for this project.
    versions = [p for p in (Path(env["ANDROID_HOME"]) / "build-tools").iterdir()
                if re.fullmatch(r"\d+\.\d+\.\d+", p.name) and (p / "apksigner").is_file()]
    if not versions:
        raise RuntimeError("Android SDK build-tools with apksigner are required.")
    build_tools = max(versions, key=lambda p: tuple(map(int, p.name.split("."))))
    signature = run([build_tools / "apksigner", "verify", "--print-certs", apk], env, capture=True)
    if "Android Debug" in signature:
        raise RuntimeError("A debug signing key cannot be used for a Play test track.")
    badging = run([build_tools / "aapt2", "dump", "badging", apk], env, capture=True)
    package = re.search(r"package: name='([^']+)' versionCode='(\d+)' versionName='([^']+)'", badging)
    if not package or package[1] != PACKAGE or "application-debuggable" in badging:
        raise RuntimeError("The release package is unexpected or debuggable.")
    cert_digests = set(re.findall(r"certificate SHA-256 digest: ([0-9a-f]{64})", signature))
    if cert_digests != {bundle_cert_digest}:
        raise RuntimeError("The APK and AAB signing certificate fingerprints do not match.")

    OUTPUT.mkdir(parents=True, exist_ok=True)
    name = f"moment-{package[3]}-{package[2]}"
    artifacts = {"bundle": OUTPUT / f"{name}.aab", "apk": OUTPUT / f"{name}.apk"}
    shutil.copyfile(bundle, artifacts["bundle"])
    shutil.copyfile(apk, artifacts["apk"])
    (OUTPUT / "upload-certificate.pem").write_text(certificate, encoding="utf-8")
    shutil.copyfile(ROOT / "play/ko-KR/release-notes.txt", OUTPUT / "release-notes-ko-KR.txt")
    info = {"packageName": PACKAGE, "versionName": package[3], "versionCode": int(package[2]),
            "intendedTrack": "internal", "uploadedToGooglePlay": False,
            "uploadCertificateSha256": bundle_cert_digest,
            "files": {kind: {"path": path.name, "sha256": sha256(path)} for kind, path in artifacts.items()}}
    (OUTPUT / "release-info.json").write_text(json.dumps(info, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Verified signed release: {OUTPUT}")
    print(f"Play upload: {artifacts['bundle'].name}")
    print("This command does not upload or publish a release to Google Play.")


if __name__ == "__main__":
    try:
        main()
    except (RuntimeError, OSError) as error:
        print(f"Release preparation failed: {error}", file=sys.stderr)
        sys.exit(1)
