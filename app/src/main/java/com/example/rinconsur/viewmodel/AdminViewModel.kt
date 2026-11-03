package com.example.rinconsur.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rinconsur.R
import com.example.rinconsur.data.AppDatabase
import com.example.rinconsur.data.InformeEntity
import com.example.rinconsur.data.InformeRepository
import com.example.rinconsur.data.RinconDataStore
import com.example.rinconsur.data.Tienda
import com.example.rinconsur.data.TiendaRepository
import com.example.rinconsur.data.TrabajadorRepository
import com.example.rinconsur.model.ROL_ADMIN
import com.example.rinconsur.model.ROL_TRABAJADOR
import com.example.rinconsur.model.Trabajador
import com.example.rinconsur.model.USUARIO_ADMIN
import com.example.rinconsur.model.conTrabajadorInicial
import com.example.rinconsur.model.generarUsuarioAuto
import com.example.rinconsur.model.usuarioReservado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AdminTab {
    USUARIOS,
    TIENDAS,
    INFORMES
}

enum class RangoFechaFiltro {
    TODOS,
    HOY,
    ULTIMOS_7_DIAS,
    ULTIMOS_30_DIAS
}

enum class VentanaHorariaFiltro {
    TODO_EL_DIA,
    MANANA_08_14,
    TARDE_14_20,
    NOCHE_20_08
}

enum class AvisoAdmin {
    USUARIO_AGREGADO,
    TIENDA_CREADA,
    CONFIRMAR_ELIMINAR_USUARIO,
    USUARIO_ELIMINADO,
    CONFIRMAR_ELIMINAR_TIENDA,
    TIENDA_ELIMINADA
}

data class AdminUiState(
    val tabActual: AdminTab = AdminTab.USUARIOS,
    val usuarioAdminActual: String = "",

    // Formulario Crear Usuario
    val nombreWorker: String = "",
    val apellidoWorker: String = "",
    val usuarioWorker: String = "",
    val usuarioEditadoManualmente: Boolean = false,
    val claveWorker: String = "",
    val esAdminWorker: Boolean = false,
    val errorNombreWorker: String? = null,
    val errorApellidoWorker: String? = null,
    val errorUsuarioWorker: String? = null,
    val errorClaveWorker: String? = null,
    val busquedaWorker: String = "",

    // Formulario Agregar Tienda
    val nombreTienda: String = "",
    val categoriaTienda: String = "Vestuario",
    val errorNombreTienda: String? = null,
    val errorCategoriaTienda: String? = null,
    val tieneProbadoresTienda: Boolean = false,
    val busquedaTienda: String = "",
    val aviso: AvisoAdmin? = null,
    val trabajadorAEliminar: Trabajador? = null,
    val tiendaAEliminar: Tienda? = null,

    // Informes y Filtros
    val informes: List<InformeEntity> = emptyList(),
    val filtroTiendaInforme: String = "Todas",
    val filtroTrabajadorInforme: String = "Todos",
    val filtroRangoFecha: RangoFechaFiltro = RangoFechaFiltro.TODOS,
    val filtroVentanaHoraria: VentanaHorariaFiltro = VentanaHorariaFiltro.TODO_EL_DIA,

    // Listas
    val trabajadores: List<Trabajador> = emptyList(),
    val tiendas: List<Tienda> = emptyList(),
    val categoriasDisponibles: List<String> = listOf(
        "Vestuario",
        "Calzado",
        "Deportes",
        "Hogar",
        "Electrónica",
        "Accesorios",
        "General"
    )
) {
    val esSuperAdmin: Boolean
        get() = usuarioAdminActual.trim().equals(USUARIO_ADMIN, ignoreCase = true)

    val trabajadoresFiltrados: List<Trabajador>
        get() {
            val listaVisibles = if (esSuperAdmin) {
                trabajadores.filter { !it.usuario.equals(USUARIO_ADMIN, ignoreCase = true) }
            } else {
                trabajadores.filter { !it.esAdminRol() && !it.usuario.equals(USUARIO_ADMIN, ignoreCase = true) }
            }

            val q = busquedaWorker.trim()
            if (q.isBlank()) return listaVisibles
            return listaVisibles.filter { t ->
                t.nombre.contains(q, ignoreCase = true) ||
                        t.apellido.contains(q, ignoreCase = true) ||
                        t.usuario.contains(q, ignoreCase = true)
            }
        }

    val tiendasFiltradas: List<Tienda>
        get() {
            val q = busquedaTienda.trim()
            if (q.isBlank()) return tiendas
            return tiendas.filter { store ->
                store.nombre.contains(q, ignoreCase = true) ||
                        store.categoria.contains(q, ignoreCase = true)
            }
        }

    val opcionesTiendasFiltro: List<String>
        get() = listOf("Todas") + tiendas.map { it.nombre }.distinct()

    val opcionesTrabajadoresFiltro: List<String>
        get() = listOf("Todos") + trabajadores.map { t ->
            if (t.nombre.isNotBlank() || t.apellido.isNotBlank()) {
                "${t.nombre} ${t.apellido}".trim()
            } else {
                t.usuario
            }
        }.distinct()

    val informesFiltrados: List<InformeEntity>
        get() {
            return informes.filter { inf ->
                val coincideTienda = filtroTiendaInforme == "Todas" || inf.tienda.equals(filtroTiendaInforme, ignoreCase = true)
                val coincideTrabajador = filtroTrabajadorInforme == "Todos" ||
                        inf.usuario.equals(filtroTrabajadorInforme, ignoreCase = true) ||
                        inf.nombreTrabajador.equals(filtroTrabajadorInforme, ignoreCase = true)
                val coincideFecha = esFechaEnRango(inf.fechaMillis, filtroRangoFecha)
                val coincideVentana = esHoraEnVentana(inf.fechaMillis, filtroVentanaHoraria)

                coincideTienda && coincideTrabajador && coincideFecha && coincideVentana
            }
        }

    val totalPersonasFiltrado: Int
        get() = informesFiltrados.sumOf { it.personasIngresaron }

    val totalUsadosFiltrado: Int
        get() = informesFiltrados.sumOf { it.probadoresUsados }

    val totalExcesosFiltrado: Int
        get() = informesFiltrados.sumOf { it.excesosTiempo }
}

private fun esFechaEnRango(fechaMillis: Long, rango: RangoFechaFiltro): Boolean {
    if (rango == RangoFechaFiltro.TODOS) return true
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val inicioHoy = cal.timeInMillis

    return when (rango) {
        RangoFechaFiltro.HOY -> fechaMillis >= inicioHoy
        RangoFechaFiltro.ULTIMOS_7_DIAS -> fechaMillis >= (inicioHoy - 6 * 24 * 60 * 60 * 1000L)
        RangoFechaFiltro.ULTIMOS_30_DIAS -> fechaMillis >= (inicioHoy - 29 * 24 * 60 * 60 * 1000L)
        RangoFechaFiltro.TODOS -> true
    }
}

private fun esHoraEnVentana(fechaMillis: Long, ventana: VentanaHorariaFiltro): Boolean {
    if (ventana == VentanaHorariaFiltro.TODO_EL_DIA) return true
    val cal = Calendar.getInstance()
    cal.timeInMillis = fechaMillis
    val hora = cal.get(Calendar.HOUR_OF_DAY)

    return when (ventana) {
        VentanaHorariaFiltro.MANANA_08_14 -> hora in 8..13
        VentanaHorariaFiltro.TARDE_14_20 -> hora in 14..19
        VentanaHorariaFiltro.NOCHE_20_08 -> hora >= 20 || hora < 8
        VentanaHorariaFiltro.TODO_EL_DIA -> true
    }
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.get(context = application)
    private val dataStore = RinconDataStore(context = application)
    private val trabajadorRepo = TrabajadorRepository(dao = db.trabajadorDao())
    private val tiendaRepo = TiendaRepository(dao = db.tiendaDao())
    private val informeRepo = InformeRepository(dao = db.informeDao())

    private val _estado = MutableStateFlow(value = AdminUiState())
    val estado: StateFlow<AdminUiState> = _estado

    init {
        viewModelScope.launch {
            dataStore.observar().collect { sesion ->
                _estado.update { it.copy(usuarioAdminActual = sesion.usuario) }
            }
        }
        viewModelScope.launch {
            trabajadorRepo.trabajadores.collect { lista ->
                _estado.update {
                    it.copy(trabajadores = lista.conTrabajadorInicial())
                }
            }
        }
        viewModelScope.launch {
            tiendaRepo.tiendas.collect { lista ->
                _estado.update {
                    it.copy(tiendas = lista)
                }
            }
        }
        viewModelScope.launch {
            informeRepo.informes.collect { lista ->
                _estado.update {
                    it.copy(informes = lista)
                }
            }
        }
    }

    fun onTabChange(tab: AdminTab) {
        _estado.update { it.copy(tabActual = tab) }
    }

    // Handlers Búsqueda
    fun onBusquedaWorkerChange(valor: String) {
        _estado.update { it.copy(busquedaWorker = valor) }
    }

    fun onBusquedaTiendaChange(valor: String) {
        _estado.update { it.copy(busquedaTienda = valor) }
    }

    // Handlers Filtros Informes
    fun onFiltroTiendaInformeChange(valor: String) {
        _estado.update { it.copy(filtroTiendaInforme = valor) }
    }

    fun onFiltroTrabajadorInformeChange(valor: String) {
        _estado.update { it.copy(filtroTrabajadorInforme = valor) }
    }

    fun onFiltroRangoFechaChange(rango: RangoFechaFiltro) {
        _estado.update { it.copy(filtroRangoFecha = rango) }
    }

    fun onFiltroVentanaHorariaChange(ventana: VentanaHorariaFiltro) {
        _estado.update { it.copy(filtroVentanaHoraria = ventana) }
    }

    // Handlers Formulario Usuario
    fun onNombreWorkerChange(valor: String) {
        _estado.update { actual ->
            val autogerenado = if (!actual.usuarioEditadoManualmente) {
                generarUsuarioAuto(nombre = valor, apellido = actual.apellidoWorker)
            } else {
                actual.usuarioWorker
            }
            actual.copy(
                nombreWorker = valor,
                usuarioWorker = autogerenado,
                errorNombreWorker = null
            )
        }
    }

    fun onApellidoWorkerChange(valor: String) {
        _estado.update { actual ->
            val autogerenado = if (!actual.usuarioEditadoManualmente) {
                generarUsuarioAuto(nombre = actual.nombreWorker, apellido = valor)
            } else {
                actual.usuarioWorker
            }
            actual.copy(
                apellidoWorker = valor,
                usuarioWorker = autogerenado,
                errorApellidoWorker = null
            )
        }
    }

    fun onUsuarioWorkerChange(valor: String) {
        _estado.update {
            it.copy(
                usuarioWorker = valor,
                usuarioEditadoManualmente = true,
                errorUsuarioWorker = null
            )
        }
    }

    fun onClaveWorkerChange(valor: String) {
        _estado.update { it.copy(claveWorker = valor, errorClaveWorker = null) }
    }

    fun onEsAdminWorkerChange(valor: Boolean) {
        _estado.update { it.copy(esAdminWorker = valor) }
    }

    fun crearTrabajador() {
        val actual = _estado.value
        val app = getApplication<Application>()
        val nombre = actual.nombreWorker.trim()
        val apellido = actual.apellidoWorker.trim()
        val usuario = actual.usuarioWorker.trim()
        val clave = actual.claveWorker.trim()
        val obligatorio = app.getString(R.string.error_campo_obligatorio)

        val errNombre = if (nombre.isBlank()) obligatorio else null
        val errApellido = if (apellido.isBlank()) obligatorio else null
        val errUsuario = if (usuario.isBlank()) obligatorio else null
        val errClave = if (clave.isBlank()) obligatorio else null

        if (errNombre != null || errApellido != null || errUsuario != null || errClave != null) {
            _estado.update {
                it.copy(
                    errorNombreWorker = errNombre,
                    errorApellidoWorker = errApellido,
                    errorUsuarioWorker = errUsuario,
                    errorClaveWorker = errClave
                )
            }
            return
        }

        viewModelScope.launch {
            val guardados = trabajadorRepo.trabajadores.first().conTrabajadorInicial()
            if (usuarioReservado(usuario = usuario, trabajadores = guardados)) {
                _estado.update {
                    it.copy(errorUsuarioWorker = app.getString(R.string.error_usuario_duplicado))
                }
                return@launch
            }
            trabajadorRepo.agregar(
                nombre = nombre,
                apellido = apellido,
                usuario = usuario,
                clave = clave,
                tienda = "",
                claveProvisoria = true,
                rol = if (actual.esSuperAdmin && actual.esAdminWorker) ROL_ADMIN else ROL_TRABAJADOR
            )
            _estado.update {
                it.copy(
                    nombreWorker = "",
                    apellidoWorker = "",
                    usuarioWorker = "",
                    usuarioEditadoManualmente = false,
                    claveWorker = "",
                    esAdminWorker = false,
                    errorNombreWorker = null,
                    errorApellidoWorker = null,
                    errorUsuarioWorker = null,
                    errorClaveWorker = null,
                    aviso = AvisoAdmin.USUARIO_AGREGADO
                )
            }
            mostrarToast(texto = app.getString(R.string.usuario_agregado))
        }
    }

    fun eliminarTrabajador(trabajador: Trabajador) {
        val actual = _estado.value
        if (trabajador.esAdminRol() && !actual.esSuperAdmin) {
            return
        }
        if (trabajador.usuario.equals(USUARIO_ADMIN, ignoreCase = true)) {
            return
        }
        _estado.update {
            it.copy(
                aviso = AvisoAdmin.CONFIRMAR_ELIMINAR_USUARIO,
                trabajadorAEliminar = trabajador
            )
        }
    }

    fun cancelarEliminarTrabajador() {
        _estado.update { it.copy(aviso = null, trabajadorAEliminar = null) }
    }

    fun confirmarEliminarTrabajador() {
        val actual = _estado.value
        val trabajador = actual.trabajadorAEliminar ?: return
        if (trabajador.esAdminRol() && !actual.esSuperAdmin) {
            cancelarEliminarTrabajador()
            return
        }
        if (trabajador.usuario.equals(USUARIO_ADMIN, ignoreCase = true)) {
            cancelarEliminarTrabajador()
            return
        }
        viewModelScope.launch {
            if (trabajador.id > 0) {
                val entidad = db.trabajadorDao().findById(trabajador.id)
                if (entidad != null) {
                    trabajadorRepo.eliminar(entidad)
                    mostrarToast(texto = getApplication<Application>().getString(R.string.usuario_eliminado))
                    _estado.update {
                        it.copy(
                            aviso = AvisoAdmin.USUARIO_ELIMINADO,
                            trabajadorAEliminar = null
                        )
                    }
                } else {
                    _estado.update { it.copy(aviso = null, trabajadorAEliminar = null) }
                }
            } else {
                _estado.update { it.copy(aviso = null, trabajadorAEliminar = null) }
            }
        }
    }

    fun cerrarAviso() {
        _estado.update { it.copy(aviso = null) }
    }

    // Handlers Formulario Tienda
    fun onNombreTiendaChange(valor: String) {
        _estado.update { it.copy(nombreTienda = valor, errorNombreTienda = null) }
    }

    fun onCategoriaTiendaChange(valor: String) {
        _estado.update { it.copy(categoriaTienda = valor, errorCategoriaTienda = null) }
    }

    fun onTieneProbadoresTiendaChange(valor: Boolean) {
        _estado.update { it.copy(tieneProbadoresTienda = valor) }
    }

    fun crearTienda() {
        val actual = _estado.value
        val app = getApplication<Application>()
        val nombre = actual.nombreTienda.trim()
        val categoria = actual.categoriaTienda.trim()
        val obligatorio = app.getString(R.string.error_campo_obligatorio)

        val errNombre = if (nombre.isBlank()) obligatorio else null
        val errCat = if (categoria.isBlank()) obligatorio else null

        if (errNombre != null || errCat != null) {
            _estado.update {
                it.copy(
                    errorNombreTienda = errNombre,
                    errorCategoriaTienda = errCat
                )
            }
            return
        }

        viewModelScope.launch {
            tiendaRepo.agregar(
                nombre = nombre,
                categoria = categoria,
                tieneProbadores = actual.tieneProbadoresTienda
            )
            _estado.update {
                it.copy(
                    nombreTienda = "",
                    categoriaTienda = it.categoriasDisponibles.firstOrNull() ?: "Vestuario",
                    tieneProbadoresTienda = false,
                    errorNombreTienda = null,
                    errorCategoriaTienda = null,
                    aviso = AvisoAdmin.TIENDA_CREADA
                )
            }
            mostrarToast(texto = app.getString(R.string.tienda_creada))
        }
    }

    private fun mostrarToast(texto: String) {
        val application = getApplication<Application>()
        val mostrar = Runnable {
            Toast.makeText(application, texto, Toast.LENGTH_LONG).show()
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mostrar.run()
        } else {
            Handler(Looper.getMainLooper()).post(mostrar)
        }
    }

    fun eliminarTienda(tienda: Tienda) {
        _estado.update {
            it.copy(
                aviso = AvisoAdmin.CONFIRMAR_ELIMINAR_TIENDA,
                tiendaAEliminar = tienda
            )
        }
    }

    fun cancelarEliminarTienda() {
        _estado.update { it.copy(aviso = null, tiendaAEliminar = null) }
    }

    fun confirmarEliminarTienda() {
        val tienda = _estado.value.tiendaAEliminar ?: return
        viewModelScope.launch {
            tiendaRepo.eliminar(tienda)
            mostrarToast(texto = getApplication<Application>().getString(R.string.tienda_eliminada))
            _estado.update {
                it.copy(
                    aviso = AvisoAdmin.TIENDA_ELIMINADA,
                    tiendaAEliminar = null
                )
            }
        }
    }
}
