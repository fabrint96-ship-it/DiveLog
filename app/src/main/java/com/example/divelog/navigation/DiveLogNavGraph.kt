package com.example.divelog.navigation

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.divelog.data.DiveRepository
import com.example.divelog.data.local.DiveDatabase
import com.example.divelog.data.local.DrawingStorageHelper
import com.example.divelog.data.local.FileStorageHelper
import com.example.divelog.data.local.ImageStorageHelper
import com.example.divelog.data.local.ShareHelper
import com.example.divelog.domain.model.Dive
import com.example.divelog.domain.model.GalleryItemType
import com.example.divelog.ui.screens.adddive.AddDiveScreen
import com.example.divelog.ui.screens.divedetail.DiveDetailScreen
import com.example.divelog.ui.screens.divelist.DiveListScreen
import com.example.divelog.ui.screens.drawing.DrawingScreen
import com.example.divelog.ui.screens.login.LoginScreen
import com.example.divelog.ui.screens.photoviewer.PhotoViewerScreen
import com.example.divelog.viewmodel.AuthViewModel
import com.example.divelog.viewmodel.DiveViewModel
import com.example.divelog.viewmodel.DiveViewModelFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.data.DiveMediaNoteRepository
import com.example.divelog.viewmodel.DiveMediaNoteViewModel
import com.example.divelog.viewmodel.DiveMediaNoteViewModelFactory
import com.example.divelog.ui.screens.locationpicker.LocationPickerScreen
import com.example.divelog.data.location.ReverseGeocoder
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import com.example.divelog.data.location.MapIntentHelper

@Composable
fun DiveLogNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val database = DiveDatabase.getDatabase(context)
    val repository = DiveRepository(database.diveDao())

    val mediaNoteRepository = DiveMediaNoteRepository(
        database.diveMediaNoteDao()
    )

    val mediaNoteViewModel: DiveMediaNoteViewModel = viewModel(
        factory = DiveMediaNoteViewModelFactory(mediaNoteRepository)
    )

    val authViewModel: AuthViewModel = viewModel()

    val diveViewModel: DiveViewModel = viewModel(
        factory = DiveViewModelFactory(repository)
    )

    val authUiState by authViewModel.uiState.collectAsState()
    val uiState by diveViewModel.uiState.collectAsState()
    val dives = uiState.dives

    LaunchedEffect(Unit) {
        diveViewModel.syncEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(authUiState.isLoading, authUiState.isLoggedIn) {
        if (!authUiState.isLoading) {
            if (authUiState.isLoggedIn) {
                diveViewModel.restoreAfterLoginIfNeeded()

                navController.navigate(Routes.DIVE_LIST) {
                    popUpTo(Routes.AUTH_LOADING) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.AUTH_LOADING) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH_LOADING
    ) {
        composable(Routes.AUTH_LOADING) {
            AuthLoadingScreen()
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
                isSyncing = uiState.isSyncing,
                snackbarHostState = snackbarHostState,
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
                    dives.forEach { dive ->
                        diveViewModel.deleteDive(dive)
                    }

                    diveViewModel.resetAutoRestoreFlag()
                    authViewModel.logout()

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DIVE_LIST) {
                            inclusive = true
                        }
                        launchSingleTop = true
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

        composable(Routes.ADD_DIVE) {
            val backStackEntry = navController.currentBackStackEntry

            val pickedLocation by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<String?>("picked_location", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            val pickedLatitude by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<Double?>("picked_lat", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            val pickedLongitude by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<Double?>("picked_lng", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            AddDiveScreen(
                diveToEdit = null,
                pickedLocation = pickedLocation,
                pickedLatitude = pickedLatitude,
                pickedLongitude = pickedLongitude,
                onSaveDive = { _, title, location, diveType, date, depth, duration, temperature, visibility, notes, photos, latitude, longitude ->
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
                            photos = savedPhotos,
                            latitude = latitude,
                            longitude = longitude
                        )
                    )

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("snackbar_message", "Inmersión guardada")

                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onPickLocationClick = {
                    navController.navigate(Routes.LOCATION_PICKER)
                }
            )
        }

        composable(Routes.EDIT_DIVE) { backStackEntry ->
            val diveId = backStackEntry.arguments
                ?.getString("diveId")
                ?.toIntOrNull()

            val selectedDive = dives.find { it.id == diveId }

            val backStackEntry = navController.currentBackStackEntry

            val pickedLocation by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<String?>("picked_location", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            val pickedLatitude by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<Double?>("picked_lat", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            val pickedLongitude by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow<Double?>("picked_lng", null)
                ?.collectAsState()
                ?: remember { mutableStateOf(null) }

            AddDiveScreen(
                diveToEdit = selectedDive,
                pickedLocation = pickedLocation,
                pickedLatitude = pickedLatitude,
                pickedLongitude = pickedLongitude,
                onSaveDive = { _, title, location, diveType, date, depth, duration, temperature, visibility, notes, photos, latitude, longitude ->
                    selectedDive?.let { dive ->
                        val existingPhotos = dive.photos

                        val newPhotos = photos.filterNot { photo ->
                            existingPhotos.contains(photo)
                        }

                        val savedNewPhotos = ImageStorageHelper.saveImagesToInternalStorage(
                            context = context,
                            uris = newPhotos.map { Uri.parse(it) }
                        )

                        val finalPhotos = existingPhotos + savedNewPhotos

                        diveViewModel.updateDive(
                            dive.copy(
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
                                drawings = dive.drawings,
                                cloudId = dive.cloudId,
                                syncId = dive.syncId,
                                latitude = latitude,
                                longitude = longitude
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
                },
                onPickLocationClick = {
                    navController.navigate(Routes.LOCATION_PICKER)
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
                onEditClick = { editDiveId ->
                    navController.navigate(Routes.editDive(editDiveId))
                },
                onPhotoClick = { photo ->
                    selectedDive?.let { currentDive ->
                        navController.navigate(
                            Routes.photoViewer(
                                currentDive.id,
                                Uri.encode(photo)
                            )
                        )
                    }
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

                        if (!item.uri.startsWith("http")) {
                            FileStorageHelper.deleteFile(item.uri)
                        }
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
                },
                onOpenMapClick = { lat, lng, label ->
                    MapIntentHelper.openLocation(
                        context = context,
                        latitude = lat,
                        longitude = lng,
                        label = label
                    )
                },
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

        composable(Routes.PHOTO_VIEWER) { backStackEntry ->
            val diveId = backStackEntry.arguments
                ?.getString("diveId")
                ?.toIntOrNull()
                ?: 0

            val photoUri = backStackEntry.arguments
                ?.getString("photoUri")
                ?: ""

            val decodedPhotoUri = Uri.decode(photoUri)

            val mediaNote by mediaNoteViewModel.note.collectAsState()

            LaunchedEffect(decodedPhotoUri) {
                mediaNoteViewModel.loadNote(decodedPhotoUri)
            }

            PhotoViewerScreen(
                photoUri = decodedPhotoUri,
                initialNote = mediaNote?.note ?: "",
                onSavePostcardNote = { noteText ->
                    mediaNoteViewModel.saveNote(
                        diveId = diveId,
                        mediaUri = decodedPhotoUri,
                        mediaType = "PHOTO",
                        noteText = noteText
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LOCATION_PICKER) {
            LocationPickerScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLocationSelected = { lat, lng ->

                    val previousEntry = navController.previousBackStackEntry

                    previousEntry?.savedStateHandle?.set("picked_lat", lat)
                    previousEntry?.savedStateHandle?.set("picked_lng", lng)
                    previousEntry?.savedStateHandle?.set(
                        "picked_location",
                        "Lat %.5f, Lng %.5f".format(lat, lng)
                    )

                    navController.popBackStack()

                    scope.launch {
                        val locationName = ReverseGeocoder.getLocationName(
                            context = context,
                            latitude = lat,
                            longitude = lng
                        )

                        if (locationName.isNotBlank()) {
                            previousEntry?.savedStateHandle?.set("picked_location", locationName)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AuthLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DiveDeepBlue, DiveOceanBlue, DiveTeal)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )

            Text(
                text = "DiveLog",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Tu bitácora submarina",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}