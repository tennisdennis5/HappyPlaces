package com.example.happyplaces.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.happyplaces.data.model.HappyPlace
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.foundation.layout.padding


/**
 * Zeigt alle gespeicherten Orte als Marker auf einer OpenStreetMap-Karte.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesMapScreen(
    places: List<HappyPlace>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(6.0)
        }
    }

    /*
     * Aktualisiert die Marker, sobald sich die Ortsliste ändert.
     */
    LaunchedEffect(places) {
        mapView.overlays.removeAll { overlay ->
            overlay is Marker
        }

        places.forEach { place ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(
                    place.latitude,
                    place.longitude
                )

                title = place.title
                snippet = place.description

                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                setOnMarkerClickListener { clickedMarker, _ ->
                    clickedMarker.showInfoWindow()
                    true
                }
            }

            mapView.overlays.add(marker)
        }

        /*
         * Bei vorhandenen Orten wird der erste Ort als
         * Startzentrum der Karte verwendet.
         */
        places.firstOrNull()?.let { firstPlace ->
            mapView.controller.setCenter(
                GeoPoint(
                    firstPlace.latitude,
                    firstPlace.longitude
                )
            )
        }

        mapView.invalidate()
    }

    DisposableEffect(mapView) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Gespeicherte Orte")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->

        AndroidView(
            factory = {
                mapView
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }

}