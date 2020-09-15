package net.arwix.spaceweather.library.radiation.domain

import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.radiation.data.ProtonData
import net.arwix.spaceweather.library.radiation.data.ProtonRepository

open class ProtonUseCase(repository: ProtonRepository): WeatherUseCase<List<ProtonData>>(repository)