package net.arwix.spaceweather.library.domain

import net.arwix.spaceweather.library.data.WeatherSWPCBarData
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData
import org.junit.Test

class WeatherAlertCheckerTest {

    @Test
    fun check() {
//        val previousAlertData = KpIndexData(1601920800, 4.333)
        val previousAlertData = KpIndexData(1601942400, 4.333)
        val currentData = KpIndexData(1601953200, 5.6667)
        val old3Data = KpIndexData(1601942400, 4.333)
        val array = arrayOf(
            WeatherSWPCBarData(currentData, currentData.copy(time = currentData.time + 144)),
            WeatherSWPCBarData(old3Data, old3Data.copy(time = old3Data.time + 14)))
        val r = check(previousAlertData, 4, array, false)
        println(r)
    }

    private fun check(
        previousAlertData: KpIndexData?,
        minAlertIndex: Int,
        dataArray: Array<WeatherSWPCBarData<KpIndexData>>,
        alertIfSameIndex: Boolean = false
    ): Boolean {
        val (maxBarData, maxDataInBar) = dataArray.take(2).maxByOrNull {
            it.barData.getIntIndex()
        } ?: return false

        if (maxBarData.getIntIndex() < minAlertIndex) return false

        if (previousAlertData == null) {
            return true
        }
        //  5(4a) 7; 1 6(5a);
        if (maxBarData.getIntIndex() > previousAlertData.getIntIndex()) {
            return true
        }

        val (currentData, old3Data) = dataArray.take(2)
        val currentIndex = currentData.barData.getIntIndex()
        val old3Index = currentData.barData.getIntIndex()

        // 5 5 5a; 4 5 5a; 5 4 5a; 3 4 5a; 5 3 5a; 4 3 7a
        if (previousAlertData.time < old3Data.barData.time) {
            // older minDeltaTime
            if (currentIndex > old3Index) {
                return true
            }
            if (currentIndex == old3Index) {
                return alertIfSameIndex
            }
            return true
        }
        // 5 5a; 4 5a; 3 5a; 5(5a) 5;
        if (previousAlertData.time < currentData.barData.time) {
            if (currentIndex == previousAlertData.getIntIndex()) {
                return alertIfSameIndex
            }
        }
        return false
    }
}