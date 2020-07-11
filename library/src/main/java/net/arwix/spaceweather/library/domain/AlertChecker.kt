package net.arwix.spaceweather.library.domain

import net.arwix.spaceweather.library.data.WeatherSWPCData

abstract class AlertChecker<T: WeatherSWPCData> {

    protected abstract fun saveCurrentAlert(data: T)
    protected abstract fun getPreviousAlert(): T?
    protected abstract fun copyData(data: T, time: Long): T?
    protected abstract fun alert(data: T)

    /**
     * @param minAlertIndex
     * @param dataArray [0] current... [3] older
     * @param minDeltaTime is seconds
     */
    open fun check(
        minAlertIndex: Int,
        dataArray: Array<T>,
        minDeltaTime: Long
    ) {
        val maxElement = dataArray.maxBy { it.getIntIndex() }!!
        if (maxElement.getIntIndex() < minAlertIndex) return

        val (currentData, old3Data, old6Data) = dataArray
//        val old6Index = old6Data.getIntIndex()
        val old3Index = old3Data.getIntIndex()
        val currentIndex = currentData.getIntIndex()

        val previousAlertData = getPreviousAlert()
        if ((previousAlertData?.time ?: -1) < old6Data.time) {
            alert(maxElement)
            saveCurrentAlert(maxElement)
        } else if (previousAlertData != null) {
            if (maxElement.getIntIndex() > previousAlertData.getIntIndex()) {
                // 4 5 6
                alert(maxElement)
                saveCurrentAlert(maxElement)
            } else if (
                currentIndex > minAlertIndex &&
                old3Index < currentIndex &&
                currentData.time > previousAlertData.time + minDeltaTime
            ) {
                // 4 6 5
                alert(currentData)
                copyData(previousAlertData, maxElement.time)
                copyData(previousAlertData, maxElement.time)?.let(::saveCurrentAlert)
            }
        }
    }

}