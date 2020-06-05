package net.arwix.spaceweather.library.common

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
            value = item.maxBy { it.value }!!.value
        )
    }.toList()
}

fun List<ProtonData>.chunkProtonDataToBar(size: Int = 36): List<ProtonData> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(size)
    return groups.asSequence().map { item ->
        ProtonData(
            time = item.first().time,
            value = item.maxBy { it.value }!!.value
        )
    }.toList()
}