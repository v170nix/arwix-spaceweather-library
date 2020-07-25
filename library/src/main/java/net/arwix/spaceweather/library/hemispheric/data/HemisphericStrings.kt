package net.arwix.spaceweather.library.hemispheric.data

import android.content.res.Resources
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

@Suppress("MemberVisibilityCanBePrivate", "unused")
class HemisphericStrings(resources: Resources) {

    private val ref = resources.weak()

    val northPowerTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_aurora_power_north)
    }
    val southPowerTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_aurora_power_south)
    }
    val gigaWattTitle by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getString(R.string.space_weather_aurora_power_gw)
    }
}