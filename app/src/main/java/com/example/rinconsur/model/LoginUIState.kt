package com.example.rinconsur.model

data class LoginUiState(
    val usuario: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores(),
    val mensajeOlvido: String? = null,
    val esAdmin: Boolean = false,
    val tienda: String = "",
    val claveProvisoria: Boolean = false,
    val nuevaClave: String = "",
    val confirmarClave: String = "",
    val errorNuevaClave: String? = null,
    val errorConfirmarClave: String? = null
)