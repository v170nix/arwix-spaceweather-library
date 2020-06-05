package net.arwix.spaceweather.library.solar_wind.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Entity(tableName = "solar_wind_plasma_table")
data class SolarWindPlasmaData(
    @PrimaryKey val time: Long,
    val density: Double,
    val speed: Double,
    val temperature: Double)