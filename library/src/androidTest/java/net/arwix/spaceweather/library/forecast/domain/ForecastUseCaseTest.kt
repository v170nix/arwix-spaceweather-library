package net.arwix.spaceweather.library.forecast.domain

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.retrofit2converter.asConverterFactory
import net.arwix.spaceweather.library.data.SpaceWeatherApi
import net.arwix.spaceweather.library.forecast.data.Forecast3DayData
import net.arwix.spaceweather.library.forecast.data.ForecastRepository
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit

@RunWith(AndroidJUnit4::class)
class ForecastUseCaseTest {

    private lateinit var forecastRepository: ForecastRepository
    private lateinit var api: SpaceWeatherApi
    private lateinit var forecastUseCase: ForecastUseCase

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref",
        Context.MODE_PRIVATE
    )

    @Before
    fun setUp() {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor {
                val url = it.request().url().toString()
                it.proceed(it.request())
            }
            .build().changeToGZipType()
        api = Retrofit.Builder()
            .baseUrl("https://storage.googleapis.com/sun-explorer.appspot.com/")
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .client(client)
            .build()
            .create(SpaceWeatherApi::class.java)
        val checker =  UpdateCheckerData(pref, "weather.v3.forecast3day.update_time")
        forecastRepository = ForecastRepository(api, pref, checker)
        forecastUseCase = ForecastUseCase(forecastRepository)
        pref.edit().clear().apply()
    }

    @Test
    fun update() {
        runBlocking {
            val initJob = Job()
            forecastUseCase.init(this + initJob)

            var data: WrappedLoadedData<Forecast3DayData>? = null
            val job = launch {
                data = forecastUseCase.state
                    .filter { it.value != null }
                    .first()
            }
            launch {
                forecastUseCase.update(true)
            }
            delay(1000)
            job.cancel()
            initJob.cancel()
            assertNotNull(data)
        }
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
}