package net.arwix.spaceweather.library.data

import kotlinx.serialization.Serializable
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData
import retrofit2.http.GET
import retrofit2.http.Query

interface SpaceWeatherApi {
    @GET("data/v3/kp_index/flux.json")
    suspend fun getKpIndexFlux(@Query("force") string: String): Map<Long, Double>

    @GET("data/v3/x_rays/flux.json")
    suspend fun getXRayFlux(@Query("force") string: String): XRayFluxData

    @GET("data/v3/proton/flux.json")
    suspend fun getProtonFlux(@Query("force") string: String): Map<Long, Double>

    @GET("data/v3/solar_wind/plasma.json")
    suspend fun getSolarWindPlasma(@Query("force") string: String): Map<Long, List<Double>>

    @GET("data/v3/hemi/power.json")
    suspend fun getHemisphericPower(@Query("force") string: String): Map<Long, List<Int>>

    @GET("data/v3/forecast/3day_data.json")
    suspend fun getForecast3Day(@Query("force") string: String): ForecastData

    @GET("swpc/goes/latest_metadata.json")
    suspend fun getXRayImageMetadata(@Query("force") string: String): XRayImageMetadata

    @GET("swpc/lasco/latest_metadata.json")
    suspend fun getLASCOImageMetadata(): LASCOImageMetadata

    @Serializable
    data class LASCOImageMetadata(val lasco2Time: Long, val lasco3Time: Long)

    @Serializable
    data class XRayImageMetadata(val pthna04: Long)

    @Serializable
    data class XRayFluxData(
        val lastEvent: XRayFlareEventData,
        val allData: Map<Long, Double>,
        val last6HoursData: Map<Long, Double>
    )

    @Serializable
    data class ForecastData(
        val radiation: List<ForecastRadiationData>,
        val x_ray: List<ForecastXRayData>,
        val geomagnetic: List<ForecastGeoData>
    )

    @Serializable
    data class ForecastRadiationData(val date: String, val percent: Byte)

    @Serializable
    data class ForecastXRayData(val date: String, val percent_m: Byte, val percent_x: Byte)

    @Serializable
    data class ForecastGeoData(
        val date: String,
        val percent_active: Byte,
        val percent_minor_storm: Byte,
        val percent_major_storm: Byte
    )
}