/*
 * CleanSweep
 * Copyright (c) 2025 LoopOtto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.cleansweep.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
    DARKER,
    AMOLED
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color(0xFFF8FAFC),
    primaryContainer = Color(0xFFDCEBFF),
    onPrimaryContainer = Color(0xFF0B2559),
    secondary = Color(0xFF475569),
    onSecondary = Color(0xFFF8FAFC),
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFF0F766E),
    onTertiary = Color(0xFFF8FAFC),
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF042F2E),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFF7F7),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF450A0A),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFBFCFE),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    surfaceContainerLowest = Color(0xFFFEFFFE),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFEFF4F8),
    surfaceContainerHighest = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFFCBD5E1),
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF5EEAD4),
    onTertiary = Color(0xFF042F2E),
    tertiaryContainer = Color(0xFF134E4A),
    onTertiaryContainer = Color(0xFFCCFBF1),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0F141C),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    surfaceContainerLowest = Color(0xFF0B111A),
    surfaceContainerLow = Color(0xFF111827),
    surfaceContainer = Color(0xFF172033),
    surfaceContainerHigh = Color(0xFF1F2937),
    surfaceContainerHighest = Color(0xFF293445)
)

private val DarkerColorScheme = darkColorScheme(
    primary = Color(0xFF9FCAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF2D3948),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD7BDE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0A0B10),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF0A0B10),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF191A1D),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199)
)

private val AmoledColorScheme = darkColorScheme(
    primary = Color(0xFF9FCAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    tertiary = Color(0xFFD7BDE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color.Black,
    onBackground = Color(0xFFE2E2E6),
    surface = Color.Black,
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199),
    secondaryContainer = Color(0xFF1A1A1A),

    // Explicitly set surface containers to pure black for the AMOLED theme.
    // This ensures popups like DropdownMenu have a black background.
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color.Black,
    surfaceContainer = Color.Black,
    surfaceContainerHigh = Color.Black,
    surfaceContainerHighest = Color.Black
)

val LocalAppTheme = staticCompositionLocalOf { AppTheme.SYSTEM }

val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(12.dp)
)

@Composable
fun CleanSweepTheme(
    theme: AppTheme = AppTheme.SYSTEM,
    useDynamicColors: Boolean = true,
    accentColorKey: String = "DEFAULT_BLUE",
    content: @Composable () -> Unit
) {
    val darkTheme = theme.isDark
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current

    val colorScheme = when {
        // Handle special themes (AMOLED, DARKER) first.
        // If dynamic colors are on, a hybrid theme gets created.
        // combined with dynamic accent colors.
        (theme == AppTheme.AMOLED || theme == AppTheme.DARKER) -> {
            val baseColorScheme = if (theme == AppTheme.AMOLED) AmoledColorScheme else DarkerColorScheme
            if (useDynamicColors && supportsDynamic) {
                val dynamic = dynamicDarkColorScheme(context)
                baseColorScheme.copy(
                    primary = dynamic.primary,
                    onPrimary = dynamic.onPrimary,
                    primaryContainer = dynamic.primaryContainer,
                    onPrimaryContainer = dynamic.onPrimaryContainer,
                    secondary = dynamic.secondary,
                    onSecondary = dynamic.onSecondary,
                    secondaryContainer = dynamic.secondaryContainer,
                    onSecondaryContainer = dynamic.onSecondaryContainer,
                    tertiary = dynamic.tertiary,
                    onTertiary = dynamic.onTertiary,
                    tertiaryContainer = dynamic.tertiaryContainer,
                    onTertiaryContainer = dynamic.onTertiaryContainer
                )
            } else {
                val accentColor = predefinedAccentColors.find { it.key == accentColorKey }
                    ?: predefinedAccentColors.first()
                baseColorScheme.copy(primary = accentColor.darkColor)
            }
        }

        // For standard themes, fully replace with dynamic colors if enabled.
        useDynamicColors && supportsDynamic -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // For standard themes when dynamic colors are off, use predefined accents.
        else -> {
            val baseColorScheme = when (theme) {
                AppTheme.LIGHT -> LightColorScheme
                AppTheme.DARK -> DarkColorScheme
                // For SYSTEM theme when dynamic colors are off
                else -> if (darkTheme) DarkColorScheme else LightColorScheme
            }
            val accentColor = predefinedAccentColors.find { it.key == accentColorKey }
                ?: predefinedAccentColors.first()
            baseColorScheme.copy(
                primary = if (darkTheme) accentColor.darkColor else accentColor.lightColor
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = ExpressiveShapes,
            content = content
        )
    }
}

val AppTheme.isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = when (this) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK, AppTheme.DARKER, AppTheme.AMOLED -> true
    }
