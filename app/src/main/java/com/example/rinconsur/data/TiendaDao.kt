package com.example.rinconsur.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TiendaDao {

    @Query("SELECT * FROM tiendas ORDER BY id ASC")
    fun getAll(): Flow<List<Tienda>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tienda: Tienda): Long

    @Delete
    suspend fun delete(tienda: Tienda)
}
