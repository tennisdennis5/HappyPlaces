package com.example.happyplaces.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/**
 * Interaktive OpenStreetMap-Karte zur Standortauswahl.
 *
 * Durch Tippen auf die Karte wird ein Marker gesetzt.
 * Über den Button kann alternativ der aktuelle Standort übernommen werden.
 */
@Composable
fun MapPicker(
    latitude: Double,
    longitude: Double,
    onLocationSelected: (
        latitude: Double,
        longitude: Double
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    val marker = remember(mapView) {
        Marker(mapView).apply {
            title = "Ausgewählter Standort"
            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
        }
    }

    /**
     * Aktualisiert Marker und Kartenzentrum.
     */
    fun updateMarker(
        selectedLatitude: Double,
        selectedLongitude: Double,
        animate: Boolean
    ) {
        val point = GeoPoint(
            selectedLatitude,
            selectedLongitude
        )

        marker.position = point

        if (!mapView.overlays.contains(marker)) {
            mapView.overlays.add(marker)
        }

        if (animate) {
            mapView.controller.animateTo(point)
        } else {
            mapView.controller.setCenter(point)
        }

        mapView.invalidate()
    }

    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun loadCurrentLocation() {
        val cancellationTokenSource =
            CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(
                    context,
                    "Der aktuelle Standort konnte nicht ermittelt werden.",
                    Toast.LENGTH_LONG
                ).show()

                return@addOnSuccessListener
            }

            onLocationSelected(
                location.latitude,
                location.longitude
            )

            updateMarker(
                selectedLatitude = location.latitude,
                selectedLongitude = location.longitude,
                animate = true
            )

            mapView.controller.setZoom(17.0)
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Fehler beim Ermitteln des Standorts.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val coarseGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true

            val fineGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            if (coarseGranted || fineGranted) {
                loadCurrentLocation()
            } else {
                Toast.makeText(
                    context,
                    "Die Standortberechtigung wurde nicht erteilt.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    fun requestCurrentLocation() {
        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (coarseGranted || fineGranted) {
            loadCurrentLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    DisposableEffect(mapView) {
        val mapEventsOverlay = MapEventsOverlay(
            object : MapEventsReceiver {

                override fun singleTapConfirmedHelper(
                    point: GeoPoint
                ): Boolean {
                    onLocationSelected(
                        point.latitude,
                        point.longitude
                    )

                    updateMarker(
                        selectedLatitude = point.latitude,
                        selectedLongitude = point.longitude,
                        animate = false
                    )

                    return true
                }

                override fun longPressHelper(
                    point: GeoPoint
                ): Boolean {
                    return false
                }
            }
        )

        mapView.overlays.add(0, mapEventsOverlay)
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    /*
     * Reagiert auf Änderungen, beispielsweise wenn der aktuelle
     * Standort über den Button ausgewählt wurde.
     */
    androidx.compose.runtime.LaunchedEffect(
        latitude,
        longitude
    ) {
        updateMarker(
            selectedLatitude = latitude,
            selectedLongitude = longitude,
            animate = false
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Tippe auf die Karte, um den Standort auszuwählen."
        )

        AndroidView(
            factory = {
                mapView
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Text(
            text =
                "Breitengrad: %.5f\nLängengrad: %.5f"
                    .format(latitude, longitude)
        )

        Button(
            onClick = {
                requestCurrentLocation()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aktuellen Standort verwenden")
        }
    }
}