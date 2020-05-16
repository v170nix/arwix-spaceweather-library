package net.arwix.spaceweather.library.data

import android.content.SharedPreferences
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class WeatherForecastRepository(
    private val api: SpaceWeatherApi,
    private val preferences: SharedPreferences,
    private val updateCheckerData: UpdateCheckerData
) {

    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        val nextUpdateTime = ZonedDateTime
            .now(ZoneId.of("UTC"))
            .withHour(22)
            .withMinute(20).toEpochSecond()
        val successUpdateDate = updateCheckerData.getLastSuccessTime() ?: 0
        if (!force && (successUpdateDate + 86400L > nextUpdateTime)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            val result = WeatherForecast3DayData(api.getForecast3Day(q))
            saveData(preferences, result)
            result
        }.onSuccess {
            updateCheckerData.saveSuccessUpdateTime(nextUpdateTime)
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    fun getData() = Companion.getData(preferences)

    private companion object {
        private const val KEY_FORECAST_DATA = "weather.v3.forecast.data"

        private fun getData(preferences: SharedPreferences): WeatherForecast3DayData? = runCatching {
            WeatherForecast3DayData.fromJson(preferences.getString(KEY_FORECAST_DATA, null)!!)
        }.getOrNull()

        private fun saveData(preferences: SharedPreferences, data: WeatherForecast3DayData) {
            preferences.edit().putString(KEY_FORECAST_DATA, data.toJson()).apply()
        }


    }

}