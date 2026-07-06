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

package com.cleansweep.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.cleansweep.R
import com.cleansweep.ui.navigation.AppNavigation
import com.cleansweep.ui.navigation.Screen
import com.cleansweep.util.PermissionManager

/**
 * This composable contains the entire UI of the main application, AFTER all security
 * and license checks have passed. It is shared by both flavors.
 */
@Composable
fun MainApp(
    viewModel: MainViewModel,
    windowSizeClass: WindowSizeClass
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    var hasAllFilesAccess by remember { mutableStateOf(PermissionManager.hasAllFilesAccess()) }
    var justGrantedAccess by remember { mutableStateOf(false) }

    // This observer will re-check the permission when the app is resumed.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val oldState = hasAllFilesAccess
                val newState = PermissionManager.hasAllFilesAccess()
                hasAllFilesAccess = newState
                if (!oldState && newState) {
                    justGrantedAccess = true // Set a one-time flag that we just got permission
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (isOnboardingCompleted) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        true -> {
            if (hasAllFilesAccess) {
                val startDestination = if (justGrantedAccess) {
                    Screen.SessionSetup.createRoute(forceRefresh = true)
                } else {
                    Screen.SessionSetup.createRoute(forceRefresh = false)
                }

                AppNavigation(
                    navController = rememberNavController(),
                    windowSizeClass = windowSizeClass,
                    startDestination = startDestination
                )

                // After the composition that uses the flag, reset it.
                LaunchedEffect(justGrantedAccess) {
                    if (justGrantedAccess) {
                        justGrantedAccess = false
                    }
                }
            } else {
                PermissionRequiredScreen()
            }
        }
        false -> {
            val navController = rememberNavController()
            AppNavigation(
                navController = navController,
                windowSizeClass = windowSizeClass,
                startDestination = Screen.Onboarding.route
            )
        }
    }
}

@Composable
fun PermissionRequiredScreen() {
    var showCloseDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                modifier = Modifier
                    .padding(18.dp)
                    .size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.permission_screen_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.permission_required_step),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.permission_screen_desc),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.permission_privacy_note),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                try {
                    val intent = PermissionManager.createAllFilesAccessIntent(context)
                    if (intent != null) {
                        context.startActivity(intent)
                    } else {
                        showCloseDialog = true
                    }
                } catch (e: Exception) {
                    showCloseDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.open_settings))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { showCloseDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.close_app))
        }
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            title = { Text(stringResource(R.string.onboarding_close_dialog_title)) },
            text = {
                Text(stringResource(R.string.onboarding_close_dialog_body))
            },
            confirmButton = {
                Button(
                    onClick = {
                        (context as? androidx.activity.ComponentActivity)?.finish()
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showCloseDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
