package net.arwix.spaceweather.library.xray.domain

import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.domain.WeatherNotificationManager
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

open class NotificationXRayFlareChecker(
    private val preferences: SharedPreferences,
    private val notificationManager: WeatherNotificationManager
) {

    private fun saveCurrentAlert(data: XRayFlareEventData) {
        preferences.edit()
            .putString(
                "WeatherAlertChecker.x_ray.flare",
                Json.encodeToString(XRayFlareEventData.serializer(), data)
            )
            .apply()
    }

    private fun getPreviousAlert(): XRayFlareEventData? =
        runCatching {
            Json.decodeFromString(
                XRayFlareEventData.serializer(),
                preferences.getString("WeatherAlertChecker.x_ray.flare", null)!!
            )
        }.getOrNull()

    fun check(currentWeatherData: XRayFlareEventData) {
        val oldData = getPreviousAlert()
        if (oldData == null ||
            oldData.beginTime != currentWeatherData.beginTime ||
            oldData.maxClass != currentWeatherData.maxClass
        ) {
            alert(currentWeatherData)
            saveCurrentAlert(currentWeatherData)
            return
        }
        if (oldData.endTime != currentWeatherData.endTime) {
            saveCurrentAlert(currentWeatherData)
        }
    }

    open fun alert(data: XRayFlareEventData) {
        notificationManager.doFlareNotify(data)
    }

}