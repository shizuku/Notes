package com.shizuku.tools.notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val time: Long,
    val content: String
) : Comparable<Note> {
    override fun compareTo(other: Note): Int {
        return (time - other.time).toInt()
    }
}

data class NoteWithoutId(
    val time: Long,
    val content: String
)
