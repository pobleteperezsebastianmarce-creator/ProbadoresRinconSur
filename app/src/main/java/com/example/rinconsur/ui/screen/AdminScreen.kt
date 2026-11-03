package com.example.rinconsur.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.R
import com.example.rinconsur.data.InformeEntity
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.BordeSuave
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeBoton
import com.example.rinconsur.viewmodel.AvisoAdmin
import com.example.rinconsur.viewmodel.AdminTab
import com.example.rinconsur.viewmodel.AdminUiState
import com.example.rinconsur.viewmodel.AdminViewModel
import com.example.rinconsur.viewmodel.RangoFechaFiltro
import com.example.rinconsur.viewmodel.VentanaHorariaFiltro
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val padding = dimensionResource(id = R.dimen.padding_pantalla)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = padding, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.admin_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoOscuro,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            TabRow(
                selectedTabIndex = estado.tabActual.ordinal,
                containerColor = Blanco,
                contentColor = TextoOscuro,
                indicator = { tabPositions ->
                    if (estado.tabActual.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[estado.tabActual.ordinal]),
                            color = VerdeBoton
                        )
                    }
                }
            ) {
                Tab(
                    selected = estado.tabActual == AdminTab.USUARIOS,
                    onClick = { viewModel.onTabChange(tab = AdminTab.USUARIOS) },
                    text = {
                        Text(
                            text = stringResource(id = R.string.tab_crear_usuarios),
                            fontWeight = if (estado.tabActual == AdminTab.USUARIOS) FontWeight.Bold else FontWeight.Normal,
                            color = if (estado.tabActual == AdminTab.USUARIOS) TextoOscuro else TextoSuave,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = estado.tabActual == AdminTab.TIENDAS,
                    onClick = { viewModel.onTabChange(tab = AdminTab.TIENDAS) },
                    text = {
                        Text(
                            text = stringResource(id = R.string.tab_agregar_tiendas),
                            fontWeight = if (estado.tabActual == AdminTab.TIENDAS) FontWeight.Bold else FontWeight.Normal,
                            color = if (estado.tabActual == AdminTab.TIENDAS) TextoOscuro else TextoSuave,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = estado.tabActual == AdminTab.INFORMES,
                    onClick = { viewModel.onTabChange(tab = AdminTab.INFORMES) },
                    text = {
                        Text(
                            text = stringResource(id = R.string.tab_informes),
                            fontWeight = if (estado.tabActual == AdminTab.INFORMES) FontWeight.Bold else FontWeight.Normal,
                            color = if (estado.tabActual == AdminTab.INFORMES) TextoOscuro else TextoSuave,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            when (estado.tabActual) {
                AdminTab.USUARIOS -> TabCrearUsuarios(estado = estado, viewModel = viewModel)
                AdminTab.TIENDAS -> TabAgregarTiendas(estado = estado, viewModel = viewModel)
                AdminTab.INFORMES -> TabInformes(estado = estado, viewModel = viewModel)
            }

            Spacer(modifier = Modifier.height(height = 12.dp))

            TextButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.cerrar_sesion), color = TextoSuave)
            }
        }
        AvisosAdmin(estado = estado, viewModel = viewModel)
    }
}

@Composable
private fun AvisosAdmin(
    estado: AdminUiState,
    viewModel: AdminViewModel
) {
    when (estado.aviso) {
        AvisoAdmin.USUARIO_AGREGADO -> DialogoAceptar(
            texto = stringResource(id = R.string.usuario_agregado),
            onAceptar = viewModel::cerrarAviso
        )
        AvisoAdmin.TIENDA_CREADA -> DialogoAceptar(
            texto = stringResource(id = R.string.tienda_creada),
            onAceptar = viewModel::cerrarAviso
        )
        AvisoAdmin.USUARIO_ELIMINADO -> DialogoAceptar(
            texto = stringResource(id = R.string.usuario_eliminado),
            onAceptar = viewModel::cerrarAviso
        )
        AvisoAdmin.TIENDA_ELIMINADA -> DialogoAceptar(
            texto = stringResource(id = R.string.tienda_eliminada),
            onAceptar = viewModel::cerrarAviso
        )
        AvisoAdmin.CONFIRMAR_ELIMINAR_USUARIO -> AlertDialog(
            onDismissRequest = {},
            text = {
                Text(
                    text = stringResource(id = R.string.confirmar_eliminar_usuario),
                    color = TextoOscuro
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarEliminarTrabajador) {
                    Text(text = stringResource(id = R.string.eliminar))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarEliminarTrabajador) {
                    Text(text = stringResource(id = R.string.cancelar))
                }
            }
        )
        AvisoAdmin.CONFIRMAR_ELIMINAR_TIENDA -> AlertDialog(
            onDismissRequest = {},
            text = {
                Text(
                    text = stringResource(id = R.string.confirmar_eliminar_tienda),
                    color = TextoOscuro
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarEliminarTienda) {
                    Text(text = stringResource(id = R.string.eliminar))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarEliminarTienda) {
                    Text(text = stringResource(id = R.string.cancelar))
                }
            }
        )
        null -> Unit
    }
}

@Composable
private fun DialogoAceptar(
    texto: String,
    onAceptar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(
                text = texto,
                color = TextoOscuro
            )
        },
        confirmButton = {
            TextButton(onClick = onAceptar) {
                Text(text = stringResource(id = R.string.aceptar))
            }
        }
    )
}

@Composable
private fun TabCrearUsuarios(
    estado: AdminUiState,
    viewModel: AdminViewModel
) {
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)

    Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
        CampoAdmin(
            valor = estado.nombreWorker,
            etiqueta = stringResource(id = R.string.nombre_trabajador),
            error = estado.errorNombreWorker,
            onCambio = viewModel::onNombreWorkerChange
        )

        CampoAdmin(
            valor = estado.apellidoWorker,
            etiqueta = stringResource(id = R.string.apellido_trabajador),
            error = estado.errorApellidoWorker,
            onCambio = viewModel::onApellidoWorkerChange
        )

        CampoAdmin(
            valor = estado.usuarioWorker,
            etiqueta = stringResource(id = R.string.usuario),
            error = estado.errorUsuarioWorker,
            onCambio = viewModel::onUsuarioWorkerChange,
            pista = if (!estado.usuarioEditadoManualmente && estado.usuarioWorker.isNotBlank()) {
                "Autogenerado con 2 primeras letras del nombre + apellido"
            } else {
                null
            }
        )

        CampoAdmin(
            valor = estado.claveWorker,
            etiqueta = stringResource(id = R.string.contrasena_provisoria),
            error = estado.errorClaveWorker,
            esClave = true,
            onCambio = viewModel::onClaveWorkerChange
        )

        if (estado.esSuperAdmin) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(
                        text = stringResource(id = R.string.es_administrador),
                        fontWeight = FontWeight.SemiBold,
                        color = TextoOscuro
                    )
                    Text(
                        text = stringResource(id = R.string.es_administrador_desc),
                        fontSize = 12.sp,
                        color = TextoSuave
                    )
                }
                Switch(
                    checked = estado.esAdminWorker,
                    onCheckedChange = viewModel::onEsAdminWorkerChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Blanco,
                        checkedTrackColor = VerdeBoton
                    )
                )
            }
        }

        Button(
            onClick = viewModel::crearTrabajador,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = altoBoton),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeBoton,
                contentColor = Blanco
            )
        ) {
            Text(text = stringResource(id = R.string.crear_trabajador))
        }

        Text(
            text = stringResource(id = R.string.trabajadores_titulo),
            color = TextoOscuro,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        CampoBusqueda(
            query = estado.busquedaWorker,
            placeholder = stringResource(id = R.string.buscar_trabajadores),
            onQueryChange = viewModel::onBusquedaWorkerChange
        )

        val lista = estado.trabajadoresFiltrados

        if (lista.isEmpty()) {
            Text(
                text = if (estado.busquedaWorker.isNotBlank()) {
                    stringResource(id = R.string.sin_busqueda_resultados)
                } else {
                    stringResource(id = R.string.sin_trabajadores)
                },
                color = TextoSuave,
                fontSize = 14.sp
            )
        } else {
            lista.forEach { trabajador ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    border = BorderStroke(width = 1.dp, color = BordeSuave)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(weight = 1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (trabajador.nombre.isNotBlank() || trabajador.apellido.isNotBlank()) {
                                        "${trabajador.nombre} ${trabajador.apellido}".trim()
                                    } else {
                                        trabajador.usuario
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                if (trabajador.esAdminRol()) {
                                    Text(
                                        text = stringResource(id = R.string.rol_admin_badge),
                                        fontSize = 11.sp,
                                        color = VerdeBoton,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.rol_trabajador_badge),
                                        fontSize = 11.sp,
                                        color = TextoSuave,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                if (trabajador.claveProvisoria) {
                                    Text(
                                        text = stringResource(id = R.string.clave_provisoria_badge),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(height = 2.dp))
                            Text(
                                text = "Usuario: ${trabajador.usuario}",
                                color = TextoSuave,
                                fontSize = 13.sp
                            )
                        }

                        if (trabajador.id > 0) {
                            IconButton(onClick = { viewModel.eliminarTrabajador(trabajador) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.eliminar_trabajador),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(size = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabAgregarTiendas(
    estado: AdminUiState,
    viewModel: AdminViewModel
) {
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)
    var menuCategoriaAbierto by remember { mutableStateOf(value = false) }

    Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
        CampoAdmin(
            valor = estado.nombreTienda,
            etiqueta = stringResource(id = R.string.nombre_tienda),
            error = estado.errorNombreTienda,
            onCambio = viewModel::onNombreTiendaChange
        )

        Text(
            text = stringResource(id = R.string.categoria_tienda),
            color = TextoSuave,
            fontSize = 14.sp
        )

        ExposedDropdownMenuBox(
            expanded = menuCategoriaAbierto,
            onExpandedChange = { menuCategoriaAbierto = it }
        ) {
            OutlinedTextField(
                value = estado.categoriaTienda,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriaAbierto)
                },
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth(),
                shape = RoundedCornerShape(size = 10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Blanco,
                    unfocusedContainerColor = Blanco,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedBorderColor = TextoOscuro,
                    unfocusedBorderColor = BordeSuave,
                    focusedTrailingIconColor = TextoOscuro,
                    unfocusedTrailingIconColor = TextoOscuro
                )
            )
            ExposedDropdownMenu(
                expanded = menuCategoriaAbierto,
                onDismissRequest = { menuCategoriaAbierto = false }
            ) {
                estado.categoriasDisponibles.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(text = cat) },
                        onClick = {
                            viewModel.onCategoriaTiendaChange(valor = cat)
                            menuCategoriaAbierto = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.tiene_probadores),
                fontWeight = FontWeight.SemiBold,
                color = TextoOscuro,
                modifier = Modifier.weight(weight = 1f)
            )
            Switch(
                checked = estado.tieneProbadoresTienda,
                onCheckedChange = viewModel::onTieneProbadoresTiendaChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Blanco,
                    checkedTrackColor = VerdeBoton
                )
            )
        }

        Button(
            onClick = viewModel::crearTienda,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = altoBoton),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeBoton,
                contentColor = Blanco
            )
        ) {
            Text(text = stringResource(id = R.string.agregar_tienda_btn))
        }

        Text(
            text = stringResource(id = R.string.tiendas_titulo),
            color = TextoOscuro,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        CampoBusqueda(
            query = estado.busquedaTienda,
            placeholder = stringResource(id = R.string.buscar_tiendas),
            onQueryChange = viewModel::onBusquedaTiendaChange
        )

        val lista = estado.tiendasFiltradas

        if (lista.isEmpty()) {
            Text(
                text = if (estado.busquedaTienda.isNotBlank()) {
                    stringResource(id = R.string.sin_busqueda_resultados)
                } else {
                    stringResource(id = R.string.sin_tiendas)
                },
                color = TextoSuave,
                fontSize = 14.sp
            )
        } else {
            lista.forEach { tienda ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    border = BorderStroke(width = 1.dp, color = BordeSuave)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(weight = 1f)) {
                            Text(
                                text = tienda.nombre,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            Spacer(modifier = Modifier.height(height = 2.dp))
                            Text(
                                text = "Categoría: ${tienda.categoria}",
                                color = TextoSuave,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = { viewModel.eliminarTienda(tienda) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.eliminar_tienda),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(size = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabInformes(
    estado: AdminUiState,
    viewModel: AdminViewModel
) {
    var menuTiendaAbierto by remember { mutableStateOf(value = false) }
    var menuTrabajadorAbierto by remember { mutableStateOf(value = false) }
    var menuFechaAbierto by remember { mutableStateOf(value = false) }
    var menuVentanaAbierto by remember { mutableStateOf(value = false) }

    Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
        Text(
            text = "Filtros de Búsqueda",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )

        // Filtro Tienda & Trabajador
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = stringResource(id = R.string.filtro_tienda),
                    fontSize = 12.sp,
                    color = TextoSuave
                )
                ExposedDropdownMenuBox(
                    expanded = menuTiendaAbierto,
                    onExpandedChange = { menuTiendaAbierto = it }
                ) {
                    OutlinedTextField(
                        value = estado.filtroTiendaInforme,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTiendaAbierto) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp),
                        colors = coloresCampoSelect()
                    )
                    ExposedDropdownMenu(
                        expanded = menuTiendaAbierto,
                        onDismissRequest = { menuTiendaAbierto = false }
                    ) {
                        estado.opcionesTiendasFiltro.forEach { opción ->
                            DropdownMenuItem(
                                text = { Text(text = opción) },
                                onClick = {
                                    viewModel.onFiltroTiendaInformeChange(valor = opción)
                                    menuTiendaAbierto = false
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = stringResource(id = R.string.filtro_trabajador),
                    fontSize = 12.sp,
                    color = TextoSuave
                )
                ExposedDropdownMenuBox(
                    expanded = menuTrabajadorAbierto,
                    onExpandedChange = { menuTrabajadorAbierto = it }
                ) {
                    OutlinedTextField(
                        value = estado.filtroTrabajadorInforme,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTrabajadorAbierto) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp),
                        colors = coloresCampoSelect()
                    )
                    ExposedDropdownMenu(
                        expanded = menuTrabajadorAbierto,
                        onDismissRequest = { menuTrabajadorAbierto = false }
                    ) {
                        estado.opcionesTrabajadoresFiltro.forEach { opción ->
                            DropdownMenuItem(
                                text = { Text(text = opción) },
                                onClick = {
                                    viewModel.onFiltroTrabajadorInformeChange(valor = opción)
                                    menuTrabajadorAbierto = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Filtro Fecha & Ventana Horaria
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = stringResource(id = R.string.filtro_rango_fecha),
                    fontSize = 12.sp,
                    color = TextoSuave
                )
                ExposedDropdownMenuBox(
                    expanded = menuFechaAbierto,
                    onExpandedChange = { menuFechaAbierto = it }
                ) {
                    OutlinedTextField(
                        value = textoRango(rango = estado.filtroRangoFecha),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuFechaAbierto) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp),
                        colors = coloresCampoSelect()
                    )
                    ExposedDropdownMenu(
                        expanded = menuFechaAbierto,
                        onDismissRequest = { menuFechaAbierto = false }
                    ) {
                        RangoFechaFiltro.values().forEach { rango ->
                            DropdownMenuItem(
                                text = { Text(text = textoRango(rango = rango)) },
                                onClick = {
                                    viewModel.onFiltroRangoFechaChange(rango = rango)
                                    menuFechaAbierto = false
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = stringResource(id = R.string.filtro_ventana_horaria),
                    fontSize = 12.sp,
                    color = TextoSuave
                )
                ExposedDropdownMenuBox(
                    expanded = menuVentanaAbierto,
                    onExpandedChange = { menuVentanaAbierto = it }
                ) {
                    OutlinedTextField(
                        value = textoVentana(ventana = estado.filtroVentanaHoraria),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuVentanaAbierto) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp),
                        colors = coloresCampoSelect()
                    )
                    ExposedDropdownMenu(
                        expanded = menuVentanaAbierto,
                        onDismissRequest = { menuVentanaAbierto = false }
                    ) {
                        VentanaHorariaFiltro.values().forEach { ventana ->
                            DropdownMenuItem(
                                text = { Text(text = textoVentana(ventana = ventana)) },
                                onClick = {
                                    viewModel.onFiltroVentanaHorariaChange(ventana = ventana)
                                    menuVentanaAbierto = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Resumen Totales
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco),
            border = BorderStroke(width = 1.dp, color = BordeSuave)
        ) {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.totales_filtrados),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextoOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ItemMetricaTotal(
                        titulo = stringResource(id = R.string.total_personas),
                        valor = estado.totalPersonasFiltrado.toString()
                    )
                    ItemMetricaTotal(
                        titulo = stringResource(id = R.string.total_probadores_usados),
                        valor = estado.totalUsadosFiltrado.toString()
                    )
                    ItemMetricaTotal(
                        titulo = stringResource(id = R.string.total_excesos),
                        valor = estado.totalExcesosFiltrado.toString()
                    )
                }
            }
        }

        val lista = estado.informesFiltrados

        if (lista.isEmpty()) {
            Text(
                text = if (estado.informes.isEmpty()) {
                    stringResource(id = R.string.sin_informes_registrados)
                } else {
                    stringResource(id = R.string.sin_informes_filtrados)
                },
                color = TextoSuave,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            lista.forEach { informe ->
                TarjetaInformeAdmin(informe = informe)
            }
        }
    }
}

@Composable
private fun ItemMetricaTotal(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VerdeBoton)
        Text(text = titulo, fontSize = 11.sp, color = TextoSuave, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TarjetaInformeAdmin(informe: InformeEntity) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaTexto = formatoFecha.format(Date(informe.fechaMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        border = BorderStroke(width = 1.dp, color = BordeSuave)
    ) {
        Column(modifier = Modifier.padding(all = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fechaTexto,
                    fontSize = 12.sp,
                    color = TextoSuave,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${informe.tienda} - ${informe.seccion}",
                    fontSize = 11.sp,
                    color = VerdeBoton,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(height = 6.dp))
            Text(
                text = "Trabajador: ${informe.nombreTrabajador.ifBlank { informe.usuario }}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextoOscuro
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Personas: ${informe.personasIngresaron}",
                    fontSize = 13.sp,
                    color = TextoOscuro
                )
                Text(
                    text = "Usados: ${informe.probadoresUsados}",
                    fontSize = 13.sp,
                    color = TextoOscuro
                )
                Text(
                    text = "Excesos: ${informe.excesosTiempo}",
                    fontSize = 13.sp,
                    color = if (informe.excesosTiempo > 0) MaterialTheme.colorScheme.error else TextoOscuro
                )
            }
        }
    }
}

@Composable
private fun textoRango(rango: RangoFechaFiltro): String = when (rango) {
    RangoFechaFiltro.TODOS -> stringResource(id = R.string.opcion_todos)
    RangoFechaFiltro.HOY -> stringResource(id = R.string.rango_hoy)
    RangoFechaFiltro.ULTIMOS_7_DIAS -> stringResource(id = R.string.rango_ultimos_7)
    RangoFechaFiltro.ULTIMOS_30_DIAS -> stringResource(id = R.string.rango_ultimos_30)
}

@Composable
private fun textoVentana(ventana: VentanaHorariaFiltro): String = when (ventana) {
    VentanaHorariaFiltro.TODO_EL_DIA -> stringResource(id = R.string.ventana_todo_dia)
    VentanaHorariaFiltro.MANANA_08_14 -> stringResource(id = R.string.ventana_manana)
    VentanaHorariaFiltro.TARDE_14_20 -> stringResource(id = R.string.ventana_tarde)
    VentanaHorariaFiltro.NOCHE_20_08 -> stringResource(id = R.string.ventana_noche)
}

@Composable
private fun coloresCampoSelect() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Blanco,
    unfocusedContainerColor = Blanco,
    focusedTextColor = TextoOscuro,
    unfocusedTextColor = TextoOscuro,
    focusedBorderColor = TextoOscuro,
    unfocusedBorderColor = BordeSuave,
    focusedTrailingIconColor = TextoOscuro,
    unfocusedTrailingIconColor = TextoOscuro
)

@Composable
private fun CampoBusqueda(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = placeholder,
                tint = TextoSuave,
                modifier = Modifier.size(size = 20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.limpiar_busqueda),
                        tint = TextoSuave,
                        modifier = Modifier.size(size = 18.dp)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Blanco,
            unfocusedContainerColor = Blanco,
            focusedBorderColor = TextoOscuro,
            unfocusedBorderColor = BordeSuave,
            cursorColor = TextoOscuro,
            focusedTextColor = TextoOscuro,
            unfocusedTextColor = TextoOscuro
        )
    )
}

@Composable
private fun CampoAdmin(
    valor: String,
    etiqueta: String,
    error: String?,
    onCambio: (String) -> Unit,
    esClave: Boolean = false,
    pista: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(text = etiqueta) },
        singleLine = true,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            } else if (pista != null) {
                Text(text = pista, color = TextoSuave, fontSize = 11.sp)
            }
        },
        visualTransformation = if (esClave) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esClave) KeyboardType.Password else KeyboardType.Text
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TextoOscuro,
            unfocusedBorderColor = TextoOscuro.copy(alpha = 0.35f),
            focusedLabelColor = TextoOscuro,
            unfocusedLabelColor = TextoSuave,
            cursorColor = TextoOscuro,
            focusedTextColor = TextoOscuro,
            unfocusedTextColor = TextoOscuro
        )
    )
}

