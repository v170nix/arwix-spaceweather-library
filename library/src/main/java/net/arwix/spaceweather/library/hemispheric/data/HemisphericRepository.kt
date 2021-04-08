package net.arwix.spaceweather.library.hemispheric.data

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

class HemisphericRepository(
    private val api: SpaceWeatherApi2,
    private val hemisphericDao: HemisphericDao,
    private val updateCheckerData: UpdateCheckerData
): SpaceWeatherRepository<List<HemisphericPowerData>> {
    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(3L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            api.getHemisphericPower(q).map { (time, value) ->
                HemisphericPowerData(time * 10L, value[0], value[1])
            }
        }.onSuccess {
            withContext(Dispatchers.IO) { hemisphericDao.deleteAndInserts(it) }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            if (it is CancellationException) throw it
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    fun getFlow(count: Int): Flow<List<HemisphericPowerData>> =
        hemisphericDao.getAllDataDistinctUntilChanged(count).filter { it.isNotEmpty() }

    override fun getFlow(): Flow<List<HemisphericPowerData>> =
        hemisphericDao.getAllDataDistinctUntilChanged().filter { it.isNotEmpty() }

    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }

}