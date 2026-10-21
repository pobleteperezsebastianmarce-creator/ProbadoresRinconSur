package com.example.rinconsur.model

import androidx.compose.ui.util.fastCoerceAtLeast

enum class FiltroProbadores {

    TODOS,
    DISPONIBLES,
    OCUPADOS,
    ALERTAS

}
enum class  EstadoProbador {

    DISPONIBLE,
    OCUPADO,
    EXCESO
}

data class Probador(
    val numero: Int,
    val ocupado: Boolean = false,
    val enExceso: Boolean = false,
    val inicioUsoMillis: Long? = null,
    val alertaEnviada: Boolean = false,
    val usadoEnTurno: Boolean = false,
    val prendas: Int =0
) {

    fun estadoVisual(): EstadoProbador = when {
        !ocupado -> EstadoProbador.DISPONIBLE
        enExceso -> EstadoProbador.EXCESO
        else -> EstadoProbador.OCUPADO
    }

    fun millisTranscurridos(ahora: Long): Long {
        val inicio = inicioUsoMillis ?: return 0L
        return (ahora - inicio).coerceAtLeast(0L)
    }
}

const val LIMITE_USO_MILLIS = 12 * 60 * 1000L
const val MIN_PROBADORES = 1
const val MAX_PROBADORES = 12
const val MIN_PRENDAS = 1 
const val MAX_PRENDAS = 5

fun List<Probador>.codificarEstado(): String = joinToString(separator = ";") { probador ->
    listOf(
        probador.numero.toString(),
        if (probador.ocupado) "1" else "0",
        if (probador.enExceso) "1" else "0",
        probador.inicioUsoMillis?.toString().orEmpty(),
        if (probador.alertaEnviada) "1" else "0",
        if (probador.usadoEnTurno) "1" else "0",
        probador.prendas.coerceIn(0, MAX_PRENDAS).toString()
    ).joinToString(separator = ",")
}

fun decodificarProbadores(texto: String): List<Probador> {
    if (texto.isBlank()) return emptyList()
    val salas = mutableListOf<Probador>()
    for (fila in texto.split(';')) {
        val partes = fila.split(',')
        if (partes.size != 7) return emptyList()
        val numero = partes[0].toIntOrNull() ?: return emptyList()
        val prendas = partes[6].toIntOrNull() ?: return emptyList()
        val inicio = partes[3].takeIf { it.isNotEmpty() }?.toLongOrNull()
        if (partes[3].isNotEmpty() && inicio == null) return emptyList()
        salas += Probador(
            numero = numero,
            ocupado = partes[1] == "1",
            enExceso = partes[2] == "1",
            inicioUsoMillis = inicio,
            alertaEnviada = partes[4] == "1",
            usadoEnTurno = partes[5] == "1",
            prendas = prendas.coerceIn(0, MAX_PRENDAS)
        )

    }
    return salas
}

fun formatoTiempo(millis: Long): String {
    val totalSegundos = ( millis / 1000L).fastCoerceAtLeast( 0)
    val minutos = totalSegundos / 60L
    val segundos= totalSegundos % 60L
    return "%d:%02d".format(minutos, segundos)
}

fun List<Probador>.aplicarFiltro(filtro: FiltroProbadores): List<Probador> = when (filtro) {
    FiltroProbadores.TODOS -> this
    FiltroProbadores.DISPONIBLES -> filter { probador -> !probador.ocupado}
    FiltroProbadores.OCUPADOS -> filter { probador ->  probador.ocupado}
    FiltroProbadores.ALERTAS -> filter { probador ->  probador.enExceso}
    }


























        