package net.arwix.spaceweather.library.xray.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

@Suppress("unused", "MemberVisibilityCanBePrivate")
class XRayStrings(resources: Resources) {

    private val ref = resources.weak()

    private val navigationStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radio_blackout_navigation)
    }

    private val hfRadioStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radio_blackout_hf_radio)
    }

    val navigationTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_radio_blackout_navigation_title)
    }
    val hfRadioTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_radio_blackout_hf_radio_title)
    }

    fun getBlackoutStrings(data: XRayData): RadioBlackoutStrings?  = getBlackoutStrings(data.getIntIndex())

    fun getBlackoutStrings(@IntRange(from = 0L, to = 5L) xRayIndex: Int): RadioBlackoutStrings? {
        val index = xRayIndex - 1
        if (index < 0 || index > 4) return null
        return runCatching {
            RadioBlackoutStrings(
                navigationStrings!![index],
                hfRadioStrings!![index]
            )
        }.getOrNull()
    }

    data class RadioBlackoutStrings(
        val navigation: String,
        val hfRadio: String
    )

}