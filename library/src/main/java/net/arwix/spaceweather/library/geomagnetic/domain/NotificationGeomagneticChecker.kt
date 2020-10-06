package net.arwix.spaceweather.library.geomagnetic.domain

import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.arwix.spaceweather.library.common.chunkKpIndexToBarIncludeMaxTime
import net.arwix.spaceweather.library.data.WeatherSWPCBarData
import net.arwix.spaceweather.library.domain.WeatherAlertChecker
import net.arwix.spaceweather.library.domain.WeatherNotificationManager
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData

@Suppress("unused")
open class NotificationGeomagneticChecker(
    private val preferences: SharedPreferences,
    private val notificationManager: WeatherNotificationManager
) : WeatherAlertChecker<KpIndexData>() {

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

    override fun copyData(data: KpIndexData, time: Long): KpIndexData? = data.copy(time = time)

    open fun check(data: List<KpIndexData>, alertIfSameIndex: Boolean = true) {
        val bars = data.asReversed().chunkKpIndexToBarIncludeMaxTime()
        val dataArray = bars.takeLast(3).asReversed()
        val logCheck = LogCheck(dataArray)
        val string = Json.encodeToString(serializer(), logCheck)
        preferences.edit().putString("WeatherLog.dataArray", string).commit()
        super.check(4, dataArray.toTypedArray(), alertIfSameIndex)
    }

    override fun alert(data: KpIndexData) {
        notificationManager.doGeomagneticNotify(data)
    }
}


@Serializable
data class LogCheck(val dataArray: List<WeatherSWPCBarData<KpIndexData>>)