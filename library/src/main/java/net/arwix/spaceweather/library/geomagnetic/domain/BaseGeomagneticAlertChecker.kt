package net.arwix.spaceweather.library.geomagnetic.domain

import android.content.SharedPreferences
import net.arwix.spaceweather.library.common.chunkKpIndexToBar
import net.arwix.spaceweather.library.domain.AlertChecker
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData

abstract class BaseGeomagneticAlertChecker(
    private val preferences: SharedPreferences
) : AlertChecker<KpIndexData>() {

    override fun saveCurrentAlert(data: KpIndexData) {
        preferences.edit()
            .putLong("WeatherAlertChecker.kpIndex.time", data.time)
            .putFloat("WeatherAlertChecker.kpIndex.value", data.value.toFloat())
            .apply()
    }

    override fun getPreviousAlert(): KpIndexData? {
        return runCatching {
            val time =
                preferences.getLong("WeatherAlertChecker.kpIndex.time", -1).takeIf { it > -1 }!!
                    .toLong()
            val value =
                preferences.getFloat("WeatherAlertChecker.kpIndex.value", -1f).takeIf { it > 0f }!!
                    .toDouble()
            KpIndexData(time, value)
        }.getOrNull()
    }

    override fun copyData(data: KpIndexData, time: Long): KpIndexData? = data.copy(time)

    fun check(data: List<KpIndexData>) {
        val bars = data.chunkKpIndexToBar()
        val dataArray = bars.take(3)
        super.check(4, dataArray.toTypedArray(), 10800L)
    }
}