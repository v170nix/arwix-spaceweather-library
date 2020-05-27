package net.arwix.spaceweather.library.data

import androidx.room.Database
import androidx.room.RoomDatabase
import net.arwix.spaceweather.library.geomagnetic.data.GeomagneticDao
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData
import net.arwix.spaceweather.library.radiation.data.ProtonDao
import net.arwix.spaceweather.library.radiation.data.ProtonData
import net.arwix.spaceweather.library.xray.data.XRayDao
import net.arwix.spaceweather.library.xray.data.XRayData
import net.arwix.spaceweather.library.xray.data.XRayFlareEventDao
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

@Database(
    entities = [
        KpIndexData::class,
        XRayData::class,
        XRayFlareEventData::class,
        ProtonData::class
    ],
    version = 312
)
abstract class SpaceWeatherDatabase : RoomDatabase() {
    abstract fun getGeomagneticDao(): GeomagneticDao
    abstract fun getXRayDao(): XRayDao
    abstract fun getXRayFlareEventDao(): XRayFlareEventDao
    abstract fun getProtonDao(): ProtonDao
}