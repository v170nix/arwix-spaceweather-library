package net.arwix.spaceweather.library.hemispheric.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface HemisphericDao {
    @Query("SELECT * FROM hemispheric_power_table ORDER BY time DESC LIMIT :count")
    abstract fun getAllData(count: Int): Flow<List<HemisphericPowerData>>

    @Query("SELECT * FROM hemispheric_power_table ORDER BY time DESC")
    abstract fun getAllData(): Flow<List<HemisphericPowerData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<HemisphericPowerData>)

    @Query("DELETE FROM hemispheric_power_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInserts(list: List<HemisphericPowerData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged(count: Int) = getAllData(count).distinctUntilChanged()
    fun getAllDataDistinctUntilChanged() = getAllData().distinctUntilChanged()
}