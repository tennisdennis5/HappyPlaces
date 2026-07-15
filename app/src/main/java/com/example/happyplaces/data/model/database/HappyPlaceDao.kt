package com.example.happyplaces.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.happyplaces.data.model.HappyPlace
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {

    @Query("SELECT * FROM happy_places ORDER BY createdAt DESC")
    fun getAllPlaces(): Flow<List<HappyPlace>>

    @Insert
    suspend fun insert(place: HappyPlace)

    @Update
    suspend fun update(place: HappyPlace)

    @Delete
    suspend fun delete(place: HappyPlace)

    @Query("SELECT * FROM happy_places WHERE id = :id")
    suspend fun getPlaceById(id: Long): HappyPlace?
}