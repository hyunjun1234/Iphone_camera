package studio.moment.camera

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.color.Looks
import studio.moment.camera.data.PreferenceStore
import studio.moment.camera.data.Preferences
import studio.moment.camera.data.PhotoQuality

@RunWith(AndroidJUnit4::class)
class PreferenceStoreTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before fun clearPreferences() {
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun upgradeKeepsLegacyAdjustmentsWhenSwitchingStyles() {
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit()
            .putString("look", "8").putFloat("intensity", .4f).putFloat("warmth", .3f).putFloat("tone", -.2f).commit()
        val store = PreferenceStore(context)
        val old = store.read()
        assertEquals(GradeSettings("8", .4f, .3f, -.2f), old.grade)
        store.write(old)
        store.write(old.copy(grade = store.readGrade("xs")))
        assertEquals(GradeSettings("xs"), store.read().grade)
        assertEquals(old.grade, PreferenceStore(context).readGrade("8"))
    }

    @Test fun everyStyleRetainsItsOwnAdjustmentsAcrossStoreInstances() {
        val expected = Looks.all.mapIndexed { index, look ->
            GradeSettings(look.id, index / 10f, index / 10f - .5f, .5f - index / 10f)
        }
        expected.forEach { PreferenceStore(context).write(Preferences(grade = it)) }
        val restored = PreferenceStore(context)
        expected.forEach { assertEquals(it, restored.readGrade(it.lookId)) }
        assertEquals(expected.last(), restored.read().grade)
        // Resetting the active style must not overwrite another style's saved adjustment.
        restored.write(restored.read().copy(grade = expected.last().copy(warmth = 0f)))
        assertEquals(expected.first(), PreferenceStore(context).readGrade(expected.first().lookId))
    }

    @Test fun corruptSavedValuesUseSafeDefaultsAndRanges() {
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit().putString("look", "xs")
            .putFloat("grade.xs.intensity", Float.NaN).putFloat("grade.xs.warmth", 10f)
            .putFloat("grade.xs.tone", Float.NEGATIVE_INFINITY).commit()
        assertEquals(GradeSettings("xs", 1f, 1f, 0f), PreferenceStore(context).read().grade)
        assertEquals(GradeSettings("8"), PreferenceStore(context).readGrade("8"))
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit().putString("photoQuality", "unknown").commit()
        assertEquals(PhotoQuality.STANDARD, PreferenceStore(context).read().photoQuality)
    }

    @Test fun photoQualityPersistsAndOlderInstallsStartWithStandard() {
        assertEquals(PhotoQuality.STANDARD, PreferenceStore(context).read().photoQuality)
        PhotoQuality.entries.forEach { quality ->
            val store = PreferenceStore(context)
            store.write(store.read().copy(photoQuality = quality))
            assertEquals(quality, PreferenceStore(context).read().photoQuality)
        }
    }
}
