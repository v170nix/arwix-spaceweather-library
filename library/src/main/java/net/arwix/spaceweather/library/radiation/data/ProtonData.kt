package net.arwix.spaceweather.library.radiation.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.math.log10

@Entity(tableName = "proton_flux_table")
data class ProtonData constructor(@PrimaryKey val time: Long, val value: Double) {

    @Ignore
    fun getFloatIndex() = log10(value.toFloat()).takeIf { !it.isNaN() } ?: -1f

    @Ignore
    fun getIntIndex() = (getFloatIndex().takeIf { it <=5f } ?: 5f).toInt()

}