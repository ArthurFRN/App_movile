package com.example.ratcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    secondary = AzulGrisaceo,
    tertiary = AmarilloClaro
)

// Colores claros según el mockup: fondo gris muy suave, tarjetas blancas y azul como color principal
private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    secondary = TextoSecundario,
    tertiary = Amarillo,
    background = FondoPantalla,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFEEF1F6),
    onSurfaceVariant = TextoSecundario,
    surfaceContainerHigh = Color.White,     // ventanas emergentes
    secondaryContainer = AzulSuave,         // fondo de las barras de progreso
    onSecondaryContainer = AzulPrimario,
    outline = Color(0xFFD5DCE6),
    outlineVariant = BordeSuave,
    error = Rojo
)

@Composable
fun RatcherTheme(
    // El mockup solo tiene tema claro, pero cambiando él false a true se puede adaptar al modo en el cual se encuentra el dispositivo
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
