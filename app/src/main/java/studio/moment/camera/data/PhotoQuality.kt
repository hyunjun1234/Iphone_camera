package studio.moment.camera.data

enum class PhotoQuality(val label: String, val maxPixels: Int, val jpegQuality: Int, val description: String) {
    SMALL("용량 절약", 3_000_000, 85, "최대 300만 화소로 파일 크기를 줄여요."),
    // 4032x3024 is the 12MP picture the camera is asked for; a lower limit would resample every full 3:4 photo.
    STANDARD("표준", 4032 * 3024, 96, "최대 1,200만 화소로 화질과 용량을 균형 있게 유지해요."),
    HIGH("높음", 24_000_000, 100, "최대 2,400만 화소로 압축을 줄여 저장해요. 촬영과 저장에 시간이 더 걸릴 수 있어요."),
}
