package com.example.rinconsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rinconsur.R
import com.example.rinconsur.data.AppDatabase
import com.example.rinconsur.data.TrabajadorRepository
import com.example.rinconsur.model.CLAVE_ADMIN
import com.example.rinconsur.model.LoginErrores
import com.example.rinconsur.model.LoginUiState
import com.example.rinconsur.model.TRABAJADOR_INICIAL
import com.example.rinconsur.model.USUARIO_ADMIN
import com.example.rinconsur.model.esEmpleadoInicial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = TrabajadorRepository(
        dao = AppDatabase.get(context = application).trabajadorDao()
    )

    private val _estado = MutableStateFlow(value = LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado

    fun onUsuarioChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = valor,
                mensajeOlvido = null,
                errores = it.errores.copy(usuario = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                mensajeOlvido = null,
                errores = it.errores.copy(clave = null)
            )
        }
    }

    fun onOlvidoClave() {
        val mensaje = getApplication<Application>().getString(R.string.mensaje_olvido)
        _estado.update { it.copy(mensajeOlvido = mensaje) }
    }

    fun intentarLoginAdmin(onOk: () -> Unit) {
        val estadoActual = _estado.value
        val app = getApplication<Application>()
        val usuario = estadoActual.usuario.trim()
        val clave = estadoActual.clave
        val obligatorio = app.getString(R.string.error_campo_obligatorio)
        val erroresVacios = LoginErrores(
            usuario = if (usuario.isBlank()) obligatorio else null,
            clave = if (clave.isBlank()) obligatorio else null
        )
        if (erroresVacios.usuario != null || erroresVacios.clave != null) {
            _estado.update {
                it.copy(usuario = usuario, esAdmin = true, tienda = "", errores = erroresVacios)
            }
            return
        }
        viewModelScope.launch {
            val esMasterAdmin =
                usuario.equals(USUARIO_ADMIN, ignoreCase = true) && clave == CLAVE_ADMIN
            val trabajadores = repositorio.trabajadores.first()
            val trabajadorAdmin = trabajadores.firstOrNull {
                it.usuario.trim()
                    .equals(usuario, ignoreCase = true) && it.clave == clave && it.esAdminRol()
            }

            if (!esMasterAdmin && trabajadorAdmin == null) {
                _estado.update {
                    it.copy(
                        usuario = usuario,
                        esAdmin = false,
                        tienda = "",
                        claveProvisoria = false,
                        errores = LoginErrores(clave = app.getString(R.string.error_credenciales_admin))
                    )
                }
                return@launch
            }

            _estado.update {
                it.copy(
                    usuario = usuario,
                    esAdmin = true,
                    tienda = trabajadorAdmin?.tienda.orEmpty(),
                    claveProvisoria = trabajadorAdmin?.claveProvisoria ?: false,
                    errores = LoginErrores()
                )
            }
            onOk()
        }
    }

    fun intentarLoginTrabajador(onOk: () -> Unit) {
        val estadoActual = _estado.value
        val app = getApplication<Application>()
        val usuario = estadoActual.usuario.trim()
        val clave = estadoActual.clave
        val obligatorio = app.getString(R.string.error_campo_obligatorio)
        val erroresVacios = LoginErrores(
            usuario = if (usuario.isBlank()) obligatorio else null,
            clave = if (clave.isBlank()) obligatorio else null
        )
        if (erroresVacios.usuario != null || erroresVacios.clave != null) {
            _estado.update {
                it.copy(usuario = usuario, esAdmin = false, tienda = "", errores = erroresVacios)
            }
            return
        }
        viewModelScope.launch {
            val trabajadores = repositorio.trabajadores.first()
            val trabajador = when {
                esEmpleadoInicial(usuario = usuario, clave = clave) -> TRABAJADOR_INICIAL
                else -> trabajadores.firstOrNull {
                    it.usuario.trim().equals(usuario, ignoreCase = true) && it.clave == clave
                }
            }
            if (trabajador == null) {
                _estado.update {
                    it.copy(
                        usuario = usuario,
                        esAdmin = false,
                        tienda = "",
                        claveProvisoria = false,
                        errores = LoginErrores(clave = app.getString(R.string.error_credenciales_trabajador))
                    )
                }
                return@launch
            }
            _estado.update {
                it.copy(
                    usuario = usuario,
                    esAdmin = false,
                    tienda = trabajador.tienda,
                    claveProvisoria = trabajador.claveProvisoria,
                    errores = LoginErrores()
                )
            }
            onOk()
        }
    }

    fun onNuevaClaveChange(valor: String) {
        _estado.update { it.copy(nuevaClave = valor, errorNuevaClave = null) }
    }

    fun onConfirmarClaveChange(valor: String) {
        _estado.update { it.copy(confirmarClave = valor, errorConfirmarClave = null) }
    }

    fun guardarNuevaClave(onExito: () -> Unit) {
        val actual = _estado.value
        val app = getApplication<Application>()
        val nueva = actual.nuevaClave
        val confirmar = actual.confirmarClave
        val obligatorio = app.getString(R.string.error_campo_obligatorio)

        var errNueva = if (nueva.isBlank()) obligatorio else null
        var errConfirmar = if (confirmar.isBlank()) obligatorio else null

        if (errNueva == null && errConfirmar == null) {
            if (nueva == actual.clave) {
                errNueva = app.getString(R.string.error_clave_igual_provisoria)
            } else if (nueva != confirmar) {
                errConfirmar = app.getString(R.string.error_claves_no_coinciden)
            }
        }

        if (errNueva != null || errConfirmar != null) {
            _estado.update {
                it.copy(
                    errorNuevaClave = errNueva,
                    errorConfirmarClave = errConfirmar
                )
            }
            return
        }

        viewModelScope.launch {
            repositorio.cambiarClave(usuario = actual.usuario, nuevaClave = nueva)
            _estado.update {
                it.copy(
                    clave = nueva,
                    claveProvisoria = false,
                    nuevaClave = "",
                    confirmarClave = "",
                    errorNuevaClave = null,
                    errorConfirmarClave = null
                )
            }
            onExito()
        }
    }

    fun intentarLogin(onOk: () -> Unit) {
        intentarLoginTrabajador(onOk = onOk)
    }

    fun limpiar() {
        _estado.value = LoginUiState()

    }
}


