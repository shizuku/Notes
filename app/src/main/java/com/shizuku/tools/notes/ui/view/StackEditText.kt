package com.shizuku.tools.notes.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.MutableLiveData
import java.util.*

class StackEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val undoStack = Stack<Change>()
    private val redoStack = Stack<Change>()

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (status) {
                    Status.EDIT -> {
                        undoStack.push(Change(p0.toString(), p1, p2, p3))
                        redoStack.clear()
                    }
                    Status.UNDO -> {
                        redoStack.push(Change(p0.toString(), p1, p2, p3))
                    }
                    Status.REDO -> {
                        undoStack.push(Change(p0.toString(), p1, p2, p3))
                    }
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                fetcher?.value = p0.toString()
            }
        })
    }

    fun undo(): Boolean {
        return if (undoStack.empty()) {
            false
        } else {
            undoStack.pop()?.let {
                status = Status.UNDO
                text?.replace(
                    it.start,
                    it.start + it.lengthAfter,
                    it.text?.subSequence(it.start, it.start + it.lengthBefore)
                )
                status = Status.EDIT
                setSelection(it.start + it.lengthBefore)
            }
            true
        }
    }

    fun redo(): Boolean {
        return if (redoStack.empty()) {
            false
        } else {
            redoStack.pop()?.let {
                status = Status.REDO
                text?.replace(
                    it.start,
                    it.start + it.lengthAfter,
                    it.text?.subSequence(it.start, it.start + it.lengthBefore)
                )
                status = Status.EDIT
                setSelection(it.start + it.lengthBefore)
            }
            true
        }
    }

    private var fetcher: MutableLiveData<String>? = null
    fun fetchTo(text: MutableLiveData<String>) {
        fetcher = text
    }

    private data class Change(
        val text: CharSequence?,
        val start: Int,
        val lengthBefore: Int,
        val lengthAfter: Int
    )

    private enum class Status {
        EDIT,
        UNDO,
        REDO
    }

    private var status: Status = Status.EDIT
}
