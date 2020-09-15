package net.arwix.spaceweather.library.geomagnetic.domain

import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.geomagnetic.data.GeomagneticRepository
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData

open class GeomagneticUseCase(repository: GeomagneticRepository): WeatherUseCase<List<KpIndexData>>(repository)