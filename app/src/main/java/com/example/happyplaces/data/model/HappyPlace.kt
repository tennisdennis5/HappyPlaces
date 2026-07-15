package com.example.happyplaces.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Repräsentiert einen gespeicherten Lieblingsort.
 */
@Entity(tableName = "happy_places")
data class HappyPlace(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val description: String,

    val imageUri: String? = null,

    val latitude: Double,

    val longitude: Double,

    /**
     * Persönliche Notiz zu diesem Ort.
     * Ein leerer String bedeutet: keine Notiz vorhanden.
     */
    val personalNote: String = "",

    val createdAt: Long = System.currentTimeMillis()
)