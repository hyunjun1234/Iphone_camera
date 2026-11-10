# 기종별 색감의 출처와 확인 범위

검토일: 2026-11-11

0.2.9부터 스타일 값은 색상 차트 사진을 측정해 계산한 값입니다. 같은 ColorChecker 차트를 찍은 갤럭시 7종의 평균과 아이폰 9종을 비교해, 갤럭시 사진을 각 아이폰의 색으로 옮기는 변화를 구했습니다. 애플이 공개한 값은 없고 기종마다 사진 한 장을 측정했으므로, 실제 아이폰 사진과 일치한다는 근거는 아닙니다. 조사한 자료와 자료별 한계는 [아이폰 기종별 색감 수치 조사](../reports/아이폰%20기종별%20색감%20수치%20조사.md)에 있습니다.

## 값을 다시 계산하기

```
python3 scripts/color/derive_looks.py --markdown
```

numpy와 scipy가 필요합니다. 터미널에 아래의 기종별 표와 `Look.kt`에 넣을 값이 출력됩니다. 입력은 `scripts/color/colorchecker.json`이며 16개 기기의 패치 색 측정값과 차트 사진 링크가 들어 있습니다.

## 계산 순서

기준은 갤럭시 S23, S24, S24 Ultra, S25, S25 Ultra, A55, A56 차트의 평균입니다. 이 평균에 스타일을 적용한 결과가 아이폰 차트와 같아지도록 값을 순서대로 구합니다.

| 순서 | 구하는 값 | 측정에 쓴 패치 | 맞추는 대상 |
| --- | --- | --- | --- |
| 1 | gamma, contrast, fade, highlight | 회색 6개 | 패치별 밝기(L*) |
| 2 | warmth, tint | 회색 20번부터 23번 | 선형 R/G와 B/G |
| 3 | saturation | 색 18개 | 화이트밸런스를 맞춘 뒤 밝기로 나눈 채도 |

차트 사진은 Notebookcheck 리뷰에 실린 파일이고, 패치 가운데의 색을 2026-11-10(아이폰)과 2026-11-11(갤럭시)에 읽었습니다.

## 측정값이 아닌 선택

| 항목 | 정한 값 | 이유 |
| --- | --- | --- |
| 아이폰 9종에 공통인 밝기 차이 | 절반만 반영 | 차트에서 아이폰의 중간 회색은 갤럭시 평균보다 L* 6.4 밝습니다. 그런데 갤럭시 7종의 중간 회색이 표준값 대비 -0.39EV(S25 Ultra)에서 +0.43EV(S23)까지 갈려, 어느 갤럭시와 비교하느냐에 따라 차이가 크게 달라집니다. 아이폰 기종 사이의 밝기 차이는 그대로 반영했습니다. |
| 스타일의 세기 | sqrt(12.5 / 변화량), 0.85배에서 1.6배 | 측정값 그대로면 7, 8, X는 원본과 차이가 작고 13, 15 Pro, 16은 큽니다. 변화량은 스타일이 갤럭시 차트에 만드는 평균 색차(CIE76)입니다. |
| 8의 밝기와 대비 | X의 차트 값 | 8의 차트는 EV100 6.8로, 촬영 정보가 남아 있는 다른 차트(8.7에서 10.6)보다 약 3스톱 어두운 곳에서 찍혔습니다. 8과 X는 2017년에 함께 나온 같은 세대입니다. X의 차트에는 촬영 정보가 없어 조도는 미확인입니다. |
| 13의 화이트밸런스 | 측정값과 기준 기종 평균의 중간 | 13의 차트는 촬영 환경이 다르고, 녹색 쪽 치우침이 10.5포인트로 다른 일곱 기종(3.0에서 5.3)의 2배 이상입니다. |
| 15 Pro | 밝기와 채도는 13과 16의 평균, 화이트밸런스는 기준 기종 평균 | 15 Pro의 차트는 초광각 렌즈(2.22mm)로 EV100 5.6에서 찍혀 기본 카메라의 색을 보여 주지 않습니다. |

기준 기종 평균은 7, 8, X, 11, 16의 회색 균형 평균입니다(갤럭시 평균 대비 R/G -0.8, B/G -6.3포인트).

## 기종별 값

측정 열은 갤럭시 평균과의 차이이고 세기를 곱하기 전의 값입니다. warmth부터 gamma까지는 `Look.kt`에 들어간 값입니다.

| 스타일 | 측정 R/G | 측정 B/G | 측정 채도 | 세기 | warmth | tint | saturation | contrast | fade | highlight | gamma | 중간 회색 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | +5.7 | -14.8 | 0.96 | 1.19 | 0.033 | -0.013 | 0.842 | 0.962 | 0.050 | -0.020 | 1.322 | 0.42 |
| 7 | -0.5 | -5.5 | 0.95 | 1.53 | 0.011 | -0.010 | 0.691 | 1.266 | 0.022 | -0.020 | 1.122 | 0.46 |
| 8 | -0.3 | -6.3 | 1.09 | 1.28 | 0.012 | -0.010 | 1.154 | 1.205 | 0.000 | -0.020 | 0.894 | 0.55 |
| X | +0.7 | -7.7 | 1.02 | 1.52 | 0.020 | -0.013 | 1.008 | 1.250 | 0.000 | -0.020 | 0.876 | 0.56 |
| XS | +6.9 | -17.4 | 1.11 | 1.17 | 0.044 | -0.017 | 1.245 | 1.216 | 0.020 | 0.040 | 0.905 | 0.54 |
| 11 | -2.6 | -4.6 | 1.02 | 1.28 | 0.005 | -0.013 | 1.351 | 1.031 | 0.000 | 0.040 | 0.790 | 0.58 |
| 13 | -4.8 | -9.3 | 1.21 | 0.87 | 0.008 | -0.015 | 1.366 | 1.305 | 0.000 | 0.005 | 0.844 | 0.57 |
| 15 Pro | -0.8 | -6.3 | 1.14 | 0.92 | 0.009 | -0.008 | 1.342 | 1.310 | 0.011 | 0.016 | 0.814 | 0.59 |
| 16 | -1.1 | -7.5 | 1.07 | 0.97 | 0.011 | -0.011 | 1.315 | 1.350 | 0.050 | 0.031 | 0.802 | 0.60 |

- R/G와 B/G는 회색의 선형 채널 비가 갤럭시 평균에서 벗어난 정도(포인트)입니다. B/G가 음수이면 회색이 더 노랗습니다.
- 측정 채도는 갤럭시 평균 대비 배율입니다.
- 중간 회색은 입력 0.5가 스타일을 거친 뒤의 값입니다. 0.5보다 작으면 사진이 어두워집니다.
- 13과 15 Pro의 측정 R/G, B/G는 위 표의 대체값입니다. 차트에서 읽은 값은 13이 -8.7과 -12.2, 15 Pro가 -7.5와 -23.5입니다.
- saturation이 측정 채도보다 큰 기종은 밝기를 올리면서 옅어지는 색을 그만큼 되돌린 것입니다. 7은 반대로 대비를 올리면 색이 진해져서 낮췄습니다.

## 사진에서 보이는 변화

원본과의 평균 색차(CIEDE2000)입니다. 256×256 크기의 샘플 6장(인물, 야간 발사대, 커피잔, 고양이, 에뮬레이터 실내 장면, 회색 그라데이션)에 앱과 같은 계산식을 Python으로 적용해 2026-11-11에 계산했습니다. 앱의 LUT 보간과 반올림은 포함하지 않았고, 이 계산 스크립트는 저장소에 없습니다.

| 스타일 | 0.2.8 | 0.2.9 |
| --- | --- | --- |
| 6s | 4.5 | 5.8 |
| 7 | 2.8 | 5.0 |
| 8 | 2.4 | 4.5 |
| X | 2.2 | 5.0 |
| XS | 3.7 | 6.4 |
| 11 | 1.9 | 7.2 |
| 13 | 1.7 | 6.7 |
| 15 Pro | 1.2 | 6.9 |
| 16 | 1.4 | 7.9 |

서로 가장 비슷한 스타일은 13과 15 Pro(1.3), 8과 X(1.5), 15 Pro와 16(1.6)입니다. 8은 X의 밝기 값을, 15 Pro는 13과 16의 평균을 쓰기 때문입니다.

저장소에서 다시 확인할 수 있는 기준은 단위 테스트 `ColorLutTest`입니다. ColorChecker 표준색 24개에서 모든 스타일의 평균 색차(CIE76)가 7 이상이고 스타일 사이가 1.8 이상인지 검사합니다. 0.2.8의 값은 9개 중 7개 스타일이 첫 기준에 못 미쳤습니다(Python 계산으로 2.4에서 4.9).

```
./gradlew testDebugUnitTest --tests "*ColorLutTest*"
```

결과는 `app/build/reports/tests/testDebugUnitTest/index.html`에 생깁니다.

## 근거 수준

| 항목 | 상태 |
| --- | --- |
| 아이폰의 회색이 갤럭시보다 노랗다 | 차트 9종 모두 B/G가 음수입니다. 국내 사용자 표현과 방향이 같습니다. 크기는 기종당 사진 한 장의 값입니다. |
| XS와 6s가 가장 따뜻하고 13의 채도가 가장 높다 | 차트의 순서이며 리뷰와 사용자 표현이 같은 방향입니다. 근거는 보고서 3절에 있습니다. |
| 밝기와 대비 | 가장 약합니다. 차트 조명이 공개되지 않았고 갤럭시 7종 사이의 차이가 큽니다. |
| 색상별 차이 | 반영하지 않았습니다. 일곱 값을 한꺼번에 맞춘 뒤 남는 차이를 보면 빨강 패치의 a*가 9종 모두 낮고(평균 -6.4), 보라 패치의 b*가 9종 모두 낮습니다(평균 -5.1). 앱의 계산식에 색상별 항이 없습니다. |
| 갤럭시 실기기 사진에 적용한 결과 | 미확인 |
| CameraX로 받은 사진의 색이 삼성 기본 카메라 앱과 같은지 | 미확인 |

재현 정확도를 주장하려면 대상 갤럭시와 해당 아이폰으로 같은 장면을 찍은 원본이 필요합니다. 수집과 검증 계획은 [조사 문서](RESEARCH.md#4-실제-아이폰에-더-가깝게-만드는-다음-단계)에 있습니다.

## 차트 사진의 사용 범위

차트 사진은 내려받아 패치 색을 읽는 데만 썼습니다. 저장소와 앱에는 사진을 넣지 않았고 측정한 숫자와 출처 링크만 있습니다.

## 기종별 링크

| 스타일 | 측정한 차트가 실린 리뷰 | 애플 공식 사양 | 촬영 리뷰 |
| --- | --- | --- | --- |
| 6s | [Notebookcheck 6s](https://www.notebookcheck.net/Apple-iPhone-6S-Smartphone-Review.151403.0.html) | [사양](https://support.apple.com/en-us/111952) | [DXOMARK 6s](https://www.dxomark.com/apple-iphone-6s-review-solid-and-compelling-performance/) |
| 7 | [Notebookcheck 7](https://www.notebookcheck.net/Apple-iPhone-7-Smartphone-Review.173851.0.html) | [사양](https://support.apple.com/en-us/111943) | [DXOMARK 7](https://www.dxomark.com/apple-iphone-7-camera-review-better-than-ever/) |
| 8 | [Notebookcheck 8](https://www.notebookcheck.net/Apple-iPhone-8-Smartphone-Review.250930.0.html) | [사양](https://support.apple.com/en-us/111976) | [DXOMARK 8](https://www.dxomark.com/apple-iphone-8-reviewed-a-solid-performance-upgrade-over-the-iphone-7/) |
| X | [Notebookcheck X](https://www.notebookcheck.net/Apple-iPhone-X-Smartphone-Review.264436.0.html) | [사양](https://support.apple.com/en-us/111864) | [DXOMARK X](https://www.dxomark.com/apple-iphone-x-top-performer-stills/) |
| XS | [Notebookcheck XS](https://www.notebookcheck.net/Apple-iPhone-XS-Smartphone-Review.333546.0.html) | [사양](https://support.apple.com/en-us/111881) | [DXOMARK XS Max](https://www.dxomark.com/apple-iphone-xs-max-review-flagship-imaging-power/) |
| 11 | [Notebookcheck 11](https://www.notebookcheck.net/Apple-iPhone-11-Smartphone-Review-More-than-just-an-affordable-Apple-smartphone.435087.0.html) | [사양](https://support.apple.com/en-us/111865) | [DXOMARK 11](https://www.dxomark.com/apple-iphone-11-camera-review/) |
| 13 | [Notebookcheck 13](https://www.notebookcheck.net/Apple-iPhone-13-Smartphone-Review-High-quality-Apple-phone-with-an-even-brighter-display.572153.0.html) | [사양](https://support.apple.com/en-gb/111872) | [DXOMARK 13](https://www.dxomark.com/apple-iphone-13-camera-review-reliable-imaging-companion/) |
| 15 Pro | [Notebookcheck 15 Pro](https://www.notebookcheck.net/Apple-iPhone-15-Pro-smartphone-in-review-High-end-phone-as-a-replacement-for-the-PlayStation.761070.0.html) (값에 쓰지 않음) | [사양](https://support.apple.com/en-us/111829) | [DXOMARK 15 Pro](https://www.dxomark.com/apple-iphone-15-pro-camera-test/) |
| 16 | [Notebookcheck 16](https://www.notebookcheck.net/Apple-iPhone-16-smartphone-review-More-innovations-than-one-would-think.908118.0.html) | [사양](https://support.apple.com/en-us/121029) | [DXOMARK 16](https://www.dxomark.com/apple-iphone-16-camera-test/) |

DXOMARK XS 리뷰의 시험 기기는 XS Max입니다. 갤럭시 7종의 차트 링크는 `scripts/color/colorchecker.json`의 `source` 항목에 있습니다.

## 공개 설정값 조사

갤럭시 사용자와 보정 작성자가 공개한 설정값은 [공개 색감 설정값 조사](PUBLIC_COLOR_RECIPES.md)에 있습니다. 입력 기기와 편집 앱이 서로 달라 앱의 값에는 쓰지 않았습니다.

화면의 스타일 설명은 앱이 사진에 적용하는 변화를 적은 문장입니다.
