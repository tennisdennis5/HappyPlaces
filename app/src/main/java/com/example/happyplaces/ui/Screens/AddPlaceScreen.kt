package com.example.happyplaces.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

/**
 * Eingabeseite zum Erstellen eines neuen Lieblingsortes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    onSave: (
        title: String,
        description: String,
        imageUri: String?,
        latitude: Double,
        longitude: Double
    ) -> Unit,
    onCancel: () -> Unit
) {
    var title by rememberSaveable {
        mutableStateOf("")
    }

    var description by rememberSaveable {
        mutableStateOf("")
    }

    // Berlin ist lediglich die Startposition der Karte.
    var latitude by rememberSaveable {
        mutableDoubleStateOf(52.5200)
    }

    var longitude by rememberSaveable {
        mutableDoubleStateOf(13.4050)
    }

    val inputValid =
        title.isNotBlank() &&
                description.isNotBlank()

    var selectedImageUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            selectedImageUri = uri?.toString()
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Neuen Ort hinzufügen")
                },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Name des Ortes")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Beschreibung")
                },
                minLines = 3
            )

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedImageUri == null) {
                        "Foto auswählen"
                    } else {
                        "Anderes Foto auswählen"
                    }
                )
            }

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Ausgewähltes Foto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            MapPicker(
                latitude = latitude,
                longitude = longitude,
                onLocationSelected = {
                        selectedLatitude,
                        selectedLongitude ->

                    latitude = selectedLatitude
                    longitude = selectedLongitude
                }
            )

            Button(
                onClick = {
                    onSave(
                        title.trim(),
                        description.trim(),
                        selectedImageUri,
                        latitude,
                        longitude
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputValid
            ) {
                Text("Ort speichern")
            }

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abbrechen")
            }
        }
    }
}