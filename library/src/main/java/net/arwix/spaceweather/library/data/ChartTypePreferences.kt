package net.arwix.spaceweather.library.data

import android.content.SharedPreferences

enum class ChartTimeMode {
    Short, Medium, Long;
}

enum class ChartType {
    KpIndex, Proton, XRay;

    private fun getPreferenceKey(): String = when (this) {
        KpIndex -> "weather.pref.kp_index.chart_mode"
        Proton -> "weather.pref.proton.chart_mode"
        XRay -> "weather.pref.x_ray.chart_mode"
    }

    fun getChartTimeMode(preferences: SharedPreferences, default: ChartTimeMode): ChartTimeMode =
        runCatching {
            val ordinal = preferences.getInt(getPreferenceKey(), default.ordinal)
            ChartTimeMode.values()[ordinal]
        }.getOrDefault(default)

    fun saveChartTimeMode(preferences: SharedPreferences, chartMode: ChartTimeMode) {
        preferences.edit().putInt(getPreferenceKey(), chartMode.ordinal).apply()
    }
}

class ChartTypePreferences(
    private val preferences: SharedPreferences,
    private val chartType: ChartType,
    private val defaultTimeMode: ChartTimeMode
) {

    fun getTimeMode() = chartType.getChartTimeMode(preferences, defaultTimeMode)

    fun saveTimeMode(timeMode: ChartTimeMode) =
        chartType.saveChartTimeMode(preferences, timeMode)

}