package net.arwix.spaceweather.library.radiation.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class ProtonDao {

    @Query("SELECT * FROM proton_flux_table ORDER BY time DESC LIMIT :count")
    abstract fun getAllData(count: Int): Flow<List<ProtonData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<ProtonData>)

    @Query("DELETE FROM proton_flux_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInserts(list: List<ProtonData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged(count: Int) = getAllData(count).distinctUntilChanged()

}