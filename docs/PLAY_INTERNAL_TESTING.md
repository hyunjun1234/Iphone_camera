# Google Play 내부 테스트

업로드용 키로 서명한 AAB를 [GitHub Releases](https://github.com/hyunjun1234/Iphone_camera/releases/tag/v0.2.6)에서 받을 수 있습니다. GitHub에 파일을 올리는 작업과 Play Console에 내부 테스트 버전을 게시하는 작업은 별개입니다. Console 등록은 아래 순서대로 직접 진행합니다.

- [Play Console 업로드용 AAB 다운로드](https://github.com/hyunjun1234/Iphone_camera/releases/download/v0.2.6/moment-0.2.6-8.aab)

**App Bundle 업로드란에는 `.aab` 파일 하나만 선택합니다.** APK는 휴대폰에 직접 설치할 때 쓰는 파일입니다. 이번 GitHub 릴리스에는 Console 업로드용 AAB와 체크섬을 첨부했습니다.

## 이번 배포 파일

| 항목 | 값 |
| --- | --- |
| 앱 이름 | 모먼트 |
| 패키지 이름 | `studio.moment.camera` |
| 버전 | `0.2.6` |
| 버전 코드 | `8` |
| 대상 SDK | Android 16, API 36 |
| 배포 대상 | 내부 테스트 |
| Console에 업로드할 파일 | `app/build/outputs/play/moment-0.2.6-8.aab` |
| 직접 설치할 파일 | `app/build/outputs/play/moment-0.2.6-8.apk` |

이 경로들은 로컬 빌드 결과이며 Git에 포함하지 않습니다. 같은 폴더의 `release-info.json`에 파일별 SHA-256과 업로드 인증서 지문이 있습니다. `uploadedToGooglePlay: false`는 파일 생성만 완료했다는 뜻입니다. 이 값은 실제 Console 배포 상태를 조회한 결과가 아닙니다.

## 업로드 오류가 표시될 때

- **유효한 App Bundle을 업로드하세요:** 선택한 파일의 확장자가 `.aab`인지 확인합니다. APK 파일을 App Bundle 입력란에 함께 올리지 않습니다.
- **3 버전 코드는 이미 사용되었습니다:** Console에 이미 사용된 버전 코드입니다. 새로 업로드하려면 버전 코드를 높여 빌드해야 합니다. 이번 `moment-0.2.6-8.aab`에는 버전 코드 `8`이 들어 있습니다. 파일 이름이나 Console의 출시명만 바꿔서는 해결되지 않습니다.
- 버전 코드 3의 AAB가 이미 정상 등록돼 있고 같은 파일을 다시 쓰려는 경우에는 새 업로드 대신 **라이브러리에서 추가**로 선택할 수 있습니다.

[버전 코드 안내](https://developer.android.com/studio/publish/versioning), [버전 준비 및 출시 안내](https://support.google.com/googleplay/android-developer/answer/9859348?hl=ko)

## 다시 빌드하기

```sh
python3 scripts/prepare_play_release.py
```

스크립트는 서명이 준비되지 않으면 중단합니다. 서명한 AAB와 APK의 인증서가 일치하는지, 패키지 이름이 맞는지, 디버그 앱이 아닌지 확인한 뒤 위 폴더에 복사합니다. 이 명령 자체는 Google Play에 업로드하지 않습니다.

macOS에서는 Android Studio의 JDK와 `~/Library/Android/sdk`를 기본으로 찾습니다. 다른 환경에서는 `JAVA_HOME`과 `ANDROID_HOME`을 지정하세요.

새 앱을 처음 준비하는 다른 컴퓨터에서는 아래 명령으로 업로드 키를 만들 수 있습니다. 이미 Console에 등록된 앱을 업데이트할 때는 새 키를 만들지 않고 해당 앱에 등록된 업로드 키를 사용해야 합니다.

```sh
python3 scripts/prepare_play_release.py --create-upload-key
```

이번에 생성한 키는 `~/.config/moment-camera/upload-key.p12`, 서명 설정은 같은 폴더의 `release.properties`에 있습니다. 디렉터리 권한은 `700`, 두 파일의 권한은 `600`입니다. 기존 키와 설정은 덮어쓰지 않습니다. 이후 업데이트에 사용하므로 이 두 파일을 함께 안전한 곳에 백업하세요. 비밀번호와 개인키를 Git이나 대화에 붙여넣지 마세요.

기존 업로드 키를 사용할 때는 `storeFile`, `storePassword`, `keyAlias`, `keyPassword`를 담은 properties 파일을 프로젝트 밖에 두고 경로를 지정합니다. 상대 `storeFile` 경로는 properties 파일이 있는 폴더를 기준으로 해석합니다.

```sh
MOMENT_SIGNING_PROPERTIES="/absolute/path/release.properties" python3 scripts/prepare_play_release.py
```

Google Play는 업로드 키와 배포 시 사용하는 앱 서명 키를 구분합니다. 새 앱은 Console에서 Play 앱 서명을 설정해야 합니다. 이미 같은 패키지를 사용하는 앱이 있다면 해당 앱의 업로드 인증서와 일치하는지 먼저 확인하세요. [Android 앱 서명 안내](https://developer.android.com/studio/publish/app-signing)

## Console에 등록하기

내부 테스트는 앱 설정을 모두 마치기 전에도 사용할 수 있고, 최대 100명의 테스트 계정에 배포할 수 있습니다. [Google 내부 테스트 안내](https://support.google.com/googleplay/android-developer/answer/9845334?hl=ko)

1. Google Play 개발자 계정으로 Console에 로그인합니다. 새 앱이면 이름을 ‘모먼트’, 기본 언어를 한국어로 설정하고 앱 유형과 가격 등 등록 정보를 정합니다. 같은 패키지의 앱이 이미 있으면 그 앱을 엽니다.
2. **테스트 및 출시 → 테스트 → 내부 테스트**에서 새 버전을 만들고 Play 앱 서명을 설정합니다. `moment-0.2.6-8.aab`를 업로드합니다. 첫 업로드 후 패키지 이름은 고정되므로 `studio.moment.camera`가 맞는지 확인합니다.
3. 버전 이름은 `0.2.6 (8)`으로 입력하고, [한국어 출시 노트](../play/ko-KR/release-notes.txt)를 붙여넣습니다. Console의 오류가 있다면 해결한 뒤 내부 테스트 버전을 게시합니다.
4. **테스터** 탭에서 휴대폰의 Play 스토어에 로그인한 Google 계정을 테스트 목록에 추가하고 목록을 선택해 저장합니다. 기존 테스트 목록을 교체하지 않도록 주의합니다.
5. Console에 표시되는 테스트 참여 링크를 복사합니다. 같은 Google 계정으로 링크를 열어 테스트에 참여한 뒤 Play 스토어에서 설치합니다. 처음 게시하면 링크나 앱 표시가 반영될 때까지 시간이 걸릴 수 있습니다.

이전의 디버그 APK나 로컬 설치용 APK가 휴대폰에 있으면 Play 배포본과 서명이 달라 업데이트 설치가 되지 않을 수 있습니다. 이 경우 로컬 버전을 삭제한 뒤 Play 버전을 설치해야 하며 앱 설정은 초기화됩니다. 사용자 사진은 삭제 전에 갤러리에서 확인하세요.

## 확인한 항목

- 최적화된 릴리스 AAB와 APK 빌드 및 서명 검증
- AAB와 APK의 업로드 인증서 일치, 디버그 서명 사용 여부 확인
- Google `bundletool` 1.18.3으로 AAB 구조 검증
- AAB의 패키지 이름, 버전 코드 8, 버전 0.2.6, 대상 API 36 확인
- APK의 ZIP 정렬과 포함된 ARM64 및 x86_64 네이티브 라이브러리의 ELF 16KB 정렬 확인
- 서명 설정이 없으면 배포 준비 명령과 서명 필수 빌드가 중단되는지 확인
- 생성된 키와 비밀번호, 빌드 결과가 Git 추적 대상에서 제외되는지 확인

API 36은 현재 새 앱 등록 기준에 맞춥니다. [Google Play 대상 API 기준](https://support.google.com/googleplay/android-developer/answer/11926878?hl=ko) 16KB 검사는 파일 정렬을 확인한 것으로, 16KB 기기에서의 실행 시험을 대신하지 않습니다. [Android 16KB 지원 안내](https://developer.android.com/guide/practices/page-sizes)

실제 업로드를 자동으로 진행하려면 로그인된 Console에 접근할 수 있는 브라우저 연결이나 Google Play Developer API 권한이 필요합니다. API를 사용할 경우 앱의 테스트 배포에 필요한 권한만 부여하고, 자격증명 파일은 프로젝트 밖에 보관합니다. [Google Play Developer API 설정](https://developers.google.com/android-publisher/getting_started)
