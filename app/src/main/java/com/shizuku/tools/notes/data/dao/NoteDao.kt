package com.shizuku.tools.notes.data.dao

import androidx.room.*
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.data.entity.NoteWithoutId

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    suspend fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE collectionId=:cid")
    fun getByCollectionId(cid: Long): List<Note>

    @Query("SELECT * FROM note WHERE uid=:uid")
    suspend fun getById(uid: Long): Note?

    @Query("SELECT * FROM note WHERE rowid=:rowid")
    suspend fun getByRowid(rowid: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Note): Long

    @Insert(entity = Note::class)
    suspend fun insert(item: NoteWithoutId): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(vararg users: Note)
}
