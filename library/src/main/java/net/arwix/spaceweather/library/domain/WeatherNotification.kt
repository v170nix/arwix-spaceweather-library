package net.arwix.spaceweather.library.domain

import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData
import net.arwix.spaceweather.library.radiation.data.ProtonData
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

interface WeatherNotification {
    fun doGeomagneticNotify(kpIndexData: KpIndexData)
    fun doProtonNotify(protonData: ProtonData)
    fun doFlareNotify(flareData: XRayFlareEventData)
}