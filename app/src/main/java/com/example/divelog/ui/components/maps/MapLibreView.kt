package com.example.divelog.ui.components.maps

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    latitude: Double = 40.4168,
    longitude: Double = -3.7038,
    zoom: Double = 5.0,
    onCameraIdle: (Double, Double) -> Unit = { _, _ -> },
    interactive: Boolean = true,
) {
    val context = LocalContext.current

    val mapView = remember {
        MapLibre.getInstance(context)

        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            onCreate(null)

            getMapAsync { map ->
                map.setStyle("https://demotiles.maplibre.org/style.json")

                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(latitude, longitude))
                    .zoom(zoom)
                    .build()

                map.addOnCameraIdleListener {
                    val target = map.cameraPosition.target ?: return@addOnCameraIdleListener

                    val selectedLatitude = target.latitude
                    val selectedLongitude = target.longitude

                    onCameraIdle(selectedLatitude, selectedLongitude)
                }

                map.uiSettings.apply {
                    setScrollGesturesEnabled(interactive)
                    setZoomGesturesEnabled(interactive)
                    setRotateGesturesEnabled(interactive)
                    setTiltGesturesEnabled(interactive)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        }
    )
}