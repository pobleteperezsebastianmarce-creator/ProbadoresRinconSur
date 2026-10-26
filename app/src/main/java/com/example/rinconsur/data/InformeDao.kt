package com.example.rinconsur.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InformeDao {

    @Query("SELECT * FROM informes_historial ORDER BY fechaMillis DESC")
    fun getAll(): Flow<List<InformeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(informe: InformeEntity): Long

    @Delete
    suspend fun delete(informe: InformeEntity)
}
