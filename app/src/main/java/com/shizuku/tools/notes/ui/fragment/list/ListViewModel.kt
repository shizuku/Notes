package com.shizuku.tools.notes.ui.fragment.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shizuku.tools.notes.data.database.NotesDatabase
import com.shizuku.tools.notes.data.entity.Collection
import com.shizuku.tools.notes.data.entity.CollectionWithoutId
import com.shizuku.tools.notes.data.entity.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel(app: Application) : AndroidViewModel(app) {
    private val db = NotesDatabase.get(app)
    private val collectionDao = db.collection()
    private val noteDao = db.note()

    private val _c = MutableLiveData<List<Collection>>()
    val collections: LiveData<List<Collection>> = _c

    private val _n = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _n

    init {
        loadCollections()
        loadNotes()
    }

    fun refresh() {
        loadCollections()
        loadNotes()
    }

    fun loadCollections() {
        CoroutineScope(Dispatchers.IO).launch {
            val v = collectionDao.getAll()
            withContext(Dispatchers.Main) {
                _c.value = v
            }
        }
    }

    fun loadNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val v = noteDao.getAll()
            withContext(Dispatchers.Main) {
                _n.value = v
            }
        }
    }

    fun newCollection(name: String) {
        val it = CollectionWithoutId(name)
        CoroutineScope(Dispatchers.IO).launch {
            collectionDao.insert(it)
            refresh()
        }
    }

    fun deleteCollection(cid: Long) {
        if (cid == 0L) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val v = noteDao.getByCollectionId(cid)
            for (i in v) {
                val newN = Note(i.uid, i.content, i.time, i.pin, i.type, 0)
                noteDao.update(newN)
            }
            collectionDao.delete(collectionDao.getById(cid))
            refresh()
        }
    }
}
