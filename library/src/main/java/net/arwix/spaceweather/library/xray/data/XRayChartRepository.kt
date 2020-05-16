package net.arwix.spaceweather.library.xray.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi
import net.arwix.spaceweather.library.xray.data.XRayData.Companion.MODE_1
import net.arwix.spaceweather.library.xray.data.XRayData.Companion.MODE_5


class XRayChartRepository(
    private val api: SpaceWeatherApi,
    private val dao: XRayDao,
    private val updateCheckerData: UpdateCheckerData
) {
    suspend fun update(force: Boolean): UpdateCheckerData.UpdateResult {
        if (!force && !updateCheckerData.isUpdateTime(1L * 60L)) return UpdateCheckerData.UpdateResult.IsNotUpdateTime
        runCatching {
            val q = if (force) createRandomString() else ""
            val result = api.getXRayFlux(q)
            result.last6HoursData.map { entry ->
                XRayData(entry.key * 10L, entry.value, MODE_1)
            } + result.allData.map { entry ->
                XRayData(entry.key * 10L, entry.value, MODE_5)
            }
        }.onSuccess {
            withContext(Dispatchers.IO) { dao.deleteAndInserts(it) }
            updateCheckerData.saveSuccessUpdateTime()
            return UpdateCheckerData.UpdateResult.Success(it)
        }.onFailure {
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    fun getCombineFlow(): Flow<Pair<List<XRayData>, List<XRayData>>> = combine(
        dao.getAllDataDistinctUntilChanged(MODE_1, 360).filter { it.isNotEmpty() },
        dao.getAllDataDistinctUntilChanged(MODE_5, 288 * 3).filter { it.isNotEmpty() }
    ) { a, b -> Pair(a, b) }


}