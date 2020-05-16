package net.arwix.spaceweather.library.radiation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createRandomString
import net.arwix.spaceweather.library.data.SpaceWeatherApi

class ProtonRepository(
    private val api: SpaceWeatherApi,
    private val protonDao: ProtonDao,
    private val updateCheckerData: UpdateCheckerData
) {
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

    fun getFlow(): Flow<List<ProtonData>> = protonDao.getAllDataDistinctUntilChanged(864).filter { it.isNotEmpty() }

}