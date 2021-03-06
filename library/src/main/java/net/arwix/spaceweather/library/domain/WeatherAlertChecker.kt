package net.arwix.spaceweather.library.domain

import net.arwix.spaceweather.library.data.WeatherSWPCBarData
import net.arwix.spaceweather.library.data.WeatherSWPCData

abstract class WeatherAlertChecker<T : WeatherSWPCData> {

    protected abstract fun saveCurrentAlert(data: T)
    protected abstract fun getPreviousAlert(): T?
    protected abstract fun copyData(data: T, time: Long): T?
    protected abstract fun alert(data: T)

    /**
     * @param minAlertIndex
     * @param dataArray [0] current... [1] older
     * @param minDeltaTime is seconds
     */
    open fun check(
        minAlertIndex: Int,
        dataArray: Array<WeatherSWPCBarData<T>>,
        alertIfSameIndex: Boolean = false
    ): Boolean {
        val (maxBarData, maxDataInBar) = dataArray.take(2).maxByOrNull {
            it.barData.getIntIndex()
        } ?: return false

        if (maxBarData.getIntIndex() < minAlertIndex) return false

        val previousAlertData = getPreviousAlert()
        if (previousAlertData == null) {
            alert(maxDataInBar)
            saveCurrentAlert(maxBarData)
            return true
        }
        //  5(4a) 7; 1 6(5a);
        if (maxBarData.getIntIndex() > previousAlertData.getIntIndex()) {
            alert(maxDataInBar)
            saveCurrentAlert(maxBarData)
            return true
        }

        val (currentData, old3Data) = dataArray.take(2)
        val currentIndex = currentData.barData.getIntIndex()
        val old3Index = currentData.barData.getIntIndex()

        // 5 5 5a; 4 5 5a; 5 4 5a; 3 4 5a; 5 3 5a; 4 3 7a
        if (previousAlertData.time < old3Data.barData.time) {
            // older minDeltaTime
            if (currentIndex > old3Index) {
                alert(currentData.maxDataInBar)
                saveCurrentAlert(currentData.barData)
                return true
            }
            if (currentIndex == old3Index) {
                if (alertIfSameIndex) alert(currentData.maxDataInBar)
                saveCurrentAlert(currentData.barData)
                return alertIfSameIndex
            }
            alert(old3Data.maxDataInBar)
            saveCurrentAlert(old3Data.barData)
            return true
        }
        // 5 5a; 4 5a; 3 5a; 5(5a) 5;
        if (previousAlertData.time < currentData.barData.time) {
            if (currentIndex == previousAlertData.getIntIndex()) {
                if (alertIfSameIndex) alert(currentData.maxDataInBar)
                saveCurrentAlert(currentData.barData)
                return alertIfSameIndex
            }
        }
        return false
    }

}