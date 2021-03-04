package net.arwix.spaceweather.library.xray.domain

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
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
import net.arwix.spaceweather.library.data.SpaceWeatherDatabase
import net.arwix.spaceweather.library.xray.data.XRayData
import net.arwix.spaceweather.library.xray.data.XRayRepository
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class XRayUseCaseTest {

    private lateinit var xRayRepository: XRayRepository
    private val api: SpaceWeatherApi2 = createSpaceWeatherApi2()
    private lateinit var xRayUseCase: XRayUseCase

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref",
        Context.MODE_PRIVATE
    )

    @Before
    fun setUp() {
        val checker =  UpdateCheckerData(pref, "weather.v3.forecast3day.update_time")
        val db = Room
            .inMemoryDatabaseBuilder(context, SpaceWeatherDatabase::class.java).build()
        pref.edit().clear().apply()
        val updateCheckerData = UpdateCheckerData(pref, "weather.v3.x_ray.update_time")
        db.getXRayDao().deleteAll()
        db.getXRayFlareEventDao().deleteAll()
        xRayRepository = XRayRepository(api, db.getXRayDao(), db.getXRayFlareEventDao(), updateCheckerData)
        xRayUseCase = XRayUseCase(xRayRepository)
    }

    @Test
    fun update() {
        runBlocking {
            val initJob = Job()
            xRayUseCase.init(this + initJob)

            var data: WrappedLoadedData<Pair<List<XRayData>, List<XRayData>>>? = null
            val job = launch {
                data = xRayUseCase.state
                    .filter { it.value != null }
                    .first()
            //    assertThat( equalTo())
            }
            launch {
                xRayUseCase.update(true)
            }
            delay(5000)
            job.cancel()
            initJob.cancel()
            assertNotNull(data)
            Log.e("test", data.toString())
        }
    }

}