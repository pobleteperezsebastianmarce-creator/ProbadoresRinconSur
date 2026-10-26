package com.example.rinconsur.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tiendas")
data class Tienda(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val tieneProbadores: Boolean = false
)