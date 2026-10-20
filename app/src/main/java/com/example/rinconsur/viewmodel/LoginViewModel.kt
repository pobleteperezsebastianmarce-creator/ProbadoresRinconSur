package com.example.rinconsur.viewmodel
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.rinconsur.model.LoginErrores
import com.example.rinconsur.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
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
        _estado.update{
            it.copy(
                clave = valor,
                mensajeOlvido = null,
                errores = it.errores.copy(clave = null)
            )
        }
    }

    fun onOlvidoClave() {
        val mensaje = "Si olvidaste tu contraseña, avisa en el meson de tienda"
        _estado.update { it.copy(mensajeOlvido = mensaje) }
    }

    fun ingresar(onNavegar: () -> Unit) {
        val actual = _estado.value
        val errUsuario = if (actual.usuario.isBlank()) "El usuario es obligatorio" else null
        val errClave = if (actual.clave.isBlank()) "La clave es obligatoria" else null


        if (errUsuario != null || errClave != null) {
            _estado.update {
                it.copy(
                    errores = LoginErrores(usuario = errUsuario, clave = errClave)
                )
            }
            return
        }

        // Si los datos son válidos, limpiamos errores y ejecutamos la navegación
        _estado.update { it.copy(errores = LoginErrores()) }
        onNavegar()
    }

    fun limpiar() {
        _estado.value = LoginUiState()
    }




}


