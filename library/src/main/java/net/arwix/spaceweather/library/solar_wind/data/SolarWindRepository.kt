package net.arwix.spaceweather.library.solar_wind.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.arwix.extension.UpdatingState
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import net.arwix.spaceweather.library.data.SpaceWeatherRepository

class SolarWindRepository(
    private val api: SpaceWeatherApi2,
    private val solarWindDao: SolarWindDao,
    private val updateCheckerData: UpdateCheckerData
): SpaceWeatherRepository<List<SolarWindData>> {
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
            if (it is CancellationException) throw it
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getFlow(count: Int = 1440 * 3): Flow<List<SolarWindData>> =
        solarWindDao.getAllDataDistinctUntilChanged(count).filter { it.isNotEmpty() }

    override fun getFlow(): Flow<List<SolarWindData>> = getFlow(1440 * 3)

    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }

}