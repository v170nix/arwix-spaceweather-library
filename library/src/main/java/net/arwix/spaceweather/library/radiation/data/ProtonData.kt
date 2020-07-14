package net.arwix.spaceweather.library.radiation.data

import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import net.arwix.spaceweather.library.data.WeatherSWPCData
import kotlin.math.log10

@Entity(tableName = "proton_flux_table")
data class ProtonData constructor(
    @PrimaryKey override val time: Long,
    override val value: Double
) : WeatherSWPCData {
    @Ignore
    fun getFloatIndex() = log10(value.toFloat()).takeIf { !it.isNaN() } ?: -1f

    @Ignore
    @IntRange(from = 0L, to = 5L)
    override fun getIntIndex() = (
            getFloatIndex().takeIf { it <= 5f } ?: 5f
            ).toInt().takeIf { it > -1 } ?: 0

}