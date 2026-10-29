package com.example.rinconsur.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rinconsur.model.ROL_TRABAJADOR

@Entity(tableName = "trabajadores")
data class Trabajador(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String = "",
    val apellido: String = "",
    val usuario: String,
    val clave: String,
    val tienda: String = "",
    val claveProvisoria: Boolean = true,
    val rol: String = ROL_TRABAJADOR
)
