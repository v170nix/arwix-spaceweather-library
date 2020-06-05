package net.arwix.spaceweather.library.solar_wind.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import net.arwix.spaceweather.library.radiation.data.ProtonData

@Dao
interface SolarWindDao {
    @Query("SELECT * FROM solar_wind_plasma_table ORDER BY time DESC LIMIT :count")
    abstract fun getAllData(count: Int): Flow<List<SolarWindPlasmaData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<SolarWindPlasmaData>)

    @Query("DELETE FROM solar_wind_plasma_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInserts(list: List<SolarWindPlasmaData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged(count: Int) = getAllData(count).distinctUntilChanged()
}