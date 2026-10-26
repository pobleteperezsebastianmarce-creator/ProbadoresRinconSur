package com.example.rinconsur.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "informes_historial")
data class InformeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuario: String,
    val nombreTrabajador: String = "",
    val tienda: String,
    val seccion: String,
    val fechaMillis: Long,
    val probadoresUsados: Int,
    val excesosTiempo: Int,
    val personasIngresaron: Int
)
