package net.arwix.spaceweather.library.forecast.domain

import net.arwix.spaceweather.library.domain.WeatherUseCase
import net.arwix.spaceweather.library.forecast.data.Forecast3DayData
import net.arwix.spaceweather.library.forecast.data.ForecastRepository

open class ForecastUseCase(repository: ForecastRepository): WeatherUseCase<Forecast3DayData>(repository)