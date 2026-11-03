package com.example.rinconsur.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rinconsur.R
import com.example.rinconsur.model.Informe
import com.example.rinconsur.model.ResumenCierre
import com.example.rinconsur.navigation.BarraNavegacion
import com.example.rinconsur.navigation.RUTA_CERRAR
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.BordeSuave
import com.example.rinconsur.ui.theme.GrisCerrar
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.viewmodel.TurnoViewModel

@Composable
fun CerrarTurnoScreen(
    viewModel: TurnoViewModel,
    onVolver: () -> Unit,
    onCerrado: () -> Unit,
    onInicio: () -> Unit,
    onInforme: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val resumen = viewModel.resumenCerrar()
    val padding = dimensionResource(id = R.dimen.padding_pantalla)
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)
    val nota = if (estado.turnoActivo) {
        stringResource(id = R.string.informe_en_curso)
    } else {
        stringResource(id = R.string.informe_guardado)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .verticalScroll(state = rememberScrollState())
                .padding(all = padding),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.termino_turno),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoOscuro,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.seccion_actual, estado.seccion),
                color = TextoSuave,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            ResumenTermino(resumen = resumen, nota = nota)
            Button(
                onClick = { viewModel.cerrarTurno(onListo = onCerrado) },
                enabled = estado.turnoActivo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = altoBoton),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrisCerrar,
                    contentColor = Blanco,
                    disabledContainerColor = GrisCerrar.copy(alpha = 0.4f),
                    disabledContentColor = Blanco
                )
            ) {
                Text(text = stringResource(id = R.string.cerrar_turno))
            }
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = altoBoton),
                shape = RoundedCornerShape(percent = 50),
                border = BorderStroke(width = 1.dp, color = TextoOscuro),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro)
            ) {
                Text(text = stringResource(id = R.string.volver))
            }
        }
        BarraNavegacion(
            seleccionado = RUTA_CERRAR,
            onInicio = onInicio,
            onInforme = onInforme,
            onCerrar = {}
        )
    }
}

@Composable
private fun ResumenTermino(resumen: ResumenCierre, nota: String) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 10.dp)) {
        Text(text = nota, color = TextoSuave)
        FilaResumen(
            etiqueta = stringResource(id = R.string.probadores_utilizados),
            valor = resumen.utilizados.toString()
        )
        FilaResumen(
            etiqueta = stringResource(id = R.string.probadores_excedidos),
            valor = resumen.excedidos.toString()
        )
        FilaResumen(
            etiqueta = stringResource(id = R.string.probadores_dentro_tiempo),
            valor = resumen.dentroDelTiempo.toString()
        )
    }
}

@Composable
fun TarjetaResumen(informe: Informe, nota: String) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 10.dp)) {
        Text(text = nota, color = TextoSuave)
        FilaResumen(
            etiqueta = stringResource(id = R.string.probadores_usados),
            valor = informe.probadoresUsados.toString()
        )
        FilaResumen(
            etiqueta = stringResource(id = R.string.excesos_doce),
            valor = informe.excesoTiempo.toString()
        )
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 18.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Blanco),
        border = BorderStroke(width = 1.dp, color = BordeSuave)
    ) {
        Column(modifier = Modifier.padding(all = 18.dp)) {
            Text(text = etiqueta, color = TextoSuave)
            Text(
                text = valor,
                color = TextoOscuro,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
