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
import kotlinx.coroutines.yield
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createSpaceWeatherApi2
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForecastRepositoryTest {

    private lateinit var forecastRepository: ForecastRepository
    private val api: SpaceWeatherApi2 = createSpaceWeatherApi2()

    // https://developer.android.com/training/testing/unit-testing/local-unit-tests
    // https://medium.com/swlh/kotlin-coroutines-in-android-unit-test-28ff280fc0d5

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref", MODE_PRIVATE)

    @Before
    fun setUp() {
        val checker =  UpdateCheckerData(pref, "weather.v3.forecast3day.update_time")
        forecastRepository = ForecastRepository(api, pref, checker)
        pref.edit().clear().apply()
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
}