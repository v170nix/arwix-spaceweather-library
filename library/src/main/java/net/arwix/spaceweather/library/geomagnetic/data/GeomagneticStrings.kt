package net.arwix.spaceweather.library.geomagnetic.data

import android.content.res.Resources
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

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

    fun getStormStrings(kpIndex: KpIndexData): GeomagneticStormStrings? {
        val index = kpIndex.getIntIndex() - 5
        if (index < 0 || index > 4) return null
        return runCatching {
            GeomagneticStormStrings(powerStrings!![index], spacecraftStrings!![index], otherStrings!![index])
        }.getOrNull()
    }

    data class GeomagneticStormStrings(
        val power: String,
        val spacecraft: String,
        val other: String
    )

}