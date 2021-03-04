package net.arwix.spaceweather.library.forecast.domain

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import net.arwix.extension.WrappedLoadedData
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.common.createSpaceWeatherApi2
import net.arwix.spaceweather.library.data.SpaceWeatherApi2
import net.arwix.spaceweather.library.forecast.data.Forecast3DayData
import net.arwix.spaceweather.library.forecast.data.ForecastRepository
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForecastUseCaseTest {

    private lateinit var forecastRepository: ForecastRepository
    private val api2: SpaceWeatherApi2 = createSpaceWeatherApi2()
    private lateinit var forecastUseCase: ForecastUseCase

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref",
        Context.MODE_PRIVATE
    )

    @Before
    fun setUp() {
        val checker =  UpdateCheckerData(pref, "weather.v3.forecast3day.update_time")
        forecastRepository = ForecastRepository(api2, pref, checker)
        forecastUseCase = ForecastUseCase(forecastRepository)
        pref.edit().clear().apply()
    }

    @Test
    fun update() {
        val api2 = createSpaceWeatherApi2()
        runBlocking {
            val r = api2.getProtonFlux("2")
            Log.e("r", r.toString())
        }
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

}