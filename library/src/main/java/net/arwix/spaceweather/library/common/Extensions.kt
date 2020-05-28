package net.arwix.spaceweather.library.common

import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData

/**
 * reversed list
 */
fun List<KpIndexData>.chunkToBar(): List<KpIndexData> {
    val innerData = dropWhile { it.time % 10800 != 0L }
    val groups = innerData.chunked(36)
    return groups.asSequence().map { item ->
        KpIndexData(
            time = item.first().time,
            value = item.maxBy { it.value }!!.value
        )
    }.toList()
}