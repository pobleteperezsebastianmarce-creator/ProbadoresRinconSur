package com.example.rinconsur.model

enum class DialogoTurno {
    ASIGNAR,
    LIBERAR
}

data class  TurnoUiState(
    val cargado: Boolean = false,
    val usuario: String = "",
    val esAdmin: Boolean = false,
    val tienda: String = "",
    val claveProvisoria: Boolean = false,
    val tiendaDisponibles: List<String> = emptyList(),
    val seccion: String ="Vestuario Hombre ",
    val cantidadProbadores: Int = 12,
    val iniciando: Boolean = false,
    val turnoActivo: Boolean = false,
    val probadores: List<Probador> = emptyList(),
    val filtro: FiltroProbadores = FiltroProbadores.TODOS,
    val ahoraMillis: Long = 0L,
    val dialogo: DialogoTurno? = null,
    val probadorDialogo: Int? = null,
    val informeEnCurso: Informe = Informe(),
    val informeGuardado: Informe? = null,
    val mensajeSoporte: String? = null,
    val mensajeBreve: String? = null,
    val avisoNotificaciones: String? = null
)