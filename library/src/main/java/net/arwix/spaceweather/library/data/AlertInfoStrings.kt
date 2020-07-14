package net.arwix.spaceweather.library.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

class AlertInfoStrings(resources: Resources) {
    private val ref = resources.weak()

    private val infoStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_status_info)
    }

    fun getGeomagneticInfo(@IntRange(from = 0L, to = 9L) index: Int): String? {
        val innerIndex = (index - 3).takeIf { it > 0 } ?: 0
        return infoStrings?.getOrNull(innerIndex)
    }

    fun getRadiationInfo(@IntRange(from = 0L, to = 5L) index: Int): String? {
        return when (index) {
            0 -> infoStrings?.getOrNull(0)
            else -> infoStrings?.getOrNull(index + 1)
        }
    }

    fun getRadioBlackoutInfo(@IntRange(from = 0L, to = 5L) index: Int): String? {
        return when (index) {
            0 -> infoStrings?.getOrNull(0)
            else -> infoStrings?.getOrNull(index + 1)
        }
    }

}