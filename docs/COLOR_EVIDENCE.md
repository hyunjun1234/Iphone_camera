# 기종별 색감의 출처와 확인 범위

검토일: 2026-10-26

현재 앱의 색감 수치는 아이폰 사진을 실측해서 얻은 값이 아닙니다. 최초 구현에서는 애플 공식 사양으로 기종별 카메라 기능을 조사한 뒤, 온도와 채도, 대비 등을 직접 정했습니다. 이번 검토에서 DXOMARK의 촬영 리뷰를 추가로 확인했습니다. 이 리뷰가 기존 보정 수치의 출처였던 것처럼 설명하지 않으며, 이번 수정에서도 수치를 검증된 값으로 바꾸었다고 주장하지 않습니다.

## 자료별로 확인할 수 있는 것

| 자료 | 확인에 사용한 내용 | 확인할 수 없는 내용 |
| --- | --- | --- |
| 애플 공식 기술 사양 | 렌즈, 센서, HDR, Deep Fusion, Photographic Styles 지원 여부 | 모델별 고정 색온도나 갤럭시에 적용할 LUT 수치 |
| DXOMARK 촬영 리뷰 | 시험한 기기와 조명에서 관찰한 화이트밸런스, 채도, 노출 등의 경향 | 모든 조명과 렌즈에 통하는 색감, 이 앱의 재현 정확도 |
| 앱의 `Look.kt` | 직접 정한 프리셋 파라미터와 변환 계산 | 실제 아이폰에서 측정한 프로파일 |

## 기종별 링크

| 앱 스타일 | 애플 공식 사양 | 촬영 리뷰 | 해석할 때의 제한 |
| --- | --- | --- | --- |
| 6s | [아이폰 6s 사양](https://support.apple.com/en-us/111952) | [DXOMARK 6s](https://www.dxomark.com/apple-iphone-6s-review-solid-and-compelling-performance/) | 과거 시험 프로토콜의 리뷰입니다. 기사에 색상 음영 시험 정정 안내가 있어 단일 수치를 보정값으로 사용할 수 없습니다. |
| 7 | [아이폰 7 사양](https://support.apple.com/en-us/111943) | [DXOMARK 7](https://www.dxomark.com/apple-iphone-7-camera-review-better-than-ever/) | 리뷰의 평가와 앱의 금빛 색감 설정은 별개입니다. |
| X | [아이폰 X 사양](https://support.apple.com/en-us/111864) | [DXOMARK X](https://www.dxomark.com/apple-iphone-x-top-performer-stills/) | 과거 프로토콜과 갱신 이력을 확인해야 하며 세대 간 점수를 색감 차이로 환산할 수 없습니다. |
| XS | [아이폰 XS 사양](https://support.apple.com/en-us/111881) | [DXOMARK XS Max](https://www.dxomark.com/apple-iphone-xs-max-review-flagship-imaging-power/) | 시험 모델은 XS Max입니다. XS 실측 자료나 전면 카메라 리뷰로 취급하지 않습니다. |
| 11 | [아이폰 11 사양](https://support.apple.com/en-us/111865) | [DXOMARK 11](https://www.dxomark.com/apple-iphone-11-camera-review/) | 장면별 평가는 참고할 수 있지만 갤럭시와 같은 장면을 찍어 정합한 보정 데이터가 아닙니다. |
| 13 | [아이폰 13 사양](https://support.apple.com/en-gb/111872) | [DXOMARK 13](https://www.dxomark.com/apple-iphone-13-camera-review-reliable-imaging-companion/) | 다양한 조명의 색과 화이트밸런스를 긍정적으로 평가합니다. 항상 차가운 색이라는 근거는 아닙니다. |
| 15 Pro | [아이폰 15 Pro 사양](https://support.apple.com/en-us/111829) | [DXOMARK 15 Pro](https://www.dxomark.com/apple-iphone-15-pro-camera-test/) | 시험 프로토콜과 촬영 조건을 함께 봐야 합니다. 점수에서 온도나 채도 수치를 역산하지 않았습니다. |
| 16 | [아이폰 16 사양](https://support.apple.com/en-us/121029) | [DXOMARK 16](https://www.dxomark.com/apple-iphone-16-camera-test/) | 중립적인 화이트밸런스의 예시도 있습니다. 앱이 더하는 약한 온기는 직접 정한 디자인입니다. |

XS Max 리뷰에는 일부 밝은 실외와 실내 인공조명에서 약한 노란 기, 저조도에서 채도 감소가 관찰됐다고 나옵니다. 이를 모든 XS 사진에 나타나는 고정 특성으로 확대할 수 없습니다. 앱의 온도 `0.038`이나 채도 `0.91`은 이 리뷰가 제공한 값이 아닙니다. [XS Max 색상 평가](https://www.dxomark.com/apple-iphone-xs-max-review-flagship-imaging-power/)

공개 리뷰의 사진을 내려받아 LUT를 만들거나 학습에 사용하지 않았습니다. 사진 재사용 권한도 확보하지 않았으므로 앱에 해당 사진을 포함하지 않았습니다. 모델별 점수는 시험 프로토콜이 달라 직접 순위를 비교하지 않았습니다.

## 현재 프리셋을 설명하는 기준

화면의 설명은 실제 아이폰의 고정 특성이 아니라 앱이 사진에 적용하는 변화를 설명합니다. 예를 들어 6s 스타일의 “노란 기를 더하고 채도와 대비를 낮춘다”는 문장은 현재 필터의 동작 설명입니다. 기본 XS 스타일도 실측 정확도를 이유로 선택한 것이 아닙니다.

재현 정확도를 주장하려면 대상 갤럭시와 해당 아이폰으로 같은 장면을 찍은 원본, 조명과 촬영 설정 기록, 학습에 사용하지 않은 비교 장면이 필요합니다. 현재 저장소에는 그런 데이터가 없습니다. 수집과 검증 계획은 [조사 문서](RESEARCH.md#4-실제-아이폰에-더-가깝게-만드는-다음-단계)에 있습니다.
