package com.example.rinconsur.ui.screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.R
import com.example.rinconsur.ui.theme.Blanco
import com.example.rinconsur.ui.theme.TextoOscuro
import com.example.rinconsur.ui.theme.TextoSuave
import com.example.rinconsur.ui.theme.VerdeBoton
import com.example.rinconsur.viewmodel.LoginViewModel

@Composable
fun CambiarClaveScreen(
    viewModel: LoginViewModel,
    onExito: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val padding = dimensionResource(id = R.dimen.padding_pantalla)
    val altoBoton = dimensionResource(id = R.dimen.alto_boton)
    val espacio = dimensionResource(id = R.dimen.espacio_campos)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = padding, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = espacio)
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))
        MarcaLogin()

        Text(
            text = stringResource(id = R.string.cambiar_clave_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.cambiar_clave_subtitulo),
            color = TextoSuave,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = estado.nuevaClave,
            onValueChange = viewModel::onNuevaClaveChange,
            label = { Text(text = stringResource(id = R.string.nueva_clave)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_candado),
                    contentDescription = stringResource(id = R.string.nueva_clave),
                    tint = TextoOscuro
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = estado.errorNuevaClave != null,
            supportingText = {
                estado.errorNuevaClave?.let { mensaje ->
                    Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 10.dp),
            colors = coloresCampo()
        )

        OutlinedTextField(
            value = estado.confirmarClave,
            onValueChange = viewModel::onConfirmarClaveChange,
            label = { Text(text = stringResource(id = R.string.confirmar_clave)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_candado),
                    contentDescription = stringResource(id = R.string.confirmar_clave),
                    tint = TextoOscuro
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = estado.errorConfirmarClave != null,
            supportingText = {
                estado.errorConfirmarClave?.let { mensaje ->
                    Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.guardarNuevaClave(onExito = onExito) }
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 10.dp),
            colors = coloresCampo()
        )

        Button(
            onClick = { viewModel.guardarNuevaClave(onExito = onExito) },
            modifier = Modifier
                .fillMaxWidth()
                .height(height = altoBoton),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeBoton,
                contentColor = Blanco
            )
        ) {
            Text(text = stringResource(id = R.string.guardar_clave_btn))
        }
    }
}

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
