package net.arwix.spaceweather.library.xray.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.xray.data.XRayData
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData
import net.arwix.spaceweather.library.xray.data.XRayRepository

open class XRayUseCase(private val repository: XRayRepository): WeatherUseCase<Pair<List<XRayData>, List<XRayData>>>(repository) {

    private val _dataFlareState = MutableStateFlow<XRayFlareEventData?>(null)
    val flareState
        get() = combine(_dataFlareState, updatingState) { data, updatingState ->
            WrappedLoadedData(data, updatingState)
        }

    override suspend fun init(): Job {
        return supervisorScope {
            launch { super.init() }
            val list = repository.getFlareFlow()
            _dataFlareState.value = list.first()
            repository.getFlareFlow()
                .onEach { _dataFlareState.value = it }
                .launchIn(this)
        }
    }
}