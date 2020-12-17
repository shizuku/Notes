package com.shizuku.tools.notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection")
data class Collection(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    val name: String
)

data class CollectionWithoutId(
    val name: String
)
