package net.arwix.spaceweather.library.xray.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class XRayFlareEventDao {

    @Query("SELECT * FROM x_ray_flare_table ORDER BY beginTime DESC LIMIT :count")
    abstract fun getAllData(count: Int): Flow<List<XRayFlareEventData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(list: List<XRayFlareEventData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: XRayFlareEventData)

    @Query("DELETE FROM x_ray_flare_table")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInsert(data: XRayFlareEventData) {
        deleteAll()
        insert(data)
    }

    fun getAllDataDistinctUntilChanged(count: Int) = getAllData(count).distinctUntilChanged()


}