package com.shizuku.tools.notes.ui.fragment.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.shizuku.tools.notes.data.database.NotesDatabase
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.data.entity.NoteWithoutId
import com.shizuku.tools.notes.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(application: Application) : AndroidViewModel(application) {
    private var noteId: Long = 0L
    private val db = NotesDatabase.get(application)
    val text = MutableLiveData<String>()

    fun load(id: Long) {
        text.value = ""
        noteId = id
        CoroutineScope(Dispatchers.Main).launch {
            if (noteId == 0L) {
                withContext(Dispatchers.IO) {
                    val rowid = db.note().insert(NoteWithoutId(TimeUtils.getMills(), ""))
                    val i = db.note().getByRowid(rowid)
                    withContext(Dispatchers.Main) {
                        i?.let {
                            noteId = it.id
                        }
                    }
                }
            } else {
                withContext(Dispatchers.IO) {
                    val i = db.note().getById(noteId)
                    withContext(Dispatchers.Main) {
                        i?.let {
                            noteId = it.id
                            text.value = it.content
                        }
                    }
                }
            }
        }
    }

    fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            val s = text.value.toString()
            db.note().insert(Note(noteId, TimeUtils.getMills(), s))
        }
    }
}
