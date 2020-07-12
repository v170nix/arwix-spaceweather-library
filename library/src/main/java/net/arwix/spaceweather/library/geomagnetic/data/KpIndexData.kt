package net.arwix.spaceweather.library.geomagnetic.data

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import net.arwix.spaceweather.library.data.WeatherSWPCData
import kotlin.math.roundToInt

@Entity(tableName = "kp_index_flux_table")
data class KpIndexData(
    @PrimaryKey override val time: Long,
    @FloatRange(from = 0.0, to = 10.0) override val value: Double
): WeatherSWPCData {

    @Ignore
    @IntRange(from = 0L, to = 9L)
    override fun getIntIndex(): Int = value.roundToInt().let {
        if (it < 0) 0 else if (it > 9) 9 else it
    }
}