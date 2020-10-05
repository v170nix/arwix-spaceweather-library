package net.arwix.spaceweather.library.common

import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.common.retrofit2converter.asConverterFactory
import net.arwix.spaceweather.library.data.SpaceWeatherApi
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

fun createSpaceWeatherApi(): SpaceWeatherApi {
    val client = OkHttpClient.Builder().build().changeToGZipType()
    return Retrofit.Builder()
        .baseUrl("https://storage.googleapis.com/sun-explorer.appspot.com/")
        .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
        .client(client)
        .build()
        .create(SpaceWeatherApi::class.java)
}

private fun OkHttpClient.changeToGZipType(): OkHttpClient = this.newBuilder().addNetworkInterceptor {
    val originalRequest = it.request()
    val response = it.proceed(originalRequest)
    if (response.headers().get("Content-Type") == "application/octet-stream") {
        response.newBuilder()
            .header("Content-Encoding", "gzip")
            .addHeader("Content-Type", "application/json")
            .body(response.body())
            .build()
    } else response
}.build()