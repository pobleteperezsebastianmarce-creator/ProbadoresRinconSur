package com.example.rinconsur.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.R
import com.example.rinconsur.model.EstadoProbador
import com.example.rinconsur.model.Probador
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.BordeSuave
import com.example.rinconsur.ui.theme.NaranjaOcupado
import com.example.rinconsur.ui.theme.RojoExceso
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeDisponible

@Composable
fun ProbadorCard(
    probador: Probador,
    tiempo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val estado = probador.estadoVisual()
    val color by animateColorAsState(
        targetValue = when (estado) {
            EstadoProbador.DISPONIBLE -> VerdeDisponible
            EstadoProbador.OCUPADO -> NaranjaOcupado
            EstadoProbador.EXCESO -> RojoExceso
        },
        animationSpec = tween(durationMillis = 500),
        label = "colorEstado"
    )
    val etiqueta = when (estado) {
        EstadoProbador.DISPONIBLE -> stringResource(id = R.string.estado_disponible)
        EstadoProbador.OCUPADO -> stringResource(id = R.string.estado_ocupado)
        EstadoProbador.EXCESO -> stringResource(id = R.string.estado_exceso)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(size = 14.dp),
        color = Blanco,
        shadowElevation = 1.dp,
        border = BorderStroke(width = 1.dp, color = BordeSuave)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 4.dp)
                    .background(color = color)
            )
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.probador_nombre, probador.numero),
                    color = TextoOscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                if (probador.ocupado) {
                    Text(
                        text = tiempo,
                        color = TextoSuave,
                        fontSize = 11.sp
                    )
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.prendas_cantidad,
                            count = probador.prendas,
                            probador.prendas
                        ),
                        color = TextoSuave,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = color
                ) {
                    Text(
                        text = etiqueta,
                        color = Blanco,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
