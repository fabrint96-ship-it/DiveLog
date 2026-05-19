package com.example.divelog.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.divelog.ui.screens.adddive.AddDiveScreen
import com.example.divelog.ui.screens.divedetail.DiveDetailScreen
import com.example.divelog.ui.screens.divelist.DiveListScreen
import com.example.divelog.ui.screens.drawing.DrawingScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.divelog.viewmodel.DiveViewModel
import com.example.divelog.domain.model.Dive
import androidx.compose.ui.platform.LocalContext
import com.example.divelog.data.DiveRepository
import com.example.divelog.data.local.DiveDatabase
import com.example.divelog.viewmodel.DiveViewModelFactory
import android.net.Uri
import com.example.divelog.data.local.ImageStorageHelper
import com.example.divelog.ui.screens.photoviewer.PhotoViewerScreen
import com.example.divelog.data.local.DrawingStorageHelper
import com.example.divelog.domain.model.GalleryItemType
import com.example.divelog.data.local.FileStorageHelper
import com.example.divelog.data.local.ShareHelper
import androidx.compose.runtime.LaunchedEffect
import com.example.divelog.ui.screens.login.LoginScreen
import com.example.divelog.viewmodel.AuthViewModel

@Composable
fun DiveLogNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authUiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val database = DiveDatabase.getDatabase(context)
    val repository = DiveRepository(database.diveDao())

    val diveViewModel: DiveViewModel = viewModel(
        factory = DiveViewModelFactory(repository)
    )
    val uiState by diveViewModel.uiState.collectAsState()
    val dives = uiState.dives

    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn) {
            diveViewModel.restoreFromCloud()

            navController.navigate(Routes.DIVE_LIST) {
                popUpTo(Routes.LOGIN) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authUiState.isLoggedIn) {
            Routes.DIVE_LIST
        } else {
            Routes.LOGIN
        }
    ) {
        composable(Routes.PHOTO_VIEWER) { backStackEntry ->
            val photoUri = backStackEntry.arguments
                ?.getString("photoUri")
                ?: ""

            PhotoViewerScreen(
                photoUri = photoUri,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADD_DIVE) {
            AddDiveScreen(
                diveToEdit = null,
                onSaveDive = { id, title, location, diveType, date, depth, duration, temperature, visibility, notes, photos ->

                    val savedPhotos = ImageStorageHelper.saveImagesToInternalStorage(
                        context = context,
                        uris = photos.map { Uri.parse(it) }
                    )

                    diveViewModel.addDive(
                        Dive(
                            id = 0,
                            title = title,
                            location = location,
                            diveType = diveType,
                            date = date,
                            maxDepth = depth,
                            duration = duration,
                            waterTemperature = temperature,
                            visibility = visibility,
                            notes = notes,
                            photos = savedPhotos
                        )
                    )

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("snackbar_message", "Inmersión guardada")

                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authUiState = authUiState,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = { email, password ->
                    authViewModel.register(email, password)
                }
            )
        }

        composable(Routes.DIVE_LIST) {
            val snackbarMessage = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("snackbar_message")

            DiveListScreen(
                dives = dives,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                snackbarMessage = snackbarMessage,
                onSnackbarShown = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<String>("snackbar_message")
                },
                onAddDiveClick = {
                    navController.navigate(Routes.ADD_DIVE)
                },
                onDiveClick = { diveId ->
                    navController.navigate(Routes.diveDetail(diveId))
                },
                onLogoutClick = {
                    uiState.dives.forEach { dive ->
                        diveViewModel.deleteDive(dive)
                    }

                    authViewModel.logout()

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DIVE_LIST) {
                            inclusive = true
                        }
                    }
                },
                onBackupClick = {
                    diveViewModel.backupToCloud()
                },
                onRestoreClick = {
                    diveViewModel.restoreFromCloud()
                }
            )
        }

        composable(Routes.EDIT_DIVE) { backStackEntry ->
            val diveId = backStackEntry.arguments
                ?.getString("diveId")
                ?.toIntOrNull()

            val selectedDive = dives.find { it.id == diveId }

            AddDiveScreen(
                diveToEdit = selectedDive,
                onSaveDive = { id, title, location, diveType, date, depth, duration, temperature, visibility, notes, photos ->

                    if (selectedDive != null) {

                        val existingPhotos = selectedDive.photos

                        val newPhotos = photos.filterNot { photo ->
                            existingPhotos.contains(photo)
                        }

                        val savedNewPhotos = ImageStorageHelper.saveImagesToInternalStorage(
                            context = context,
                            uris = newPhotos.map { Uri.parse(it) }
                        )

                        val finalPhotos = existingPhotos + savedNewPhotos

                        diveViewModel.updateDive(
                            selectedDive.copy(
                                title = title,
                                location = location,
                                diveType = diveType,
                                date = date,
                                maxDepth = depth,
                                duration = duration,
                                waterTemperature = temperature,
                                visibility = visibility,
                                notes = notes,
                                photos = finalPhotos.distinct(),
                                drawings = selectedDive.drawings,
                                cloudId = selectedDive.cloudId
                            )
                        )
                    }

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("snackbar_message", "Inmersión actualizada")

                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DIVE_DETAIL) { backStackEntry ->
            val diveId = backStackEntry.arguments
                ?.getString("diveId")
                ?.toIntOrNull()

            val selectedDive = dives.find { it.id == diveId }

            DiveDetailScreen(
                dive = selectedDive,
                onBackClick = {
                    navController.popBackStack()
                },
                onDrawingClick = {
                    selectedDive?.let { dive ->
                        navController.navigate(Routes.drawing(dive.id))
                    }
                },
                onEditClick = { diveId ->
                    navController.navigate(Routes.editDive(diveId))
                },
                onPhotoClick = { photo ->
                    navController.navigate(
                        Routes.photoViewer(Uri.encode(photo))
                    )
                },
                onDeleteGalleryItem = { item ->
                    selectedDive?.let { dive ->
                        val updatedDive = when (item.type) {
                            GalleryItemType.PHOTO -> dive.copy(
                                photos = dive.photos.filterNot { it == item.uri }
                            )

                            GalleryItemType.DRAWING -> dive.copy(
                                drawings = dive.drawings.filterNot { it == item.uri }
                            )
                        }

                        diveViewModel.updateDive(updatedDive)

                        FileStorageHelper.deleteFile(item.uri)
                    }
                },
                onShareClick = { dive ->
                    ShareHelper.shareDive(
                        context = context,
                        dive = dive
                    )
                },
                onDeleteClick = { dive ->
                    diveViewModel.deleteDive(dive)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DRAWING) { backStackEntry ->
            val diveId = backStackEntry.arguments
                ?.getString("diveId")
                ?.toIntOrNull()

            val selectedDive = dives.find { it.id == diveId }

            DrawingScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveDrawing = { bitmap ->
                    val savedDrawing = DrawingStorageHelper.saveDrawingToInternalStorage(
                        context = context,
                        bitmap = bitmap
                    )

                    if (savedDrawing != null && selectedDive != null) {
                        diveViewModel.updateDive(
                            selectedDive.copy(
                                drawings = selectedDive.drawings + savedDrawing
                            )
                        )
                    }

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("snackbar_message", "Dibujo guardado")

                    navController.popBackStack()
                }
            )
        }
    }
}