package net.arwix.spaceweather.library.geomagnetic.data

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

class GeomagneticRepository(
    private val api: SpaceWeatherApi2,
    private val geoDao: GeomagneticDao,
    private val updateCheckerData: UpdateCheckerData
): SpaceWeatherRepository<List<KpIndexData>> {

    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(1L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            api.getKpIndexFlux(q).map {
                KpIndexData(it.key * 10L, it.value)
            }
        }.onSuccess {
            withContext(Dispatchers.IO) { geoDao.deleteAndInserts(it) }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            if (it is CancellationException) throw it
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }

    override fun getFlow(): Flow<List<KpIndexData>> = geoDao
        .getAllDataDistinctUntilChanged(864)
        .filter { it.isNotEmpty() }

}