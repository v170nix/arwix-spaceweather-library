package net.arwix.spaceweather.library.forecast.data

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import java.text.SimpleDateFormat
import java.util.*

@Keep
@Serializable
class Forecast3DayData constructor(
    val geomagnetic: Array<ForecastItem.GeoItem>,
    val radiation: Array<ForecastItem.RadiationItem>,
    val xRay: Array<ForecastItem.XRayItem>
) {
    constructor(data: SpaceWeatherApi2.ForecastData) : this(
        data.geomagnetic.map {
            ForecastItem.GeoItem(
                stringDateToTime(it.date),
                it.percent_active,
                it.percent_minor_storm,
                it.percent_major_storm
            )
        }.toTypedArray(),
        data.radiation.map {
            ForecastItem.RadiationItem(stringDateToTime(it.date), it.percent)
        }.toTypedArray(),
        data.x_ray.map {
            ForecastItem.XRayItem(stringDateToTime(it.date), it.percent_m, it.percent_x)
        }.toTypedArray())

    fun toJson(): String {
        return json.encodeToString(serializer(), this)
    }

    companion object {
        private val formatter by lazy(LazyThreadSafetyMode.NONE) { SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH) }
        private val json by lazy(LazyThreadSafetyMode.NONE) { Json }

        private fun stringDateToTime(date: String): Long {
            val keys = date.split(" ").map { it.trim() }.filter { it.isNotEmpty() }
            return formatter.parse("${keys[0]} ${keys[1]} ${keys[2]}")!!.time
        }

        fun fromJson(string: String): Forecast3DayData = json.decodeFromString(serializer(), string) // gson.fromJson(string, WeatherForecast3DayData::class.java)
    }
}

@Keep
@Serializable
sealed class ForecastItem(@SerialName("item_time")open val time: Long) {
    @Keep @Serializable data class RadiationItem(override val time: Long, val percent: Byte): ForecastItem(time)
    @Keep @Serializable data class XRayItem(override val time: Long, val mPercent: Byte, val xPercent: Byte): ForecastItem(time)
    @Keep @Serializable data class GeoItem(
        override val time: Long,
        val activePercent: Byte,
        val minorPercent: Byte,
        val majorPercent: Byte
    ): ForecastItem(time)
}