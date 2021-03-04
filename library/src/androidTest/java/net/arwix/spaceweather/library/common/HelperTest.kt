package net.arwix.spaceweather.library.common

import net.arwix.spaceweather.library.data.SpaceWeatherApi2

//fun createSpaceWeatherApi(): SpaceWeatherApi {
//    val client = OkHttpClient.Builder().build().changeToGZipType()
//    return Retrofit.Builder()
//        .baseUrl("https://storage.googleapis.com/sun-explorer.appspot.com/")
//        .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
//        .client(client)
//        .build()
//        .create(SpaceWeatherApi::class.java)
//}

fun createSpaceWeatherApi2(): SpaceWeatherApi2 {
    return SpaceWeatherApi2
}

//private fun OkHttpClient.changeToGZipType(): OkHttpClient = this.newBuilder().addNetworkInterceptor {
//    val originalRequest = it.request()
//    val response = it.proceed(originalRequest)
//    if (response.headers().get("Content-Type") == "application/octet-stream") {
//        response.newBuilder()
//            .header("Content-Encoding", "gzip")
//            .addHeader("Content-Type", "application/json")
//            .body(response.body())
//            .build()
//    } else response
//}.build()