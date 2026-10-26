package com.example.rinconsur.data

import com.example.rinconsur.model.TIENDA_EMPLEADO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TiendaRepository(private val dao: TiendaDao) {

    private val semilla = Mutex()

    val tiendas: Flow<List<Tienda>> = flow {
        asegurarInicial()
        emitAll(dao.getAll())
    }

    suspend fun agregar(nombre: String, categoria: String, tieneProbadores: Boolean = false) {
        require(nombre.isNotBlank()) { "El nombre de la tienda no puede estar vacío" }
        require(categoria.isNotBlank()) { "La categoría de la tienda no puede estar vacía" }
        dao.insert(
            Tienda(
                nombre = nombre.trim(),
                categoria = categoria.trim(),
                tieneProbadores = tieneProbadores
            )
        )
    }

    suspend fun eliminar(tienda: Tienda) {
        dao.delete(tienda)
    }

    private suspend fun asegurarInicial() {
        semilla.withLock {
            if (dao.getAll().first().isEmpty()) {
                agregar(
                    nombre = TIENDA_EMPLEADO,
                    categoria = "Vestuario",
                    tieneProbadores = true
                )
            }
        }
    }
}
