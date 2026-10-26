package studio.moment.camera.data

import android.system.ErrnoException
import android.system.OsConstants
import android.util.Log
import studio.moment.camera.BuildConfig

class UserMessageException(val userMessage: String) : Exception(userMessage)

/** Technical exception messages may contain paths, URIs or untranslated implementation details. */
fun userMessage(error: Throwable, fallback: String): String {
    if (BuildConfig.DEBUG) Log.e("Moment", "Operation failed", error)
    val causes = generateSequence(error) { it.cause }.take(8).toList()
    causes.filterIsInstance<UserMessageException>().firstOrNull()?.let { return it.userMessage }
    if (causes.any { it is SecurityException }) return "사진 또는 카메라에 접근할 수 없어요. 앱 권한을 확인해 주세요."
    if (causes.any { it is ErrnoException && it.errno == OsConstants.ENOSPC }) return "저장 공간이 부족해요. 공간을 확보한 뒤 다시 시도해 주세요."
    if (causes.any { it is OutOfMemoryError }) return "사진을 처리할 메모리가 부족해요. 다른 앱을 닫거나 더 작은 사진을 선택해 주세요."
    return fallback
}
