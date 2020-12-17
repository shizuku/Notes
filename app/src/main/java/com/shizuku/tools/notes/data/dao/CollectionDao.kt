package com.shizuku.tools.notes.data.dao

import androidx.room.*
import com.shizuku.tools.notes.data.entity.Collection
import com.shizuku.tools.notes.data.entity.CollectionWithoutId

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection")
    suspend fun getAll(): List<Collection>

    @Query("SELECT * FROM collection WHERE uid=:cid")
    suspend fun getById(cid: Long): Collection

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(c: Collection): Long

    @Insert(entity = Collection::class)
    suspend fun insert(c: CollectionWithoutId): Long

    @Update
    suspend fun update(c: Collection)

    @Delete
    suspend fun delete(vararg users: Collection)
}
