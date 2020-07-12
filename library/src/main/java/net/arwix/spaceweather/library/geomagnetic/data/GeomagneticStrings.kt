package net.arwix.spaceweather.library.geomagnetic.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

@Suppress("MemberVisibilityCanBePrivate", "unused")
class GeomagneticStrings(resources: Resources) {

    private val ref = resources.weak()

    private val powerStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_geomagnetic_effects_power_system)
    }

    private val spacecraftStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_geomagnetic_effects_spacecraft)
    }

    private val otherStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_geomagnetic_effects_other)
    }

    val powerTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_geomagnetic_power_system_title)
    }
    val spacecraftTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_geomagnetic_spacecraft_title)
    }
    val otherTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_geomagnetic_other_title)
    }

    fun getStormStrings(data: KpIndexData): GeomagneticStormStrings? =
        getStormStrings(data.getIntIndex())

    fun getStormStrings(@IntRange(from = 0L, to = 9L) kpIndex: Int): GeomagneticStormStrings? {
        val index = kpIndex - 5
        if (index < 0 || index > 4) return null
        return runCatching {
            GeomagneticStormStrings(
                powerStrings!![index],
                spacecraftStrings!![index],
                otherStrings!![index]
            )
        }.getOrNull()
    }

    data class GeomagneticStormStrings(
        val power: String,
        val spacecraft: String,
        val other: String
    )

}