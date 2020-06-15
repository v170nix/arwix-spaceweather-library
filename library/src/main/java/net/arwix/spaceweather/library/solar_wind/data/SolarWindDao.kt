package net.arwix.spaceweather.library.solar_wind.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface SolarWindDao {
    @Query("SELECT * FROM solar_wind_table ORDER BY time DESC LIMIT :count")
    abstract fun getAllData(count: Int): Flow<List<SolarWindData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<SolarWindData>)

    @Query("DELETE FROM solar_wind_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInserts(list: List<SolarWindData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged(count: Int) = getAllData(count).distinctUntilChanged()
}