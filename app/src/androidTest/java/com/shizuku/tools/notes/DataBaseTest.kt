package com.shizuku.tools.notes

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shizuku.tools.notes.data.dao.NoteDao
import com.shizuku.tools.notes.data.database.NotesDatabase
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DataBaseTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: NotesDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, NotesDatabase::class.java
        ).build()
        noteDao = db.note()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val note = Note(12, 24, "asdfa", Note.markdown, 0)
        val newNote = Note(12, 24, "rewqrewqrewqrewqrewqrewqrewqrewq", Note.markdown, 0)
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.insert(note)
            noteDao.insert(newNote)
        }
        val byId = noteDao.getById(12)
        assertThat(byId, equalTo(newNote))
    }
}
