package net.arwix.spaceweather.library.data

import androidx.annotation.FloatRange
import androidx.room.Ignore
import androidx.room.PrimaryKey

interface WeatherSWPCData {
    val time: Long
    val value: Double

    @Ignore
    fun getIntIndex(): Int
}