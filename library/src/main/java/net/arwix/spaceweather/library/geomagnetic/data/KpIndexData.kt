package net.arwix.spaceweather.library.geomagnetic.data

import androidx.annotation.FloatRange
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.math.roundToInt

@Entity(tableName = "kp_index_flux_table")
data class KpIndexData(
    @PrimaryKey val time: Long,
    @FloatRange(from = 0.0, to = 10.0) val value: Double
) {
    @Ignore
    fun getIntIndex(): Int = value.roundToInt().let {
        if (it < 0) 0 else if (it > 9) 9 else it
    }
}