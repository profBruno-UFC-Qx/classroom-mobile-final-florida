package com.example.florida.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// =============================================================================
// ESQUEMA CLARO (Light)
// =============================================================================

val LightColorScheme = lightColorScheme(
    // — Primária (Azul) —
    primary             = Blue40,           // botões principais, FAB, seleção ativa
    onPrimary           = Color.White,      // texto/ícone sobre primary
    primaryContainer    = Blue90,           // fundo de chips selecionados, cards de destaque
    onPrimaryContainer  = Blue10,           // texto sobre primaryContainer

    // — Secundária (Verde) —
    secondary           = Green40,          // tags "Aprovado", status positivo
    onSecondary         = Color.White,
    secondaryContainer  = Green90,          // fundo de badges de status
    onSecondaryContainer = Green10,

    // — Terciária (Âmbar) —
    tertiary            = Amber40,          // totais monetários, ícones de valor
    onTertiary          = Color.White,
    tertiaryContainer   = Amber90,          // card de resumo financeiro
    onTertiaryContainer = Amber10,

    // — Erro —
    error               = Error40,
    onError             = Color.White,
    errorContainer      = Error90,
    onErrorContainer    = Error10,

    // — Fundo e superfícies —
    background          = Neutral99,        // fundo geral das telas
    onBackground        = Neutral10,
    surface             = Color.White,      // cards, bottom sheets, dialogs
    onSurface           = Neutral10,
    surfaceVariant      = NeutralVar90,     // campos de formulário, divisores
    onSurfaceVariant    = NeutralVar30,

    // — Contorno —
    outline             = Color(0xFF72787E),
    outlineVariant      = Color(0xFFC3C6CF),

    // — Inverso (snackbars, tooltips) —
    inverseSurface      = Neutral20,
    inverseOnSurface    = Color(0xFFF0F0F4),
    inversePrimary      = Blue80,
)


// =============================================================================
// ESQUEMA ESCURO (Dark)
// =============================================================================
val DarkColorScheme = darkColorScheme(
    // — Primária —
    primary             = Blue80,
    onPrimary           = Blue20,
    primaryContainer    = Blue30,
    onPrimaryContainer  = Blue90,

    // — Secundária —
    secondary           = Green80,
    onSecondary         = Green20,
    secondaryContainer  = Green30,
    onSecondaryContainer = Green90,

    // — Terciária —
    tertiary            = Amber80,
    onTertiary          = Amber20,
    tertiaryContainer   = Amber30,
    onTertiaryContainer = Amber90,

    // — Erro —
    error               = Error80,
    onError             = Error10,
    errorContainer      = Error40,
    onErrorContainer    = Error90,

    // — Fundo e superfícies —
    background          = Color(0xFF1A1C1E),
    onBackground        = Color(0xFFE2E2E6),
    surface             = Color(0xFF1A1C1E),
    onSurface           = Color(0xFFE2E2E6),
    surfaceVariant      = Color(0xFF42474E),
    onSurfaceVariant    = NeutralVar80,

    // — Contorno —
    outline             = Color(0xFF8C9198),
    outlineVariant      = Color(0xFF42474E),

    // — Inverso —
    inverseSurface      = Color(0xFFE2E2E6),
    inverseOnSurface    = Color(0xFF2F3033),
    inversePrimary      = Blue40,
)

@Composable
fun FloridaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
