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
            api.getSolarWindPlasma(q).map {
                SolarWindPlasmaData(it.key * 10L, it.value[0], it.value[1], it.value[2])
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

    fun getFlow(count: Int = 1440 * 3): Flow<List<SolarWindPlasmaData>> =
        solarWindDao.getAllDataDistinctUntilChanged(count).filter { it.isNotEmpty() }

}