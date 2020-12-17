package com.shizuku.tools.notes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shizuku.tools.notes.data.dao.CollectionDao
import com.shizuku.tools.notes.data.dao.NoteDao
import com.shizuku.tools.notes.data.entity.*
import com.shizuku.tools.notes.data.entity.Collection
import java.io.File

@Database(
    entities = [
        Note::class,
        Collection::class,
    ], version = 2
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun note(): NoteDao
    abstract fun collection(): CollectionDao

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
