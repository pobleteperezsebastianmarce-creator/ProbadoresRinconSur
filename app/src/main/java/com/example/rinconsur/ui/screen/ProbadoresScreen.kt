package com.example.rinconsur.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.rinconsur.R
import com.example.rinconsur.model.DialogoTurno
import com.example.rinconsur.model.FiltroProbadores
import com.example.rinconsur.model.aplicarFiltro
import com.example.rinconsur.model.formatoTiempo
import com.example.rinconsur.navigation.BarraNavegacion
import com.example.rinconsur.navigation.RUTA_PROBADORES
import com.example.rinconsur.ui.components.LiberarDialog
import com.example.rinconsur.ui.components.AsignarDialog
import com.example.rinconsur.ui.components.ProbadorCard
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.RojoExceso
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.viewmodel.TurnoViewModel

@Composable
fun ProbadoresScreen(
    viewModel: TurnoViewModel,
    onInforme: () -> Unit,
    onCerrar: () -> Unit,
    onNuevoTurno: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val contexto = LocalContext.current
    val pedirPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.onPermisoNotificaciones(concedido = concedido)
    }

    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (concedido) {
                viewModel.onPermisoNotificaciones(concedido = true)
            } else {
                pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.onPermisoNotificaciones(concedido = true)
        }
    }

    val visibles = estado.probadores.aplicarFiltro(filtro = estado.filtro)
    val alertas = estado.probadores.count { it.enExceso }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::onCampana) {
                    BadgedBox(
                        badge = {
                            if (alertas > 0) {
                                Text(
                                    text = alertas.toString(),
                                    color = Blanco,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(percent = 50))
                                        .background(color = RojoExceso)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_campana),
                            contentDescription = stringResource(id = R.string.cd_campana),
                            tint = TextoOscuro
                        )
                    }
                }
                Text(
                    text = stringResource(id = R.string.titulo_probadores),
                    modifier = Modifier.weight(weight = 1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoOscuro
                )
                IconButton(onClick = viewModel::onAvatar) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_avatar),
                        contentDescription = stringResource(id = R.string.cd_avatar),
                        tint = TextoOscuro
                    )
                }
            }

            AnimatedVisibility(visible = estado.mensajeBreve != null) {
                Text(
                    text = estado.mensajeBreve.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    textAlign = TextAlign.Center,
                    color = TextoOscuro,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 6.dp)
            ) {
                FiltroProbadores.entries.forEach { filtro ->
                    val activo = filtro == estado.filtro
                    Text(
                        text = etiquetaFiltro(filtro = filtro),
                        modifier = Modifier
                            .shadow(
                                elevation = if (activo) 2.dp else 0.dp,
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .clip(shape = RoundedCornerShape(percent = 50))
                            .background(color = if (activo) Blanco else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { viewModel.onFiltro(filtro = filtro) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = TextoOscuro,
                        fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.hint_acciones),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = 6.dp, bottom = 8.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(size = 12.dp))
                    .clip(shape = RoundedCornerShape(size = 12.dp))
                    .background(color = Blanco)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                color = TextoOscuro,
                fontSize = 13.sp
            )

            if (estado.avisoNotificaciones != null) {
                Text(
                    text = estado.avisoNotificaciones.orEmpty(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = TextoSuave,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (!estado.turnoActivo) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.turno_cerrado),
                        color = TextoOscuro
                    )
                    OutlinedButton(
                        onClick = onNuevoTurno,
                        shape = RoundedCornerShape(percent = 50),
                        border = BorderStroke(width = 1.dp, color = TextoOscuro),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro)
                    ) {
                        Text(text = stringResource(id = R.string.nuevo_turno))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth()
            ) {
                if (visibles.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.sin_resultados),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 24.dp),
                        textAlign = TextAlign.Center,
                        color = TextoOscuro
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count = 3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        items(items = visibles, key = { it.numero }) { probador ->
                            ProbadorCard(
                                probador = probador,
                                tiempo = formatoTiempo(
                                    millis = probador.millisTranscurridos(ahora = estado.ahoraMillis)
                                ),
                                onClick = { viewModel.onProbadorClick(numero = probador.numero) }
                            )
                        }
                    }
                }
            }

            BarraNavegacion(
                seleccionado = RUTA_PROBADORES,
                onInicio = {},
                onInforme = onInforme,
                onCerrar = onCerrar
            )
        }

        AsignarDialog(
            visible = estado.dialogo == DialogoTurno.ASIGNAR,
            numero = estado.probadorDialogo ?: 0,
            onConfirmar = viewModel::confirmarAsignacion,
            onCancelar = viewModel::cancelarDialogo
        )
        LiberarDialog(
            visible = estado.dialogo == DialogoTurno.LIBERAR,
            numero = estado.probadorDialogo ?: 0,
            prendas = estado.probadores
                .firstOrNull { it.numero == estado.probadorDialogo }
                ?.prendas
                ?: 0,
            onConfirmar = viewModel::confirmarLiberacion,
            onCancelar = viewModel::cancelarDialogo
        )
    }
}

@Composable
private fun etiquetaFiltro(filtro: FiltroProbadores): String {
    val id = when (filtro) {
        FiltroProbadores.TODOS -> R.string.filtro_todos
        FiltroProbadores.DISPONIBLES -> R.string.filtro_disponibles
        FiltroProbadores.OCUPADOS -> R.string.filtro_ocupados
        FiltroProbadores.ALERTAS -> R.string.filtro_alertas
    }
    return stringResource(id = id)
}
