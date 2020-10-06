package net.arwix.spaceweather.library.data

import kotlinx.serialization.Serializable

@Serializable
data class WeatherSWPCBarData<T: WeatherSWPCData>(
    val barData: T,
    val maxDataInBar: T
)