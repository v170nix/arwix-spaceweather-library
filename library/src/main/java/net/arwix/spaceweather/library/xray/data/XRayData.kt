package net.arwix.spaceweather.library.xray.data

import androidx.room.Entity
import androidx.room.Ignore
import kotlin.math.log10


@Entity(tableName = "x_ray_flux_table", primaryKeys = ["time", "mode"])
data class XRayData(
    val time: Long,
    val value: Double,
    val mode: Int) {

    @Ignore
    fun getFloatIndex() = log10(value.toFloat()).takeIf { !it.isNaN() } ?: -9.5f

    @Ignore
    fun getIntIndex(): Int = getFloatIndex().let {
        if (it < R1) return@let 0
        if (it >= R5) return@let 5
        if (it < R2) return@let 1
        if (it < R3) return@let 2
        if (it < R4) return@let 3
        return@let 4
    }

    companion object {
        const val MODE_5 = 5
        const val MODE_1 = 1
        private const val R1 = -5f
        private const val R2 = -4.301029995664f // log10(5e-5)
        private const val R3 = -4f
        private const val R4 = -3f
        private const val R5 = -2.698970004336f // log10(2e-3)
    }

    //A1 = 10e-8
    //A10 = B1 = 1e-7
    //B10 = C1 = 1e-6
    //C10 = M1 = 1e-5 = R1
    //M5 = 5e-5 = R2
    //M10 = X1 = 1e-4 =R3
    //X10 = 1e-3 = R4
    //X20 = 2e-3 = R5

}