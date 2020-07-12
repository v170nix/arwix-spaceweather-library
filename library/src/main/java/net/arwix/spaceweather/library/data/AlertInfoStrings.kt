package net.arwix.spaceweather.library.data

import android.content.res.Resources
import androidx.annotation.IntRange
import net.arwix.extension.weak
import net.arwix.spaceweather.library.R

class AlertInfoStrings(resources: Resources) {
    private val ref = resources.weak()

    private val infoStrings by lazy(LazyThreadSafetyMode.NONE) {
        ref.get()?.getStringArray(R.array.space_weather_status_info)
    }

    fun getInfo(@IntRange(from = 0L, to = 5) index: Int) = infoStrings?.getOrNull(index)

}