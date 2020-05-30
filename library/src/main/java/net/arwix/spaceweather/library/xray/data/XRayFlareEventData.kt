package net.arwix.spaceweather.library.xray.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Entity(tableName = "x_ray_flare_table")
data class XRayFlareEventData(
    @PrimaryKey val beginTime: Long,
    val beginClass: String,
    val maxTime: Long?,
    val maxClass: String?,
    val endTime: Long?,
    val endClass: String?
)