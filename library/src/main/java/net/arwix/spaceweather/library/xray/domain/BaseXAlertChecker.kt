package net.arwix.spaceweather.library.xray.domain

import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

abstract class BaseXAlertChecker(
    private val preferences: SharedPreferences,
    private val json: Json
) {

    fun saveCurrentAlert(data: XRayFlareEventData) {
        preferences.edit()
            .putString(
                "WeatherAlertChecker.x_ray.flare",
                json.stringify(XRayFlareEventData.serializer(), data)
            )
            .apply()
    }

    fun getPreviousAlert(): XRayFlareEventData? =
        runCatching {
            json.parse(
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

    abstract fun alert(data: XRayFlareEventData)

}