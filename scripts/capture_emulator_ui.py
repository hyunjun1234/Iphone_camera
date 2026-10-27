#!/usr/bin/env python3
"""Capture real app screens on a disposable emulator; resets its app preferences."""
import argparse
import os
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--serial', default='emulator-5554')
    parser.add_argument('--apk', default='app/build/outputs/apk/debug/app-debug.apk')
    parser.add_argument('--output', default='app/build/ui-screenshots')
    args = parser.parse_args()
    if not args.serial.startswith('emulator-'):
        parser.error('Use a disposable emulator, not a physical phone.')
    sdk = os.environ.get('ANDROID_HOME') or os.environ.get('ANDROID_SDK_ROOT')
    executable = str(Path(sdk) / 'platform-tools/adb') if sdk else 'adb'
    output = Path(args.output)
    output.mkdir(parents=True, exist_ok=True)

    def adb(*arguments):
        return subprocess.check_output([executable, '-s', args.serial, *arguments], timeout=45)

    def nodes():
        adb('shell', 'uiautomator', 'dump', '/sdcard/moment-ui.xml')
        return list(ET.fromstring(adb('shell', 'cat', '/sdcard/moment-ui.xml')).iter('node'))

    def find(label, attribute='content-desc', partial=False):
        for node in nodes():
            value = node.get(attribute, '')
            if label in value if partial else label == value:
                return node
        return None

    def tap(label, attribute='content-desc', partial=False):
        node = find(label, attribute, partial)
        if node is None:
            raise RuntimeError(f'Missing control: {label}')
        x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
        adb('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
        time.sleep(1)

    def shot(name):
        path = output / f'{name}.png'
        path.write_bytes(adb('exec-out', 'screencap', '-p'))
        print(path, flush=True)

    adb('install', '-r', args.apk)
    adb('shell', 'pm', 'clear', 'studio.moment.camera')
    adb('shell', 'pm', 'grant', 'studio.moment.camera', 'android.permission.CAMERA')
    adb('shell', 'am', 'start', '-n', 'studio.moment.camera/.MainActivity')
    for _ in range(15):
        time.sleep(2)
        shutter = find('사진 촬영')
        if shutter is not None and shutter.get('enabled') == 'true':
            break
    else:
        shot('camera-not-ready')
        raise RuntimeError('Camera did not become ready')
    shot('camera')
    tap('스타일 선택')
    time.sleep(1)
    shot('camera-styles')
    tap('스타일 닫기')
    tap('사진 비율 3:4')
    shot('camera-wide')
    tap('사진 비율 9:16')
    shot('camera-square')
    tap('사진 비율 1:1')
    tap('색감 조절', 'text', partial=True)
    shot('tune')


if __name__ == '__main__':
    main()
