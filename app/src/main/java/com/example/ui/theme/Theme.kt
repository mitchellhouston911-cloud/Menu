package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GlowVaultLightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = TerracottaSoft,
    onPrimaryContainer = TerracottaDark,
    secondary = Sage,
    onSecondary = Color.White,
    secondaryContainer = SageSoft,
    onSecondaryContainer = SageDark,
    tertiary = Espresso,
    onTertiary = Cream,
    tertiaryContainer = Sand,
    onTertiaryContainer = Espresso,
    background = Cream,
    onBackground = Espresso,
    surface = WarmWhite,
    onSurface = Espresso,
    surfaceVariant = Sand,
    onSurfaceVariant = StoneMuted,
    surfaceContainerLowest = WarmWhite,
    surfaceContainerLow = Cream,
    surfaceContainer = Sand,
    surfaceContainerHigh = SandDeep,
    outline = StoneOutline,
    outlineVariant = SandDeep
)

private val GlowVaultDarkColorScheme = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = Espresso,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = Cream,
    secondary = SageLight,
    onSecondary = Espresso,
    secondaryContainer = DarkSageContainer,
    onSecondaryContainer = SageSoft,
    tertiary = Cream,
    onTertiary = Espresso,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = Cream,
    background = DarkBackground,
    onBackground = Cream,
    surface = DarkSurface,
    onSurface = Cream,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = StoneSubtle,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceVariant,
    surfaceContainerHigh = EspressoSoft,
    outline = DarkOutline,
    outlineVariant = EspressoSoft
)

val GlowVaultShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun GlowVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Intentional editorial apothecary palette prioritized over wallpaper dynamic tint
    val colorScheme = if (darkTheme) GlowVaultDarkColorScheme else GlowVaultLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GlowVaultTypography,
        shapes = GlowVaultShapes,
        content = content
    )
}
