package com.example.rinconsur.model

const val ROL_TRABAJADOR = "TRABAJADOR"
const val ROL_ADMIN = "ADMIN"

data class Trabajador(
    val id: Long = 0,
    val nombre: String = "",
    val apellido: String = "",
    val usuario: String,
    val clave: String,
    val tienda: String = "",
    val claveProvisoria: Boolean = true,
    val rol: String = ROL_TRABAJADOR
) {
    fun esAdminRol(): Boolean = rol == ROL_ADMIN
}

const val USUARIO_ADMIN = "admin123"
const val CLAVE_ADMIN = "admin123"
const val USUARIO_EMPLEADO = "empleado.123"
const val CLAVE_EMPLEADO = "123456"
const val TIENDA_EMPLEADO = "Rincón Sur"

val TRABAJADOR_INICIAL = Trabajador(
    usuario = USUARIO_EMPLEADO,
    clave = CLAVE_EMPLEADO,
    tienda = TIENDA_EMPLEADO,
    claveProvisoria = false,
    rol = ROL_TRABAJADOR
)

fun generarUsuarioAuto(nombre: String, apellido: String): String {
    val nomLimpio = nombre.trim().lowercase().replace(Regex("[^a-z0-9]"), "")
    val apeLimpio = apellido.trim().lowercase().replace(Regex("[^a-z0-9]"), "")
    val prefijo = nomLimpio.take(2)
    if (prefijo.isBlank() || apeLimpio.isBlank()) return ""
    return "$prefijo.$apeLimpio"
}

fun esEmpleadoInicial(usuario: String, clave: String): Boolean {
    return usuario.trim().equals(USUARIO_EMPLEADO, ignoreCase = true) && clave == CLAVE_EMPLEADO
}

fun List<Trabajador>.conTrabajadorInicial(): List<Trabajador> {
    val yaEsta = any { it.usuario.trim().equals(USUARIO_EMPLEADO, ignoreCase = true) }
    return if (yaEsta) this else listOf(TRABAJADOR_INICIAL) + this
}

fun List<Trabajador>.codificarTrabajadores(): String = joinToString(separator = "\n") { trabajador ->
    listOf(trabajador.usuario, trabajador.clave, trabajador.tienda).joinToString(separator = "\u001F")
}

fun decodificarTrabajadores(texto: String): List<Trabajador> {
    if (texto.isBlank()) return emptyList()
    val cuentas = mutableListOf<Trabajador>()
    for (fila in texto.split('\n')) {
        val partes = fila.split('\u001F')
        if (partes.size < 3) return emptyList()
        val usuario = partes[0].trim()
        val tienda = partes[2].trim()
        if (usuario.isEmpty() || partes[1].isEmpty()) return emptyList()
        cuentas += Trabajador(usuario = usuario, clave = partes[1], tienda = tienda)
    }
    return cuentas
}

fun usuarioReservado(usuario: String, trabajadores: List<Trabajador>): Boolean {
    val limpio = usuario.trim()
    if (limpio.equals(USUARIO_ADMIN, ignoreCase = true)) return true
    if (limpio.equals(USUARIO_EMPLEADO, ignoreCase = true)) return true
    return trabajadores.any { it.usuario.equals(limpio, ignoreCase = true) }
}

