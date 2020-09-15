package net.arwix.spaceweather.library.forecast.data

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.yield
import kotlinx.serialization.json.Json
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.retrofit2converter.asConverterFactory
import net.arwix.spaceweather.library.data.SpaceWeatherApi
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import kotlin.coroutines.CoroutineContext

@RunWith(AndroidJUnit4::class)
class ForecastRepositoryTest {

    private lateinit var forecastRepository: ForecastRepository
    private val networkContext: CoroutineContext = TestCoroutineDispatcher()
    private lateinit var api: SpaceWeatherApi

    // https://developer.android.com/training/testing/unit-testing/local-unit-tests
    // https://medium.com/swlh/kotlin-coroutines-in-android-unit-test-28ff280fc0d5

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref", MODE_PRIVATE)

    @Before
    fun setup() {
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
    }

    @Test
    fun update() {
        runBlocking {
            val job = launch {
                val data = forecastRepository.getFlow().firstOrNull()
                assertNotNull(data)
            }
            val firstData = forecastRepository.getData()
            assertNull(firstData)

            when (val result = forecastRepository.update(true)) {
                UpdateCheckerData.UpdateResult.IsNotUpdateTime -> throw IllegalStateException("UpdateResult.IsNotUpdateTime")

                is UpdateCheckerData.UpdateResult.Success<*> -> {
                    result as UpdateCheckerData.UpdateResult.Success<Forecast3DayData>
                    val data = forecastRepository.getData()!!
                    assertArrayEquals(data.geomagnetic, result.values.geomagnetic)
                    assertArrayEquals(data.radiation, result.values.radiation)
                    assertArrayEquals(data.xRay, result.values.xRay)
                    yield()
                    job.cancel()
                }
                is UpdateCheckerData.UpdateResult.Failure -> throw IllegalStateException("UpdateResult.Failure")
            }
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