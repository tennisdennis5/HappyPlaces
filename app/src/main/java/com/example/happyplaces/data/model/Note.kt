package com.example.happyplaces.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persönliche Notiz zu einem Happy Place.
 */
@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Zu welchem Ort gehört die Notiz?
    val placeId: Long,

    // Inhalt der Notiz
    val text: String
)