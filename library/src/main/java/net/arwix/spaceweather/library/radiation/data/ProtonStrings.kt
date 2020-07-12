package net.arwix.spaceweather.library.radiation.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ProtonStrings(resources: Resources) {

    private val ref = resources.weak()

    private val biologicalStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radiation_biological)
    }

    private val satelliteStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radiation_satellite_operations)
    }

    private val otherStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_radiation_other_system)
    }

    val biologicalTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_radiation_biological_title)
    }
    val satelliteTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_radiation_satellite_title)
    }
    val otherTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_geomagnetic_other_title)
    }

    fun getStormStrings(data: ProtonData): RadiationStormStrings? = getStormStrings(data.getIntIndex())

    fun getStormStrings(@IntRange(from = 0L, to = 5L) protonIndex: Int): RadiationStormStrings? {
        val index = protonIndex - 1
        if (index < 0 || index > 4) return null
        return runCatching {
            RadiationStormStrings(biologicalStrings!![index], satelliteStrings!![index], otherStrings!![index])
        }.getOrNull()
    }

    data class RadiationStormStrings(
        val biological: String,
        val satellite: String,
        val other: String
    )

}