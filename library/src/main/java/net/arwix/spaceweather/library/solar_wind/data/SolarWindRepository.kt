package net.arwix.spaceweather.library.solar_wind.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi

class SolarWindRepository(
    private val api: SpaceWeatherApi,
    private val solarWindDao: SolarWindDao,
    private val updateCheckerData: UpdateCheckerData
) {
    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(1L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            api.getSolarWindPlasma(q).map { (time, value) ->
                SolarWindData(
                    time * 10L, value[0], value[1], value[2],
                    value.getOrNull(3),
                    value.getOrNull(4),
                    value.getOrNull(5),
                    value.getOrNull(6)
                )
            }
        }.onSuccess {
            withContext(Dispatchers.IO) { solarWindDao.deleteAndInserts(it) }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    fun getFlow(count: Int = 1440 * 3): Flow<List<SolarWindData>> =
        solarWindDao.getAllDataDistinctUntilChanged(count).filter { it.isNotEmpty() }

}