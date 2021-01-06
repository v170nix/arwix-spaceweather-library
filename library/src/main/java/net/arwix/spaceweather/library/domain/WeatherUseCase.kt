package net.arwix.spaceweather.library.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.supervisorScope
import net.arwix.extension.UpdatingState
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.data.SpaceWeatherRepository

open class WeatherUseCase<T>(private val repository: SpaceWeatherRepository<T>) {

    private val _updatingState: MutableStateFlow<UpdatingState> =
        MutableStateFlow(UpdatingState.None)
    val updatingState get() = _updatingState

    private val _dataState = MutableStateFlow<T?>(null)

    val state: Flow<WrappedLoadedData<T>> =
        combine(_dataState, _updatingState) { data, updatingState ->
            WrappedLoadedData(data, updatingState)
        }.onStart {
            val list = repository.getData()
            if (_dataState.value == null) _dataState.value = list
        }.catch {
            emit(WrappedLoadedData(null, UpdatingState.ErrorLoading(it)))
        }

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
            _updatingState.value = it
        }.launchIn(this)
    }

}