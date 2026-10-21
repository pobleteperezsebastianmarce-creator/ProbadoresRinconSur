package com.example.rinconsur.model



data class Informe(
    val probadoresUsados: Int = 0,
    val excesoTiempo: Int = 0,
    val personajesIngresaron: Int = 0
)


data class ResumenCierre(
    val utilizados: Int = 0,
    val excedidos: Int = 0,
    val dentroDelTiempo: Int = 0
)

fun resumenCierre(utilizados: Int, excedidos:Int): ResumenCierre{
    val usos = utilizados.coerceAtLeast(0)
    val pasados = excedidos.coerceIn(0, usos)
    return ResumenCierre(
        utilizados = usos,
        excedidos = pasados,
        dentroDelTiempo = usos - pasados
    )
}