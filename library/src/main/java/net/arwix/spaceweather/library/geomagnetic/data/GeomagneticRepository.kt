package net.arwix.spaceweather.library.geomagnetic.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi

class GeomagneticRepository(
    private val api: SpaceWeatherApi,
    private val geoDao: GeomagneticDao,
    private val updateCheckerData: UpdateCheckerData
) {

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
            return UpdateCheckerData.UpdateResult.Failure(it)
        }
        throw IllegalStateException()
    }

    fun getFlow(): Flow<List<KpIndexData>> = geoDao.getAllDataDistinctUntilChanged(864).filter { it.isNotEmpty() }
}