package net.arwix.spaceweather.library.geomagnetic.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface GeomagneticDao {
    @Query("SELECT * FROM kp_index_flux_table ORDER BY time DESC LIMIT :count")
    fun getAllData(count: Int): Flow<List<KpIndexData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(list: List<KpIndexData>)

    @Query("DELETE FROM kp_index_flux_table")
    fun deleteAll()

    @Transaction
    fun deleteAndInserts(list: List<KpIndexData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged( count: Int) = getAllData(count).distinctUntilChanged()

}