package com.example.rinconsur.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrabajadorDao {

    @Query(value = "SELECT * FROM trabajadores ORDER BY id ASC")
    fun getAll(): Flow<List<Trabajador>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trabajador: Trabajador): Long

    @Update
    suspend fun update(trabajador: Trabajador)

    @Delete

    suspend fun delete(trabajador: Trabajador)

    @Query(value = "SELECT * FROM trabajadores WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Trabajador?
}