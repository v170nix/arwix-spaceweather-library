package net.arwix.spaceweather.library.hemispheric.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hemispheric_power_table")
data class HemisphericPowerData(
    @PrimaryKey val time: Long,
    val northern: Int,
    val southern: Int)