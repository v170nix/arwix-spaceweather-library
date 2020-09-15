package net.arwix.spaceweather.library.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import net.arwix.extension.UpdatingState

interface SpaceWeatherRepository<R> {
    suspend fun getData(): R = getFlow().first()
    fun getFlow(): Flow<R>
    fun updateAsFlow(force: Boolean): Flow<UpdatingState>
}