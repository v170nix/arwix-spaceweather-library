package net.arwix.spaceweather.library.hemispheric.domain

import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.hemispheric.data.HemisphericPowerData
import net.arwix.spaceweather.library.hemispheric.data.HemisphericRepository

open class HemisphericUseCase(repository: HemisphericRepository): WeatherUseCase<List<HemisphericPowerData>>(repository)