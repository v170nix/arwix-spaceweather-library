package net.arwix.spaceweather.library.forecast.data

import android.content.SharedPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import net.arwix.extension.UpdatingState
import net.arwix.extension.trySendBlocking
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import net.arwix.spaceweather.library.data.SpaceWeatherRepository
import java.time.ZoneId
import java.time.ZonedDateTime

class ForecastRepository(
    private val api: SpaceWeatherApi2,
    private val preferences: SharedPreferences,
    private val updateCheckerData: UpdateCheckerData
) : SpaceWeatherRepository<Forecast3DayData> {

    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        val nextUpdateTime = ZonedDateTime
            .now(ZoneId.of("UTC"))
            .withHour(22)
            .withMinute(20).toEpochSecond()
        val successUpdateDate = updateCheckerData.getLastSuccessTime() ?: 0
        if (!force && (successUpdateDate + 86400L > nextUpdateTime)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            val result = Forecast3DayData(api.getForecast3Day(q))
            saveData(preferences, result)
            result
        }.onSuccess {
            updateCheckerData.saveSuccessUpdateTime(nextUpdateTime)
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            if (it is CancellationException) throw it
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    override suspend fun getData() = getData(preferences)

    override fun getFlow(): Flow<Forecast3DayData> =
        callbackFlow {
            Companion.getData(preferences)?.run {
                this@callbackFlow.trySendBlocking(this)
            }

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == KEY_FORECAST_DATA) {
                    Companion.getData(preferences)?.run {
                        this@callbackFlow.trySendBlocking(this)
                    }
//                    Companion.getData(preferences)?.run(::sendBlocking)
                }
            }
            preferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }


    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }

    private companion object {
        private const val KEY_FORECAST_DATA = "weather.v3.forecast.data"

        private fun getData(preferences: SharedPreferences): Forecast3DayData? = runCatching {
            Forecast3DayData.fromJson(preferences.getString(KEY_FORECAST_DATA, null)!!)
        }.getOrNull()

        private fun saveData(preferences: SharedPreferences, data: Forecast3DayData) {
            preferences.edit().putString(KEY_FORECAST_DATA, data.toJson()).apply()
        }


    }

}