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


}


