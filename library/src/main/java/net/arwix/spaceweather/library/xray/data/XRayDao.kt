package net.arwix.spaceweather.library.xray.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class XRayDao {

    @Query("SELECT * FROM x_ray_flux_table WHERE mode == :mode ORDER BY time DESC LIMIT :count")
    abstract fun getAllData(mode: Int, count: Int): Flow<List<XRayData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<XRayData>)

    @Query("DELETE FROM x_ray_flux_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInserts(list: List<XRayData>) {
        deleteAll()
        inserts(list)
    }

    fun getAllDataDistinctUntilChanged(mode: Int, count: Int) = getAllData(mode, count).distinctUntilChanged()


}