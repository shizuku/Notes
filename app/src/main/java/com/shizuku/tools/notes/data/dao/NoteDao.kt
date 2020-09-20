package com.shizuku.tools.notes.data.dao

import androidx.room.*
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.data.entity.NoteWithoutId

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id=:id")
    fun getById(id: Long): Note?

    @Query("SELECT * FROM note WHERE rowid=:rowid")
    fun getByRowid(rowid: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Note): Long

    @Insert(entity = Note::class)
    fun insert(item: NoteWithoutId): Long

    @Update
    fun update(note: Note)
}
