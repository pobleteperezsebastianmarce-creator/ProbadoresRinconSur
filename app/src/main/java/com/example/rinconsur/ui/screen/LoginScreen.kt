package com.example.rinconsur.ui.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rinconsur.viewmodel.LoginViewModel

@Composable
fun  LoginScreen(
    viewModel: LoginViewModel,
    onLoginOk: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rincon Sur",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color= MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text ="Gestion de Probadores",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = estado.usuario,
            onValueChange = {viewModel.onUsuarioChange(it)},
            label = {Text("Usuario")},
            placeholder = {Text("Ingrese su usuario o Rut")},
            singleLine = true,
            isError = estado.errores.usuario != null,
            supportingText = {
                estado.errores.usuario?.let{ mensaje ->
                    Text(text = mensaje,color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)

        )

        OutlinedTextField(
            value = estado.clave,
            onValueChange = {viewModel.onClaveChange(it)},
            label= {Text("Contraseña")},
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
                onDone = { viewModel.ingresar(onNavegar = onLoginOk) }
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = { viewModel.ingresar(onNavegar =onLoginOk)},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(percent= 50)
        ) {
            Text(text = "Iniciar Sesion", fontSize = 16.sp)
        }

        TextButton(onClick = { viewModel.onOlvidoClave()}){
            Text(text ="Olvidaste tu contraseña?")
        }

        AnimatedVisibility(visible = estado.mensajeOlvido != null) {
            Text(
                text = estado.mensajeOlvido.orEmpty(),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }

    }
}
