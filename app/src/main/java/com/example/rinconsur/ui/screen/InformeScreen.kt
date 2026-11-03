package com.example.rinconsur.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rinconsur.R
import com.example.rinconsur.navigation.BarraNavegacion
import com.example.rinconsur.navigation.RUTA_INFORME
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.BordeSuave
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.viewmodel.TurnoViewModel

@Composable
fun InformeScreen(
    viewModel: TurnoViewModel,
    onInicio: () -> Unit,
    onCerrar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val padding = dimensionResource(id = R.dimen.padding_pantalla)
    val guardado = estado.informeGuardado

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .verticalScroll(state = rememberScrollState())
                .padding(all = padding),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.informe_titulo),
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
            if (guardado == null) {
                Text(
                    text = stringResource(id = R.string.informe_vacio),
                    color = TextoOscuro,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(space = 10.dp)) {
                    TarjetaResumen(
                        informe = guardado,
                        nota = stringResource(id = R.string.informe_guardado)
                    )
                    FilaInforme(
                        etiqueta = stringResource(id = R.string.personas_ingresaron),
                        valor = guardado.personasIngresaron.toString()
                    )
                }
            }
            TextButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.cerrar_sesion), color = TextoSuave)
            }
        }
        BarraNavegacion(
            seleccionado = RUTA_INFORME,
            onInicio = onInicio,
            onInforme = {},
            onCerrar = onCerrar
        )
    }
}

@Composable
private fun FilaInforme(etiqueta: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
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
