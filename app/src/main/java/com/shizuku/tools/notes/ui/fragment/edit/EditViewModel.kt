package com.shizuku.tools.notes.ui.fragment.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shizuku.tools.notes.data.database.NotesDatabase
import com.shizuku.tools.notes.data.entity.MutableNote
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.data.entity.NoteWithoutId
import com.shizuku.tools.notes.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditViewModel(app: Application) : AndroidViewModel(app) {
    private val _n = MutableLiveData<Note>()

    var note: MutableNote = MutableNote(0, "", 0, false, 0, 0)
    var isNew: Boolean = true
    var newToCid: Long = 0L

    private val db = NotesDatabase.get(app)
    private val noteDao = db.note()

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    data class EditTextData(val s: String, val slStart: Int, val slEnd: Int)

    private val _editText = MutableLiveData<EditTextData>()
    val editText: LiveData<EditTextData> = _editText

    fun load(noteId: Long, isNew: Boolean, cid: Long, cb: () -> Unit) {
        this.note.uid = noteId
        this.isNew = isNew
        this.newToCid = cid
        if (!this.isNew) {
            CoroutineScope(Dispatchers.IO).launch {
                val i = noteDao.getById(noteId)
                Log.d("get note", "$i")
                withContext(Dispatchers.Main) {
                    i?.let {
                        this@EditViewModel.note.copy(it)
                        init(it.content)
                    }
                    cb.invoke()
                }
            }
        } else {
            init("")
        }
    }

    private fun init(s: String) {
        _text.value = s
        _editText.value = EditTextData(s, s.length, s.length)
    }

    fun close() {
        this.note.uid = 0L
        isNew = true
        _text.value = ""
        _editText.value = EditTextData("", 0, 0)
        undoStack.clear()
        redoStack.clear()
    }

    private fun save() {
        val s = text.value.toString()
        if (isNew) {
            CoroutineScope(Dispatchers.IO).launch {
                val rowid = noteDao.insert(
                    NoteWithoutId(
                        s,
                        TimeUtils.getMills(),
                        false,
                        Note.markdown,
                        newToCid
                    )
                )
                val i = noteDao.getByRowid(rowid)
                Log.d("new note", "$i")
                withContext(Dispatchers.Main) {
                    i?.let {
                        this@EditViewModel.note.copy(i)
                        isNew = false
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                noteDao.update(
                    Note(
                        this@EditViewModel.note.uid,
                        s,
                        TimeUtils.getMills(),
                        false,
                        Note.markdown,
                        this@EditViewModel.note.collectionId
                    )
                )
            }
        }
    }

    fun moveCollection(cid: Long, cb: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val n = noteDao.getById(this@EditViewModel.note.uid)
            n?.let {
                val nn = Note(n.uid, n.content, n.time, n.pin, n.type, cid)
                noteDao.update(nn)
                this@EditViewModel.note.collectionId = cid
                withContext(Dispatchers.Main) {
                    cb.invoke()
                }
            }
        }
    }

    fun changePin(p: Boolean, cb: (r: Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val n = noteDao.getById(this@EditViewModel.note.uid)
            n?.let {
                val nn = Note(n.uid, n.content, n.time, p, n.type, n.collectionId)
                noteDao.update(nn)
                val nnn = noteDao.getById(this@EditViewModel.note.uid)
                nnn?.let {
                    this@EditViewModel.note.copy(it)
                    withContext(Dispatchers.Main) {
                        cb.invoke(it.pin)
                    }
                }
            }
        }
    }

    // Undo Redo
    private data class Change(val s: String, val start: Int, val count: Int, val after: Int)

    private val undoStack = Stack<Change>()
    private val redoStack = Stack<Change>()

    fun beforeModify(s: String, start: Int, count: Int, after: Int) {
        undoStack.push(Change(s, start, count, after))
    }

    fun onModify(s: String, start: Int, before: Int, count: Int) {
        redoStack.clear()
    }

    fun afterModify(s: String) {
        if (_text.value != s) {
            _text.value = s
            save()
        }
    }

    private fun applyChange(c: Change) {

    }

    fun undo() {
        val c = undoStack.pop()

        redoStack.push(c)
    }

    fun redo() {
        val c = redoStack.pop()

        undoStack.push(c)
    }
}
