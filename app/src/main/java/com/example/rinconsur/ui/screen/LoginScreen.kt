package com.example.rinconsur.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.R
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeBoton
import com.example.rinconsur.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    esAdminMode: Boolean = false,
    onVolver: (() -> Unit)? = null,
    onLoginOk: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val padding = dimensionResource(id = R.dimen.padding_pantalla)
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)
    val espacio = dimensionResource(id = R.dimen.espacio_campos)

    var animarLogo by remember { mutableStateOf(false) }
    var animarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animarLogo = true
        delay(1800)
        animarFormulario = true
    }

    val submitLogin = {
        if (esAdminMode) {
            viewModel.intentarLoginAdmin(onOk = onLoginOk)
        } else {
            viewModel.intentarLoginTrabajador(onOk = onLoginOk)
        }
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
                    text = if (esAdminMode) {
                        stringResource(id = R.string.login_admin_titulo)
                    } else {
                        stringResource(id = R.string.bienvenido)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextoOscuro,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (esAdminMode) {
                        stringResource(id = R.string.login_admin_subtitulo)
                    } else {
                        stringResource(id = R.string.login_trabajador_subtitulo)
                    },
                    color = TextoSuave,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = estado.usuario,
                    onValueChange = viewModel::onUsuarioChange,
                    label = { Text(text = stringResource(id = R.string.usuario)) },
                    placeholder = { Text(text = stringResource(id = R.string.usuario_hint)) },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.ic_persona),
                            contentDescription = stringResource(id = R.string.cd_usuario),
                            tint = TextoOscuro
                        )
                    },
                    singleLine = true,
                    isError = estado.errores.usuario != null,
                    supportingText = {
                        estado.errores.usuario?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = 10.dp),
                    colors = coloresCampo()
                )

                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text(text = stringResource(id = R.string.contrasena)) },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.ic_candado),
                            contentDescription = stringResource(id = R.string.cd_contrasena),
                            tint = TextoOscuro
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = estado.errores.clave != null,
                    supportingText = {
                        estado.errores.clave?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitLogin() }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = 10.dp),
                    colors = coloresCampo()
                )

                Button(
                    onClick = { submitLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = altoBoton),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeBoton,
                        contentColor = Blanco
                    )
                ) {
                    Text(text = stringResource(id = R.string.login))
                }

                if (onVolver != null) {
                    TextButton(onClick = {
                        viewModel.limpiar()
                        onVolver()
                    }) {
                        Text(
                            text = stringResource(id = R.string.cambiar_rol),
                            color = TextoSuave,
                            fontSize = 14.sp
                        )
                    }
                }

                TextButton(onClick = viewModel::onOlvidoClave) {
                    Text(
                        text = stringResource(id = R.string.olvidaste),
                        color = TextoOscuro
                    )
                }

                AnimatedVisibility(visible = estado.mensajeOlvido != null) {
                    Text(
                        text = estado.mensajeOlvido.orEmpty(),
                        color = TextoOscuro,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MarcaLogin() {
    val rincon = stringResource(id = R.string.logo_rincon)
    val transicion = rememberInfiniteTransition(label = "latido")
    val escala by transicion.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(escala)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = VerdeBoton)) {
                    append(rincon.take(1))
                }
                withStyle(style = SpanStyle(color = TextoOscuro)) {
                    append(rincon.drop(1))
                }
            },
            style = estiloMarca,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.logo_sur),
            color = TextoOscuro,
            style = estiloMarca,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(height = 4.dp))
        Text(
            text = "Gestión de Probadores",
            color = TextoSuave,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

private val estiloMarca = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 26.sp,
    lineHeight = 28.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TextoOscuro,
    unfocusedBorderColor = TextoOscuro.copy(alpha = 0.35f),
    focusedLabelColor = TextoOscuro,
    unfocusedLabelColor = TextoSuave,
    cursorColor = TextoOscuro,
    focusedTextColor = TextoOscuro,
    unfocusedTextColor = TextoOscuro
)