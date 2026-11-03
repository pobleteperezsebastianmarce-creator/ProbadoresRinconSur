package com.example.rinconsur.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = VerdeBoton,
    onPrimary = Blanco,
    secondary = TextoOscuro,
    onSecondary = Blanco,
    tertiary = VerdeDisponible,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    outline = TextoOscuro,
    error = RojoExceso,
    onError = Blanco
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeBoton,
    onPrimary = Blanco,
    secondary = TextoOscuro,
    onSecondary = Blanco,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    error = RojoExceso
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun RinconSurTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        dynamicColor -> LightColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
