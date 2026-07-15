package com.example.happyplaces.repository

import com.example.happyplaces.data.database.HappyPlaceDao
import com.example.happyplaces.data.model.HappyPlace
import kotlinx.coroutines.flow.Flow

/**
 * Vermittelt zwischen Room-Datenbank und ViewModel.
 */
class HappyPlacesRepository(
    private val happyPlaceDao: HappyPlaceDao
) {

    val places: Flow<List<HappyPlace>> =
        happyPlaceDao.getAllPlaces()

    suspend fun insert(place: HappyPlace) {
        happyPlaceDao.insert(place)
    }

    suspend fun update(place: HappyPlace) {
        happyPlaceDao.update(place)
    }

    suspend fun delete(place: HappyPlace) {
        happyPlaceDao.delete(place)
    }
}