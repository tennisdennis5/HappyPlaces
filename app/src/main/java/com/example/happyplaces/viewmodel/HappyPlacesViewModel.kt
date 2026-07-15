package com.example.happyplaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.happyplaces.data.model.HappyPlace
import com.example.happyplaces.repository.HappyPlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HappyPlacesViewModel(
    private val repository: HappyPlacesRepository
) : ViewModel() {

    val places: Flow<List<HappyPlace>> = repository.places

    fun addPlace(
        title: String,
        description: String,
        imageUri: String?,
        latitude: Double,
        longitude: Double
    ) {
        val place = HappyPlace(
            title = title,
            description = description,
            imageUri = imageUri,
            latitude = latitude,
            longitude = longitude
        )

        viewModelScope.launch {
            repository.insert(place)
        }
    }

    fun deletePlace(place: HappyPlace) {
        viewModelScope.launch {
            repository.delete(place)
        }
    }

    /**
     * Fügt eine Notiz hinzu oder bearbeitet sie.
     * Ein leerer Text löscht die Notiz.
     */
    fun savePersonalNote(
        place: HappyPlace,
        noteText: String
    ) {
        viewModelScope.launch {
            repository.update(
                place.copy(
                    personalNote = noteText.trim()
                )
            )
        }
    }
}

/**
 * Erstellt das ViewModel mit dem Repository.
 */
class HappyPlacesViewModelFactory(
    private val repository: HappyPlacesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                HappyPlacesViewModel::class.java
            )
        ) {
            return HappyPlacesViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unbekannte ViewModel-Klasse: ${modelClass.name}"
        )
    }
}