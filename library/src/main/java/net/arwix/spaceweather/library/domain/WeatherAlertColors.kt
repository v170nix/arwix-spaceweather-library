@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package net.arwix.spaceweather.library.domain

import androidx.annotation.ColorInt

abstract class WeatherAlertColors {

    enum class TypeColor { Quiet, Active, Minor, Major, Extreme }

    protected abstract val array: Array<Int>

    @ColorInt
    open fun getGeomagneticColor(index: Int): Int {
        return when (index) {
            4 -> this[TypeColor.Active]
            5 -> this[TypeColor.Minor]
            in 6..8 -> this[TypeColor.Major]
            9 -> this[TypeColor.Extreme]
            else -> this[TypeColor.Quiet]
        }
    }

    @ColorInt
    open fun getHemisphericColor(value: Int) = when (value) {
        in 20..50 -> this[TypeColor.Active]
        in 50..80 -> this[TypeColor.Minor]
        in 80..110 -> this[TypeColor.Minor]
        in 110..Int.MAX_VALUE -> this[TypeColor.Extreme]
        else -> this[TypeColor.Quiet]
    }

    @ColorInt
    open fun getSolarWindSpeedColor(speed: Double): Int =
        this[TypeColor.values()[getSolarWindSpeedIndex(speed)]]

    open fun getSolarWindSpeedIndex(speed: Double): Int = when (speed) {
        in 400.0..500.0 -> 1
        in 500.0..700.0 -> 2
        in 700.0..900.0 -> 3
        in 900.0..Double.POSITIVE_INFINITY -> 4
        else -> 0
    }

    @ColorInt
    open fun getSolarWindDensityColor(density: Double): Int =
        this[TypeColor.values()[getSolarWindDensityIndex(density)]]

    open fun getSolarWindDensityIndex(density: Double): Int = when (density) {
        in 10.0..20.0 -> 1
        in 20.0..40.0 -> 2
        in 40.0..60.0 -> 3
        in 60.0..Double.POSITIVE_INFINITY -> 4
        else -> 0
    }

    @ColorInt
    open fun getSolarWindTemperatureColor(temperature: Double): Int =
        this[TypeColor.values()[getSolarWindTemperatureIndex(temperature)]]

    open fun getSolarWindTemperatureIndex(temperature: Double): Int = when (temperature) {
        in 30000.0..300000.0 -> 1
        in 300000.0..3000000.0 -> 2
        in 3000000.0..Double.POSITIVE_INFINITY -> 3
        else -> 0
    }

    @ColorInt
    open fun getSolarWindBtColor(bt: Double): Int =
        this[TypeColor.values()[getSolarWindBtIndex(bt)]]

    open fun getSolarWindBtIndex(bt: Double): Int = when (bt) {
        in 5.0..10.0 -> 1
        in 10.0..20.0 -> 2
        in 20.0..30.0 -> 3
        in 30.0..Double.POSITIVE_INFINITY -> 4
        else -> 0
    }

    @ColorInt
    open fun getSolarWindBzColor(bz: Double): Int =
        this[TypeColor.values()[getSolarWindBzIndex(bz)]]

    open fun getSolarWindBzIndex(bz: Double): Int = when (bz) {
        in Double.NEGATIVE_INFINITY..-20.0 -> 4
        in -20.0..-10.0 -> 3
        in -10.0..-5.0 -> 2
        in -5.0..0.0 -> 1
        else -> 0
    }

    @ColorInt
    open fun getRadiationColor(index: Int): Int {
        return when (index) {
            1 -> this[TypeColor.Minor]
            2 -> this[TypeColor.Major]
            3 -> this[TypeColor.Major]
            4 -> this[TypeColor.Extreme]
            5 -> this[TypeColor.Extreme]
            else -> this[TypeColor.Quiet]
        }
    }

    @ColorInt
    open fun getXRayColor(index: Int): Int {
        return when (index) {
            1 -> this[TypeColor.Minor]
            2 -> this[TypeColor.Major]
            3 -> this[TypeColor.Major]
            4 -> this[TypeColor.Extreme]
            5 -> this[TypeColor.Extreme]
            else -> this[TypeColor.Quiet]
        }
    }

    @ColorInt
    open fun getFlareColor(flareClass: String): Int {
        val mainClass = flareClass.first().toString()
        val value = flareClass.substring(1).toDoubleOrNull() ?: 0.0
        return when (mainClass) {
            "M" -> if (value < 5.0) this[TypeColor.Minor] else this[TypeColor.Major]
            "X" -> if (value < 10.0) this[TypeColor.Major] else this[TypeColor.Extreme]
            else -> this[TypeColor.Quiet]
        }
    }

    @ColorInt
    operator fun get(typeColor: TypeColor): Int = when (typeColor) {
        TypeColor.Quiet -> array[0]
        TypeColor.Active -> array[1]
        TypeColor.Minor -> array[2]
        TypeColor.Major -> array[3]
        TypeColor.Extreme -> array[4]
    }
}