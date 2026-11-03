package com.example.rinconsur.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rinconsur.R
import com.example.rinconsur.ui.screen.AdminScreen
import com.example.rinconsur.ui.screen.CambiarClaveScreen
import com.example.rinconsur.ui.screen.CerrarTurnoScreen
import com.example.rinconsur.ui.screen.ConfigurarTurnoScreen
import com.example.rinconsur.ui.screen.InformeScreen
import com.example.rinconsur.ui.screen.LoginScreen
import com.example.rinconsur.ui.screen.ProbadoresScreen
import com.example.rinconsur.ui.screen.SeleccionRolScreen
import com.example.rinconsur.ui.theme.BordeSuave
import com.example.rinconsur.ui.theme.Crema
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeBoton
import com.example.rinconsur.viewmodel.AdminViewModel
import com.example.rinconsur.viewmodel.LoginViewModel
import com.example.rinconsur.viewmodel.TurnoViewModel

const val RUTA_SELECCION_ROL = "seleccion_rol"
const val RUTA_LOGIN_ADMIN = "login_admin"
const val RUTA_LOGIN_TRABAJADOR = "login_trabajador"
const val RUTA_CAMBIAR_CLAVE = "cambiar_clave"
const val RUTA_LOGIN = "login"
const val RUTA_ADMIN = "admin"
const val RUTA_CONFIGURAR = "configurar"
const val RUTA_PROBADORES = "probadores"
const val RUTA_CERRAR = "cerrar"
const val RUTA_INFORME = "informe"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val turnoViewModel: TurnoViewModel = viewModel()
    val turno by turnoViewModel.estado.collectAsState()

    val inicio = when {
        turno.claveProvisoria && turno.usuario.isNotBlank() -> RUTA_CAMBIAR_CLAVE
        turno.esAdmin && turno.usuario.isNotBlank() -> RUTA_ADMIN
        turno.turnoActivo && turno.usuario.isNotBlank() -> RUTA_PROBADORES
        turno.usuario.isNotBlank() -> RUTA_CONFIGURAR
        else -> RUTA_SELECCION_ROL
    }

    NavHost(
        navController = navController,
        startDestination = inicio
        ) {
            composable(route = RUTA_SELECCION_ROL) {
                SeleccionRolScreen(
                    onSeleccionarAdmin = {
                        loginViewModel.limpiar()
                        navController.navigate(route = RUTA_LOGIN_ADMIN)
                    },
                    onSeleccionarTrabajador = {
                        loginViewModel.limpiar()
                        navController.navigate(route = RUTA_LOGIN_TRABAJADOR)
                    }
                )
            }
            composable(route = RUTA_LOGIN_ADMIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    esAdminMode = true,
                    onVolver = { navController.popBackStack() },
                    onLoginOk = {
                        val ingreso = loginViewModel.estado.value
                        turnoViewModel.registrarAdmin(
                            usuario = ingreso.usuario,
                            claveProvisoria = ingreso.claveProvisoria
                        )
                        if (ingreso.claveProvisoria) {
                            navController.navigate(route = RUTA_CAMBIAR_CLAVE) {
                                popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                            }
                        } else {
                            navController.navigate(route = RUTA_ADMIN) {
                                popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(route = RUTA_LOGIN_TRABAJADOR) {
                LoginScreen(
                    viewModel = loginViewModel,
                    esAdminMode = false,
                    onVolver = { navController.popBackStack() },
                    onLoginOk = {
                        val ingreso = loginViewModel.estado.value
                        turnoViewModel.registrarSesion(
                            usuario = ingreso.usuario,
                            tienda = ingreso.tienda,
                            claveProvisoria = ingreso.claveProvisoria
                        )
                        if (ingreso.claveProvisoria) {
                            navController.navigate(route = RUTA_CAMBIAR_CLAVE) {
                                popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                            }
                        } else {
                            navController.navigate(route = RUTA_CONFIGURAR) {
                                popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(route = RUTA_CAMBIAR_CLAVE) {
                CambiarClaveScreen(
                    viewModel = loginViewModel,
                    onExito = {
                        turnoViewModel.confirmarClaveDefinitiva()
                        val destino = if (turno.esAdmin) RUTA_ADMIN else RUTA_CONFIGURAR
                        navController.navigate(route = destino) {
                            popUpTo(route = RUTA_CAMBIAR_CLAVE) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = RUTA_LOGIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    esAdminMode = false,
                    onVolver = { navController.popBackStack() },
                    onLoginOk = {
                        val ingreso = loginViewModel.estado.value
                        if (ingreso.esAdmin) {
                            turnoViewModel.registrarAdmin(
                                usuario = ingreso.usuario,
                                claveProvisoria = ingreso.claveProvisoria
                            )
                            if (ingreso.claveProvisoria) {
                                navController.navigate(route = RUTA_CAMBIAR_CLAVE) {
                                    popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                                }
                            } else {
                                navController.navigate(route = RUTA_ADMIN) {
                                    popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                                }
                            }
                        } else {
                            turnoViewModel.registrarSesion(
                                usuario = ingreso.usuario,
                                tienda = ingreso.tienda,
                                claveProvisoria = ingreso.claveProvisoria
                            )
                            if (ingreso.claveProvisoria) {
                                navController.navigate(route = RUTA_CAMBIAR_CLAVE) {
                                    popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                                }
                            } else {
                                navController.navigate(route = RUTA_CONFIGURAR) {
                                    popUpTo(route = RUTA_SELECCION_ROL) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            composable(route = RUTA_ADMIN) {
                val adminViewModel: AdminViewModel = viewModel()
                AdminScreen(
                    viewModel = adminViewModel,
                    onCerrarSesion = {
                        cerrarSesion(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            turnoViewModel = turnoViewModel
                        )
                    }
                )
            }
            composable(route = RUTA_CONFIGURAR) {
                ConfigurarTurnoScreen(
                    viewModel = turnoViewModel,
                    onTurnoIniciado = {
                        navController.navigate(route = RUTA_PROBADORES) {
                            popUpTo(route = RUTA_PROBADORES) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onCerrarSesion = {
                        cerrarSesion(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            turnoViewModel = turnoViewModel
                        )
                    }
                )
            }
            composable(route = RUTA_PROBADORES) {
                ProbadoresScreen(
                    viewModel = turnoViewModel,
                    onInforme = { navController.irSeccion(ruta = RUTA_INFORME) },
                    onCerrar = { navController.irSeccion(ruta = RUTA_CERRAR) },
                    onNuevoTurno = { navController.navigate(route = RUTA_CONFIGURAR) }
                )
            }
            composable(route = RUTA_CERRAR) {
                CerrarTurnoScreen(
                    viewModel = turnoViewModel,
                    onVolver = { navController.popBackStack() },
                    onCerrado = { navController.irSeccion(ruta = RUTA_INFORME) },
                    onInicio = { navController.irSeccion(ruta = RUTA_PROBADORES) },
                    onInforme = { navController.irSeccion(ruta = RUTA_INFORME) }
                )
            }
            composable(route = RUTA_INFORME) {
                InformeScreen(
                    viewModel = turnoViewModel,
                    onInicio = { navController.irSeccion(ruta = RUTA_PROBADORES) },
                    onCerrar = { navController.irSeccion(ruta = RUTA_CERRAR) },
                    onCerrarSesion = {
                        cerrarSesion(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            turnoViewModel = turnoViewModel
                        )
                    }
                )
            }
        }
    }


private fun cerrarSesion(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    turnoViewModel: TurnoViewModel
) {
    turnoViewModel.cerrarSesion {
        loginViewModel.limpiar()
        navController.navigate(route = RUTA_SELECCION_ROL) {
            popUpTo(id = navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
}

private fun NavHostController.irSeccion(ruta: String) {
    navigate(route = ruta) {
        popUpTo(route = RUTA_PROBADORES) { inclusive = false }
        launchSingleTop = true
    }
}

@Composable
fun BarraNavegacion(
    seleccionado: String,
    onInicio: () -> Unit,
    onInforme: () -> Unit,
    onCerrar: () -> Unit
) {
    NavigationBar(containerColor = Crema) {
        NavigationBarItem(
            selected = seleccionado == RUTA_PROBADORES,
            onClick = onInicio,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_inicio),
                    contentDescription = stringResource(id = R.string.cd_inicio),
                    modifier = Modifier.size(size = 24.dp),
                    tint = if (seleccionado == RUTA_PROBADORES) TextoOscuro else TextoSuave
                )
            },
            colors = coloresBarra()
        )
        NavigationBarItem(
            selected = seleccionado == RUTA_INFORME,
            onClick = onInforme,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendario),
                    contentDescription = stringResource(id = R.string.cd_calendario),
                    modifier = Modifier.size(size = 24.dp),
                    tint = if (seleccionado == RUTA_INFORME) TextoOscuro else TextoSuave
                )
            },
            colors = coloresBarra()
        )
        NavigationBarItem(
            selected = seleccionado == RUTA_CERRAR,
            onClick = onCerrar,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cuenta),
                    contentDescription = stringResource(id = R.string.cd_cerrar),
                    modifier = Modifier.size(size = 24.dp),
                    tint = if (seleccionado == RUTA_CERRAR) TextoOscuro else TextoSuave
                )
            },
            colors = coloresBarra()
        )
    }
}

@Composable
private fun coloresBarra() = NavigationBarItemDefaults.colors(
    selectedIconColor = TextoOscuro,
    unselectedIconColor = TextoSuave,
    indicatorColor = BordeSuave
)
