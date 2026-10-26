package com.example.rinconsur.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.rinconDataStore by preferencesDataStore(name = "rincon_sur")

data class SesionGuardada(
    val usuario: String = "",
    val seccion: String = "Vestuario Hombre",
    val cantidadProbadores: Int = 12,
    val turnoActivo: Boolean = false,
    val informeGuardado: Boolean = false,
    val probadoresUsados: Int = 0,
    val excesosTiempo: Int = 0,
    val estadoProbadores: String = "",
    val excesosEnCurso: Int = 0,
    val personasIngresaron: Int = 0,
    val personasEnCurso: Int = 0,
    val esAdmin: Boolean = false,
    val tienda: String = "",
    val trabajadores: String = "",
    val claveProvisoria: Boolean = false
)

class RinconDataStore(private val context: Context) {

    private val usuarioKey = stringPreferencesKey(name = "usuario")
    private val seccionKey = stringPreferencesKey(name = "seccion")
    private val cantidadKey = intPreferencesKey(name = "cantidad_probadores")
    private val turnoActivoKey = booleanPreferencesKey(name = "turno_activo")
    private val informeGuardadoKey = booleanPreferencesKey(name = "informe_guardado")
    private val usadosKey = intPreferencesKey(name = "informe_usados")
    private val prendasKey = intPreferencesKey(name = "informe_prendas")
    private val excesosKey = intPreferencesKey(name = "informe_excesos")
    private val estadoProbadoresKey = stringPreferencesKey(name = "estado_probadores")
    private val excesosCursoKey = intPreferencesKey(name = "excesos_en_curso")
    private val personasInformeKey = intPreferencesKey(name = "informe_personas")
    private val personasCursoKey = intPreferencesKey(name = "personas_en_curso")
    private val esAdminKey = booleanPreferencesKey(name = "es_admin")
    private val tiendaKey = stringPreferencesKey(name = "tienda_sesion")
    private val trabajadoresKey = stringPreferencesKey(name = "trabajadores")
    private val claveProvisoriaKey = booleanPreferencesKey(name = "clave_provisoria")

    fun observar(): Flow<SesionGuardada> {
        return context.rinconDataStore.data.map { preferencias ->
            SesionGuardada(
                usuario = preferencias[usuarioKey].orEmpty(),
                seccion = preferencias[seccionKey] ?: "Vestuario Hombre",
                cantidadProbadores = preferencias[cantidadKey] ?: 12,
                turnoActivo = preferencias[turnoActivoKey] ?: false,
                informeGuardado = preferencias[informeGuardadoKey] ?: false,
                probadoresUsados = preferencias[usadosKey] ?: 0,
                excesosTiempo = preferencias[excesosKey] ?: 0,
                estadoProbadores = preferencias[estadoProbadoresKey].orEmpty(),
                excesosEnCurso = preferencias[excesosCursoKey] ?: 0,
                personasIngresaron = preferencias[personasInformeKey] ?: 0,
                personasEnCurso = preferencias[personasCursoKey] ?: 0,
                esAdmin = preferencias[esAdminKey] ?: false,
                tienda = preferencias[tiendaKey].orEmpty(),
                trabajadores = preferencias[trabajadoresKey].orEmpty(),
                claveProvisoria = preferencias[claveProvisoriaKey] ?: false
            )
        }
    }

    suspend fun cerrarSesion() {
        context.rinconDataStore.edit { preferencias ->
            preferencias[usuarioKey] = ""
            preferencias[turnoActivoKey] = false
            preferencias[esAdminKey] = false
            preferencias[tiendaKey] = ""
            preferencias[estadoProbadoresKey] = ""
            preferencias[excesosCursoKey] = 0
            preferencias[personasCursoKey] = 0
            preferencias[claveProvisoriaKey] = false
        }
    }

    suspend fun guardarIngreso(
        usuario: String,
        esAdmin: Boolean,
        tienda: String,
        claveProvisoria: Boolean = false
    ) {
        context.rinconDataStore.edit { preferencias ->
            val cambioUsuario = preferencias[usuarioKey] != usuario
            preferencias[usuarioKey] = usuario
            preferencias[esAdminKey] = esAdmin
            preferencias[tiendaKey] = tienda
            preferencias[claveProvisoriaKey] = claveProvisoria
            if (esAdmin || cambioUsuario) {
                preferencias[turnoActivoKey] = false
            }
        }
    }

    suspend fun guardarTrabajadores(texto: String) {
        context.rinconDataStore.edit { preferencias ->
            preferencias[trabajadoresKey] = texto
        }
    }

    suspend fun guardarUsuario(usuario: String) {
        context.rinconDataStore.edit { preferencias ->
            preferencias[usuarioKey] = usuario
        }
    }

    suspend fun guardarTurno(seccion: String, cantidad: Int, activo: Boolean) {
        context.rinconDataStore.edit { preferencias ->
            preferencias[seccionKey] = seccion
            preferencias[cantidadKey] = cantidad
            preferencias[turnoActivoKey] = activo
        }
    }

    suspend fun guardarInforme(
        usados: Int,
        excesos: Int,
        personas: Int,
        turnoActivo: Boolean
    ) {
        context.rinconDataStore.edit { preferencias ->
            preferencias[usadosKey] = usados
            preferencias[excesosKey] = excesos
            preferencias[personasInformeKey] = personas
            preferencias.remove(key = prendasKey)
            preferencias[informeGuardadoKey] = true
            preferencias[turnoActivoKey] = turnoActivo
            preferencias[estadoProbadoresKey] = ""
            preferencias[excesosCursoKey] = 0
            preferencias[personasCursoKey] = 0
        }
    }

    suspend fun guardarCurso(
        estadoProbadores: String,
        excesosEnCurso: Int,
        personasEnCurso: Int
    ) {
        context.rinconDataStore.edit { preferencias ->
            preferencias[estadoProbadoresKey] = estadoProbadores
            preferencias[excesosCursoKey] = excesosEnCurso
            preferencias[personasCursoKey] = personasEnCurso
        }
    }
}


