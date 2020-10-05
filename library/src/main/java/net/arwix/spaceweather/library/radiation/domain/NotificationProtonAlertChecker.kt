package net.arwix.spaceweather.library.radiation.domain

import android.content.SharedPreferences
import net.arwix.spaceweather.library.common.chunkProtonDataToBar
import net.arwix.spaceweather.library.domain.WeatherAlertChecker
import net.arwix.spaceweather.library.domain.WeatherNotificationManager
import net.arwix.spaceweather.library.radiation.data.ProtonData

@Suppress("unused")
open class NotificationProtonAlertChecker(
    private val preferences: SharedPreferences,
    private val notificationManager: WeatherNotificationManager
) : WeatherAlertChecker<ProtonData>() {

    override fun saveCurrentAlert(data: ProtonData) {
        preferences.edit()
            .putLong("WeatherAlertChecker.protonData.time", data.time)
            .putFloat("WeatherAlertChecker.protonData.value", data.value.toFloat())
            .apply()
    }

    override fun getPreviousAlert(): ProtonData? {
        return runCatching {
            val time = preferences.getLong("WeatherAlertChecker.protonData.time", -1)
                .takeIf { it > -1 }!!
                .toLong()
            val value = preferences.getFloat("WeatherAlertChecker.protonData.value", -1f)
                .takeIf { it > 0f }!!
                .toDouble()
            ProtonData(time, value)
        }.getOrNull()
    }

    override fun copyData(data: ProtonData, time: Long): ProtonData? = data.copy(time = time)

    open fun check(data: List<ProtonData>) {
        val bars = data.asReversed().chunkProtonDataToBar()
        val dataArray = bars.takeLast(3).asReversed()
        super.check(0, dataArray.toTypedArray(), 10800L)
    }

    override fun alert(data: ProtonData) {
        notificationManager.doProtonNotify(data)
    }
}