package net.arwix.spaceweather.library.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.supervisorScope
import net.arwix.extension.UpdatingState
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.data.SpaceWeatherRepository

open class WeatherUseCase<T>(private val repository: SpaceWeatherRepository<T>) {

    private val _updatingState: MutableStateFlow<UpdatingState> =
        MutableStateFlow(UpdatingState.None)
    val updatingState get() = _updatingState

    private val _dataState = MutableStateFlow<T?>(null)
    val state
        get() = combine(_dataState, _updatingState) { data, updatingState ->
            WrappedLoadedData(data, updatingState)
        }

    open suspend fun init() = supervisorScope {
        val list = repository.getData()
        _dataState.value = list
        repository.getFlow()
            .onEach { _dataState.value = it }
            .launchIn(this)
    }

    open suspend fun update(force: Boolean) = supervisorScope {
        repository.updateAsFlow(force).onEach {
            _updatingState.value = it
        }.launchIn(this)
    }

}