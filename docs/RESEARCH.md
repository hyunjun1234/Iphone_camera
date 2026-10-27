# 색감 구현 방식과 카메라 기능의 범위

조사일: 2026-10-26. 아이폰 8 자료 추가: 2026-10-27. 아래에서 **공식 카메라 사양**과 **앱의 창작 프리셋 설계**를 구분합니다. 자료의 출처와 보정 수치의 관계는 [색감 근거 문서](COLOR_EVIDENCE.md)에 별도로 정리했습니다. 특정 아이폰이 항상 노랗거나 특정 색을 만든다고 단정할 수는 없습니다. 조명, 화이트밸런스, iOS 버전, 렌즈, 장면 인식과 사용자의 촬영 스타일이 결과에 영향을 줍니다.

## 1. 무엇이 모델별 차이를 만드는가

사진의 인상은 색온도만의 문제가 아닙니다. 센서와 렌즈, 자동 노출과 화이트밸런스, 여러 프레임을 합치는 HDR, 노이즈 제거, 선명도, 피부와 하늘 등을 구분하는 로컬 처리, 디스플레이까지 함께 작용합니다. Apple은 HDR을 서로 다른 노출의 여러 사진을 합쳐 밝고 어두운 영역의 정보를 남기는 처리로 설명합니다. 따라서 단일 JPEG용 LUT로 모든 효과를 동일하게 만들 수 없습니다. [Apple HDR 설명](https://support.apple.com/guide/iphone/adjust-hdr-camera-settings-iph2cafe2ebc/ios)

| 모델 | 공식 문서에서 확인한 차이 | 직접 정한 앱 스타일의 특징 |
| --- | --- | --- |
| iPhone 6s | 12MP, f/2.2, Auto HDR, 로컬 톤 매핑 | 따뜻한 흰색, 낮춘 채도, 살짝 떠 있는 검정 |
| iPhone 7 | f/1.8, 광학식 손떨림 보정, 넓은 색 영역 촬영, HDR | 은은한 골드 톤과 적당한 대비 |
| iPhone 8 | 12MP 광각, f/1.8, 광학식 손떨림 보정, 사진용 Auto HDR | 따뜻한 색, 은은한 대비, 부드러운 밝은 영역 |
| iPhone X | 듀얼 카메라, 듀얼 OIS, 사진용 Auto HDR | 조금 더 깊은 그림자와 또렷한 대비 |
| iPhone XS | Smart HDR, 로컬 톤 매핑 | 부드러운 하이라이트와 따뜻한 중간 톤. 최초 기본값 |
| iPhone 11 | Night mode, Deep Fusion, 차세대 Smart HDR | 낮은 온기, 열린 그림자, 균형 있는 채도 |
| iPhone 13 | Smart HDR 4, Deep Fusion, Photographic Styles | 비교적 높은 채도와 대비, 약간 차가운 온도 |
| iPhone 15 Pro | Photonic Engine, Smart HDR 5, 48MP 메인 카메라 | 중립적인 온도, 절제된 채도, 부드러운 밝은 영역 |
| iPhone 16 | 최신 세대 Photographic Styles, Smart HDR 5 | 밝은 중간 톤, 완만한 하이라이트, 미세한 온기 |

모델별 공식 출처: [6s](https://support.apple.com/en-us/111952), [7](https://support.apple.com/en-us/111943), [8](https://support.apple.com/en-us/111976), [X](https://support.apple.com/en-us/111864), [XS](https://support.apple.com/en-us/111881), [11](https://support.apple.com/en-us/111865), [13](https://support.apple.com/en-gb/111872), [15 Pro](https://support.apple.com/en-us/111829), [16](https://support.apple.com/en-us/121029).

Apple의 Photographic Styles는 단순한 화면 전체 필터와 다르게 장면의 일부 색상과 톤을 조절하고 지원 세대별로 기능도 다릅니다. 이번 구현은 그 비공개 알고리즘을 사용하거나 재현한다고 주장하지 않습니다. [Photographic Styles 사용 안내](https://support.apple.com/guide/iphone/use-photographic-styles-iph629d2cd37/ios)

## 2. 갤럭시 기능을 어디까지 유지할 수 있는가

CameraX를 사용하면 안드로이드의 카메라 하드웨어를 이용하면서 초점, 노출 보정, 플래시, 줌, 전후면 선택을 제어할 수 있습니다. 제조사가 Extensions를 제공하면 자동 보정, HDR, 야간 촬영, 인물 배경 흐림 등을 사용할 수 있습니다. 지원 범위는 기기와 렌즈에 따라 달라서 `isExtensionAvailable`로 매번 검사합니다. Extensions는 미리보기와 정지 사진용입니다. [CameraX 개요](https://developer.android.com/media/camera/camerax), [Camera Extensions](https://developer.android.com/media/camera/camera-extensions), [Extensions API 사용법](https://developer.android.com/media/camera/camerax/extensions-api)

앱에서는 후면/전면별로 제공된 모드와 줌 범위를 읽고 실제 지원되는 컨트롤만 활성화합니다. 0.5×가 카메라의 논리적 줌 범위에 없는 기기에는 0.5× 버튼을 만들지 않습니다. 숫자 줌이 항상 광학 렌즈 전환을 의미하지는 않습니다. 숨겨진 물리 렌즈나 특정 삼성 전용 API를 강제로 열지는 않습니다.

Expert RAW, 고화소 전용 모드, 삼성 카메라의 모든 렌즈와 후처리, 동영상 기능이 그대로 제공되는 것은 아닙니다. 해당 기능이 필요하면 삼성 기본 카메라로 촬영한 뒤 모먼트에서 사진을 가져와 스타일을 선택하고 저장하세요. 가져온 원본 파일은 수정하지 않습니다. 원본의 촬영 기능을 사용했다는 뜻이며, 모먼트의 SDR JPEG 출력이 RAW 데이터와 HDR 정보를 유지한다는 뜻은 아닙니다.

확장 모드와 GPU 미리보기 조합이 바인딩 단계에서 실패하면 일반 촬영으로 복구하고 사용자가 알 수 있도록 안내합니다. 바인딩 이후 기기 내부의 카메라 오류나 GPU 오류가 발생하면 오류 화면을 표시합니다. 실제 삼성 기기의 구현과 펌웨어별 검증은 별도로 필요합니다.

## 3. 색감 처리 구조

촬영에서는 제조사가 처리한 카메라 화면에 GPU 색상 변환을 적용해 미리보기를 보여 줍니다. 셔터를 누르면 JPG를 촬영하고, 방향을 정규화한 뒤 같은 변환표로 색감을 적용해 새 JPG를 저장합니다. 사용자가 원하면 색감 적용 전 파일도 별도로 보관합니다.

기존 사진은 선택한 파일만 복사해 읽고, sRGB 변환과 EXIF 방향 정규화를 거쳐 편집합니다. 기존 파일을 덮어쓰지 않습니다.

- `color/Look.kt`: 프리셋 데이터, 강도와 온도 및 톤, 33×33×33 LUT 생성과 삼선형 보간. 입력과 출력은 SDR sRGB 인코딩 값입니다.
- LUT는 채도, RGB 온도 보정, 감마, 대비, 검정 들기, 밝은 부분 압축을 조합합니다. 피부색 범위에 가까운 색에서는 채도 변화를 완화하는 휴리스틱을 씁니다. 얼굴 인식이나 피부 분할 모델은 아닙니다.
- `camera/LutSurfaceProcessor.kt`: 33×1089 RGBA 텍스처에 LUT를 펼치고 OpenGL ES로 미리보기 각 픽셀에 적용합니다. CameraX의 `SurfaceOutput.updateTransformMatrix`를 사용해 센서 방향과 뷰포트, 전면 반전을 처리합니다. [SurfaceProcessor](https://developer.android.com/reference/androidx/camera/core/SurfaceProcessor), [SurfaceOutput](https://developer.android.com/reference/androidx/camera/core/SurfaceOutput)
- `data/PhotoStore.kt`: 저장 사진은 같은 LUT를 CPU에서 보간합니다. 행 단위로 처리해 별도의 원본 크기 픽셀 배열을 피하고, EXIF 회전과 반전을 실제 픽셀 방향으로 정규화합니다. 시간과 렌즈, 노출 등 선택한 메타데이터를 복사하고 가짜 iPhone 기기명을 기록하지 않습니다.
- 촬영 시작 시 색감 설정과 LUT를 고정해 저장 중 설정 변화가 결과에 섞이지 않도록 합니다. JPEG 인코딩과 카메라의 미리보기/정지 사진 처리 차이 때문에 화면과 저장 사진의 모든 픽셀이 같지는 않습니다.
- 강도 0은 색상 변환을 끕니다. JPEG 재압축과 색공간 변환은 발생하므로 비트 단위 원본이 필요하면 별도 원본 파일을 사용해야 합니다.
- 출력은 JPEG 품질 96, SDR sRGB입니다. 상한은 최대 약 16MP이며 기기 메모리 한도가 작으면 더 낮아집니다. 이 상한을 넘는 입력은 디코딩 단계에서 2의 거듭제곱 비율로 축소합니다. 투명한 부분에는 흰 배경을 넣고 색감을 적용합니다. 편집본에는 GPS 위치 정보를 복사하지 않습니다. 고화소 원본 자체는 별도 저장 옵션 또는 가져온 기존 파일에 남습니다.

## 4. 실제 아이폰에 더 가깝게 만드는 다음 단계

현재 프리셋의 수치는 창작 초기값입니다. 상용 수준으로 모델 재현도를 평가하려면 다음 데이터가 필요합니다.

1. 대상 Galaxy와 각 iPhone으로 동일 장면을 촬영합니다. 실외 햇빛, 흐림, 노을, 실내 LED, 텅스텐, 혼합광, 저조도를 포함합니다. 화각과 노출, 기본 스타일, HDR 설정, iOS/One UI 버전을 기록합니다.
2. 중성 회색 및 컬러 차트를 포함하되 피부색, 하늘, 초록, 음식, 밝은 흰색 등 실제 장면도 확보합니다. 색과 밝기 정합이 된 이미지 쌍을 만듭니다.
3. Galaxy 기기와 렌즈, 조명별 기본 색차를 먼저 보정합니다. 그 뒤 아이폰 목표 톤 커브와 정규화된 3D LUT를 피팅합니다. 제한된 샘플에만 맞는 과도한 LUT를 피합니다.
4. 학습에 쓰지 않은 장면에서 색차, 밝기, 피부 톤과 주관적 선호도를 검증합니다. 시각적 인상과 수치 오차를 함께 평가합니다.
5. 노이즈와 선명도, 로컬 대비는 별도의 공간 처리 단계로 추가합니다. 단순히 채도를 낮추거나 전체에 블러를 주는 것으로 기기 특성이 재현되었다고 판단하지 않습니다.

이 순서는 앱 설계 제안이며, 실측 데이터로 검증된 재현 결과를 의미하지 않습니다.

## 5. 실제 기기 확인 항목

- Galaxy S/A/Z 시리즈별 후면과 전면 Extensions 지원과 실패 시 복구
- 기본/초광각/망원 노출 여부, 줌 경계에서의 전환, 플래시 지원과 셀피 반전
- HDR과 야간 촬영 지연, 흔들림, 촬영 중 백그라운드 전환
- 3:4 / 9:16 / 1:1 화면과 저장된 사진의 구도 일치
- 저메모리, 저장 공간 부족, 다른 앱의 카메라 사용, 권한 거절과 철회
- 사진 저장 후 갤러리 열기와 공유, 원본 보존, 색감 설정 유지
- 화면 밝기와 색상 모드를 고정한 실물 iPhone/Galaxy 비교

에뮬레이터는 화면, 권한, 일반 촬영, 저장, 색상 변환 검증에 사용할 수 있지만 삼성 Extensions와 실물 색 재현 검증을 대체하지 못합니다.
