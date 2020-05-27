package net.arwix.spaceweather.library.data

import androidx.room.Ignore

interface WeatherSWPCData {
    val time: Long
    val value: Double

    @Ignore
    fun getIntIndex(): Int
}