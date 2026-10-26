package com.example.rinconsur.data

import kotlinx.coroutines.flow.Flow

class InformeRepository(private val dao: InformeDao) {

    val informes: Flow<List<InformeEntity>> = dao.getAll()

    suspend fun guardar(
        usuario: String,
        nombreTrabajador: String = "",
        tienda: String,
        seccion: String,
        fechaMillis: Long,
        probadoresUsados: Int,
        excesosTiempo: Int,
        personasIngresaron: Int
    ) {
        dao.insert(
            InformeEntity(
                usuario = usuario,
                nombreTrabajador = nombreTrabajador,
                tienda = tienda,
                seccion = seccion,
                fechaMillis = fechaMillis,
                probadoresUsados = probadoresUsados,
                excesosTiempo = excesosTiempo,
                personasIngresaron = personasIngresaron
            )
        )
    }

    suspend fun eliminar(informe: InformeEntity) {
        dao.delete(informe)
    }
}
