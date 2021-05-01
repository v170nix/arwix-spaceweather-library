package net.arwix.spaceweather.library.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope
import net.arwix.extension.UpdatingState
import net.arwix.extension.safeLaunchIn
import net.arwix.spaceweather.library.data.SpaceWeatherRepository

open class WeatherUseCase<T>(private val repository: SpaceWeatherRepository<T>) {

    private val _updatingState: MutableStateFlow<UpdatingState> =
        MutableStateFlow(UpdatingState.None)
    val updatingState get() = _updatingState
    val state: Flow<T> = repository.getFlow()

    @Deprecated("not effect")
    open fun init(scope: CoroutineScope) {
//        scope.launch {
//            val list = repository.getData()
//            if (_dataState.value == null) _dataState.value = list
//        }
//        repository.getFlow()
//            .onEach { _dataState.value = it }
//            .launchIn(scope)
    }

    open suspend fun update(force: Boolean) = supervisorScope {
        repository.updateAsFlow(force).onEach {
            if (isActive) _updatingState.value = it
        }.safeLaunchIn(this)
    }

}