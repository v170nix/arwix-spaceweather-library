package net.arwix.spaceweather.library.data

import androidx.annotation.Keep
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

object SpaceWeatherApi2 {

    private const val url = "https://storage.googleapis.com/sun-explorer.appspot.com/"

    private fun createClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
//            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
////                        Log.e("Ktor", message)
//                    }
//                }
//                level = LogLevel.ALL
//            }
        }
    }

    private suspend inline fun <reified T> get(path: String, force: String): T {
        return createClient().use {
            it.get(url + path) {
                parameter("force", force)
            }
        }
    }

    suspend fun getKpIndexFlux(string: String): Map<Long, Double> {
        return get("data/v3/kp_index/flux.json", string)
    }

    suspend fun getXRayFlux(string: String): XRayFluxData {
        return get("data/v3/x_rays/flux.json", string)
    }

    suspend fun getProtonFlux(string: String): Map<Long, Double> {
        return get("data/v3/proton/flux.json", string)
    }

    suspend fun getSolarWindPlasma(string: String): Map<Long, List<Double>> {
        return get("data/v3/solar_wind/plasma.json", string)
    }

    suspend fun getHemisphericPower(string: String): Map<Long, List<Int>> {
        return get("data/v3/hemi/power.json", string)
    }

    suspend fun getForecast3Day(string: String): ForecastData {
        return get("data/v3/forecast/3day_data.json", string)
    }

    suspend fun getXRayImageMetadata(string: String): XRayImageMetadata {
        return get("swpc/goes/latest_metadata.json", string)
    }

    suspend fun getLASCOImageMetadata(string: String): LASCOImageMetadata {
        return get("swpc/lasco/latest_metadata.json", string)
    }

    @Keep
    @Serializable
    data class LASCOImageMetadata(val lasco2Time: Long, val lasco3Time: Long)

    @Keep
    @Serializable
    data class XRayImageMetadata(val pthna04: Long)

    @Keep
    @Serializable
    data class XRayFluxData(
        val lastEvent: XRayFlareEventData,
        val allData: Map<Long, Double>,
        val last6HoursData: Map<Long, Double>
    )

    @Keep
    @Serializable
    data class ForecastData(
        val radiation: List<ForecastRadiationData>,
        val x_ray: List<ForecastXRayData>,
        val geomagnetic: List<ForecastGeoData>
    )

    @Keep
    @Serializable
    data class ForecastRadiationData(val date: String, val percent: Byte)

    @Keep
    @Serializable
    data class ForecastXRayData(val date: String, val percent_m: Byte, val percent_x: Byte)

    @Keep
    @Serializable
    data class ForecastGeoData(
        val date: String,
        val percent_active: Byte,
        val percent_minor_storm: Byte,
        val percent_major_storm: Byte
    )
}

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
