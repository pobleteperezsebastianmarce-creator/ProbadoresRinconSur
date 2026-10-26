package com.example.rinconsur.data

import com.example.rinconsur.model.CLAVE_EMPLEADO
import com.example.rinconsur.model.ROL_TRABAJADOR
import com.example.rinconsur.model.TIENDA_EMPLEADO
import com.example.rinconsur.model.USUARIO_EMPLEADO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.rinconsur.model.Trabajador as Cuenta

class TrabajadorRepository(private val dao: TrabajadorDao) {

    private val semilla = Mutex()

    val trabajadores: Flow<List<Cuenta>> = flow {
        asegurarInicial()
        emitAll(dao.getAll().map { filas -> filas.map { fila -> fila.aCuenta() } })
    }

    suspend fun agregar(
        nombre: String = "",
        apellido: String = "",
        usuario: String,
        clave: String,
        tienda: String = "",
        claveProvisoria: Boolean = true,
        rol: String = ROL_TRABAJADOR
    ) {
        require(value = usuario.isNotBlank()) { "El usuario no puede estar vacío" }
        require(value = clave.isNotBlank()) { "La contraseña no puede estar vacía" }
        dao.insert(
            Trabajador(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                usuario = usuario.trim(),
                clave = clave,
                tienda = tienda.trim(),
                claveProvisoria = claveProvisoria,
                rol = rol
            )
        )
    }

    suspend fun cambiarClave(usuario: String, nuevaClave: String) {
        val limpio = usuario.trim()
        require(limpio.isNotBlank()) { "El usuario no puede estar vacío" }
        require(nuevaClave.isNotBlank()) { "La nueva contraseña no puede estar vacía" }
        val lista = dao.getAll().first()
        val encontrado = lista.firstOrNull { it.usuario.trim().equals(limpio, ignoreCase = true) }
        if (encontrado != null) {
            dao.update(encontrado.copy(clave = nuevaClave, claveProvisoria = false))
        }
    }

    suspend fun eliminar(trabajador: Trabajador) = dao.delete(trabajador)

    suspend fun obtener(id: Long): Trabajador? = dao.findById(id = id)

    private suspend fun asegurarInicial() {
        semilla.withLock {
            if (dao.getAll().first().isEmpty()) {
                agregar(
                    nombre = "Empleado",
                    apellido = "Inicial",
                    usuario = USUARIO_EMPLEADO,
                    clave = CLAVE_EMPLEADO,
                    tienda = TIENDA_EMPLEADO,
                    claveProvisoria = false,
                    rol = ROL_TRABAJADOR
                )
            }
        }
    }
}

private fun Trabajador.aCuenta(): Cuenta = Cuenta(
    id = id,
    nombre = nombre,
    apellido = apellido,
    usuario = usuario,
    clave = clave,
    tienda = tienda,
    claveProvisoria = claveProvisoria,
    rol = rol
)
