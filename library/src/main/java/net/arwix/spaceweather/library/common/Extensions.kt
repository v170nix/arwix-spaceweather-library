package net.arwix.spaceweather.library.common

import net.arwix.spaceweather.library.data.WeatherSWPCBarData
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData
import net.arwix.spaceweather.library.radiation.data.ProtonData

/**
 * reversed list
 */
fun List<KpIndexData>.chunkKpIndexToBar(size: Int = 36): List<KpIndexData> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(size)
    return groups.asSequence().map { item ->
        KpIndexData(
            time = item.first().time,
            value = item.maxOf { it.value }
        )
    }.toList()
}

/**
 * reversed list
 */
fun List<KpIndexData>.chunkKpIndexToBarIncludeMaxTime(size: Int = 36): List<WeatherSWPCBarData<KpIndexData>> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(size)
    return groups.asSequence().map { item ->
        val maxItem = item.maxByOrNull { it.value }!!
        WeatherSWPCBarData(
            KpIndexData(
                time = item.first().time,
                value = maxItem.value
            ), maxItem
        )
    }.toList()
}

/**
 * reversed list
 */
fun List<ProtonData>.chunkProtonDataToBar(size: Int = 36): List<ProtonData> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(size)
    return groups.asSequence().map { item ->
        ProtonData(
            time = item.first().time,
            value = item.maxOf { it.value }
        )
    }.toList()
}

/**
 * reversed list
 */
fun List<ProtonData>.chunkProtonToBarIncludeMaxTime(size: Int = 36): List<WeatherSWPCBarData<ProtonData>> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(size)
    return groups.asSequence().map { item ->
        val maxItem = item.maxByOrNull { it.value }!!
        WeatherSWPCBarData(
            ProtonData(
                time = item.first().time,
                value = maxItem.value
            ), maxItem
        )
    }.toList()
}