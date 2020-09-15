package net.arwix.spaceweather.library.radiation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.arwix.extension.UpdatingState
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi
import net.arwix.spaceweather.library.data.SpaceWeatherRepository
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData

class ProtonRepository(
    private val api: SpaceWeatherApi,
    private val protonDao: ProtonDao,
    private val updateCheckerData: UpdateCheckerData
): SpaceWeatherRepository<List<ProtonData>> {

    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(5L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            api.getProtonFlux(q).map {
                ProtonData(it.key * 10L, it.value)
            }
        }.onSuccess {
            withContext(Dispatchers.IO) { protonDao.deleteAndInserts(it) }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    override fun getFlow(): Flow<List<ProtonData>> = protonDao.getAllDataDistinctUntilChanged(864).filter { it.isNotEmpty() }

    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }
}