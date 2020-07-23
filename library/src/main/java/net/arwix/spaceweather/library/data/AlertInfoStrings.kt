package net.arwix.spaceweather.library.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

class AlertInfoStrings(resources: Resources) {
    private val ref = resources.weak()

    private val infoGeoStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_geomagnetic_status_info)
    }

    private val infoRadiationStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radiation_status_info)
    }

    private val infoBlackoutStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radio_blackout_status_info)
    }

    fun getGeomagneticInfo(@IntRange(from = 0L, to = 9L) index: Int) =
        infoGeoStrings?.getOrNull(index)

    fun getRadiationInfo(@IntRange(from = 0L, to = 5L) index: Int) =
        infoRadiationStrings?.getOrNull(index)

    fun getRadioBlackoutInfo(@IntRange(from = 0L, to = 5L) index: Int) =
        infoBlackoutStrings?.getOrNull(index)

}