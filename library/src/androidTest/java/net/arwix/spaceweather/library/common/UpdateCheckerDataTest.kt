package net.arwix.spaceweather.library.common

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateCheckerDataTest {

    private lateinit var updateCheckerData: UpdateCheckerData
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val pref: SharedPreferences = context.getSharedPreferences("myPref",
        Context.MODE_PRIVATE
    )

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        pref.edit().clear().apply()
        updateCheckerData = UpdateCheckerData(pref, "weather.v3.geomagnetic.update_time")
    }

    @Test
    fun check() {
        var check = updateCheckerData.isUpdateTime(1L * 60L)
        assertTrue(check)
        updateCheckerData.saveSuccessUpdateTime()
        check = updateCheckerData.isUpdateTime(1L * 60L)
        assertFalse(check)
    }

}