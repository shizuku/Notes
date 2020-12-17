package com.shizuku.tools.notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    val content: String,
    val time: Long,
    val pin: Boolean,
    val type: Int,
    val collectionId: Long
) {
    companion object {
        const val markdown = 1
        const val raw = 2
    }
}

class MutableNote(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    var content: String,
    var time: Long,
    var pin: Boolean,
    var type: Int,
    var collectionId: Long
) {
    fun copy(o: MutableNote) {
        this.uid = o.uid
        this.content = o.content
        this.time = o.time
        this.pin = o.pin
        this.type = o.type
        this.collectionId = o.collectionId
    }

    fun copy(o: Note) {
        this.uid = o.uid
        this.content = o.content
        this.time = o.time
        this.pin = o.pin
        this.type = o.type
        this.collectionId = o.collectionId
    }
}

data class NoteWithoutId(
    val content: String,
    val time: Long,
    val pin: Boolean,
    val type: Int,
    val collectionId: Long
)
