package com.example.rinconsur.ui.screen

import android.app.Activity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.R
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeBoton
import kotlinx.coroutines.delay

@Composable
fun SeleccionRolScreen(
    onSeleccionarAdmin: () -> Unit,
    onSeleccionarTrabajador: () -> Unit
) {
    val padding = dimensionResource(id = R.dimen.padding_pantalla)
    val espacio = dimensionResource(id = R.dimen.espacio_campos)
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)
    val context = LocalContext.current

    var animarLogo by remember { mutableStateOf(false) }
    var animarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animarLogo = true
        delay(1800)
        animarFormulario = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = padding, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = espacio)
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))

        AnimatedVisibility(
            visible = animarLogo,
            enter = fadeIn(animationSpec = tween(450)) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            MarcaLogin()
        }

        AnimatedVisibility(
            visible = animarFormulario,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = espacio),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.seleccion_rol_titulo),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextoOscuro,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.seleccion_rol_subtitulo),
                    color = TextoSuave,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(height = 8.dp))

                TarjetaRolOption(
                    titulo = stringResource(id = R.string.rol_trabajador_titulo),
                    descripcion = stringResource(id = R.string.rol_trabajador_desc),
                    iconoResId = R.drawable.ic_persona,
                    onClick = onSeleccionarTrabajador
                )

                TarjetaRolOption(
                    titulo = stringResource(id = R.string.rol_admin_titulo),
                    descripcion = stringResource(id = R.string.rol_admin_desc),
                    iconoResId = R.drawable.ic_cuenta,
                    onClick = onSeleccionarAdmin
                )


            }
        }
    }
}

@Composable
private fun TarjetaRolOption(
    titulo: String,
    descripcion: String,
    iconoResId: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco,
            contentColor = TextoOscuro
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconoResId),
                contentDescription = titulo,
                tint = VerdeBoton,
                modifier = Modifier.size(size = 36.dp)
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(height = 4.dp))
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = TextoSuave
                )
            }
        }
    }
}