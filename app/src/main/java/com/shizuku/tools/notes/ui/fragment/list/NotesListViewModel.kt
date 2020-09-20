package com.shizuku.tools.notes.ui.fragment.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shizuku.tools.notes.data.database.NotesDatabase
import com.shizuku.tools.notes.data.entity.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _n = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _n
    private val db = NotesDatabase.get(application)

    fun load() {
        CoroutineScope(Dispatchers.IO).launch {
            val v = db.note().getAll()
            withContext(Dispatchers.Main) {
                _n.value = v
            }
        }
    }
}
