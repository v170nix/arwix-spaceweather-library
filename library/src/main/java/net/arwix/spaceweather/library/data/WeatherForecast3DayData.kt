package net.arwix.spaceweather.library.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.text.SimpleDateFormat
import java.util.*

@Serializable
class WeatherForecast3DayData constructor(
    val geomagnetic: Array<WeatherForecastItem.GeoItem>,
    val radiation: Array<WeatherForecastItem.RadiationItem>,
    val xRay: Array<WeatherForecastItem.XRayItem>
) {
    constructor(data: SpaceWeatherApi.ForecastData) : this(
        data.geomagnetic.map {
            WeatherForecastItem.GeoItem(stringDateToTime(it.date), it.percent_active, it.percent_minor_storm, it.percent_major_storm)
        }.toTypedArray(),
        data.radiation.map {
            WeatherForecastItem.RadiationItem(stringDateToTime(it.date), it.percent)
        }.toTypedArray(),
        data.x_ray.map {
            WeatherForecastItem.XRayItem(stringDateToTime(it.date), it.percent_m, it.percent_x)
        }.toTypedArray())

    fun toJson(): String {
        return json.stringify(serializer(), this)
    }

    companion object {
        private val formatter by lazy(LazyThreadSafetyMode.NONE) { SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH) }
        private val json by lazy(LazyThreadSafetyMode.NONE) { Json(JsonConfiguration.Stable) }

        private fun stringDateToTime(date: String): Long {
            val keys = date.split(" ").map { it.trim() }.filter { it.isNotEmpty() }
            return formatter.parse("${keys[0]} ${keys[1]} ${keys[2]}")!!.time
        }

        fun fromJson(string: String): WeatherForecast3DayData = json.parse(serializer(), string) // gson.fromJson(string, WeatherForecast3DayData::class.java)
    }
}

@Serializable
sealed class WeatherForecastItem(@SerialName("item_time")open val time: Long) {
    @Serializable data class RadiationItem(override val time: Long, val percent: Byte): WeatherForecastItem(time)
    @Serializable data class XRayItem(override val time: Long, val mPercent: Byte, val xPercent: Byte): WeatherForecastItem(time)
    @Serializable data class GeoItem(
        override val time: Long,
        val activePercent: Byte,
        val minorPercent: Byte,
        val majorPercent: Byte
    ): WeatherForecastItem(time)
}