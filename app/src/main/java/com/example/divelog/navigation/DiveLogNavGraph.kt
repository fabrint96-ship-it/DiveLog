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
import com.example.divelog.data.model.Dive
import androidx.compose.ui.platform.LocalContext
import com.example.divelog.data.DiveRepository
import com.example.divelog.data.local.DiveDatabase
import com.example.divelog.viewmodel.DiveViewModelFactory
import android.net.Uri
import com.example.divelog.data.local.ImageStorageHelper
import androidx.compose.ui.platform.LocalContext

@Composable
fun DiveLogNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = DiveDatabase.getDatabase(context)
    val repository = DiveRepository(database.diveDao())

    val diveViewModel: DiveViewModel = viewModel(
        factory = DiveViewModelFactory(repository)
    )
    val dives by diveViewModel.dives.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.DIVE_LIST
    ) {
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

                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DIVE_LIST) {
            DiveListScreen(
                dives = dives,
                onAddDiveClick = {
                    navController.navigate(Routes.ADD_DIVE)
                },
                onDiveClick = { diveId ->
                    navController.navigate(Routes.diveDetail(diveId))
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
                                notes = notes
                            )
                        )
                    }

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
                    navController.navigate(Routes.DRAWING)
                },
                onEditClick = { diveId ->
                    navController.navigate(Routes.editDive(diveId))
                },
                onDeleteClick = { dive ->
                    diveViewModel.deleteDive(dive)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DRAWING) {
            DrawingScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}