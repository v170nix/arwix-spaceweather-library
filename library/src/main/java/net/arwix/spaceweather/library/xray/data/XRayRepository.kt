package net.arwix.spaceweather.library.xray.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import net.arwix.extension.UpdatingState
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import net.arwix.spaceweather.library.data.SpaceWeatherRepository
import net.arwix.spaceweather.library.xray.data.XRayData.Companion.MODE_1
import net.arwix.spaceweather.library.xray.data.XRayData.Companion.MODE_5


class XRayRepository(
    private val api: SpaceWeatherApi2,
    private val dao: XRayDao,
    private val flareDao: XRayFlareEventDao,
    private val updateCheckerData: UpdateCheckerData
) : SpaceWeatherRepository<Pair<List<XRayData>, List<XRayData>>> {
    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(1L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            val result = api.getXRayFlux(q)
            result.last6HoursData.map { entry ->
                XRayData(entry.key * 10L, entry.value, MODE_1)
            } + result.allData.map { entry ->
                XRayData(entry.key * 10L, entry.value, MODE_5)
            } to result.lastEvent
        }.onSuccess { (xRayData, eventData) ->
            withContext(Dispatchers.IO) {
                dao.deleteAndInserts(xRayData)
                flareDao.deleteAndInsert(eventData)
            }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(xRayData to eventData)
        }.onFailure {
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    override fun getFlow(): Flow<Pair<List<XRayData>, List<XRayData>>> = combine(
        dao.getAllDataDistinctUntilChanged(MODE_1, 360).filter { it.isNotEmpty() },
        dao.getAllDataDistinctUntilChanged(MODE_5, 288 * 3).filter { it.isNotEmpty() }
    ) { a, b -> Pair(a, b) }

    fun getFlareFlow(): Flow<XRayFlareEventData> = flareDao
        .getAllDataDistinctUntilChanged(1)
        .map { it.firstOrNull() }
        .filterNotNull()

    override fun updateAsFlow(force: Boolean) = flow {
        emit(UpdatingState.Loading)
        when (val result = update(force)) {
            UpdateCheckerData.UpdateResult.IsNotUpdateTime -> emit(UpdatingState.None)
            is UpdateCheckerData.UpdateResult.Failure -> emit(UpdatingState.ErrorLoading(result.throwable))
            is UpdateCheckerData.UpdateResult.Success<*> -> emit(UpdatingState.Complete)
        }
    }


}