package net.arwix.spaceweather.library.xray.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.xray.data.XRayData
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData
import net.arwix.spaceweather.library.xray.data.XRayRepository

open class XRayUseCase(private val repository: XRayRepository) :
    WeatherUseCase<Pair<List<XRayData>, List<XRayData>>>(repository) {

    private val _dataFlareState = MutableStateFlow<XRayFlareEventData?>(null)
    val flareState
        get() = combine(_dataFlareState, updatingState) { data, updatingState ->
            WrappedLoadedData(data, updatingState)
        }

    override fun init(scope: CoroutineScope) {
        super.init(scope)
        val list = repository.getFlareFlow()
        scope.launch {
            val data = list.first()
            if (_dataFlareState.value == null) _dataFlareState.value = data
        }
        repository.getFlareFlow()
            .onEach { _dataFlareState.value = it }
            .launchIn(scope)
    }
}