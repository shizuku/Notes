package com.shizuku.tools.notes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shizuku.tools.notes.data.dao.NoteDao
import com.shizuku.tools.notes.data.entity.Note
import java.io.File

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun note(): NoteDao

    companion object {
        @JvmStatic
        fun get(applicationContext: Context): NotesDatabase {
            return Room.databaseBuilder(
                applicationContext,
                NotesDatabase::class.java,
                File(applicationContext.getExternalFilesDir(null), "notes.db").absolutePath
            ).build()
        }
    }
}
