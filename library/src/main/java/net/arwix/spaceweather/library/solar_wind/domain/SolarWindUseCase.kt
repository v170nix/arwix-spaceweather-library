package net.arwix.spaceweather.library.solar_wind.domain

import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.solar_wind.data.SolarWindData
import net.arwix.spaceweather.library.solar_wind.data.SolarWindRepository

open class SolarWindUseCase(repository: SolarWindRepository): WeatherUseCase<List<SolarWindData>>(repository)