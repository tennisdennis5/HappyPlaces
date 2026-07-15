package com.example.happyplaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.happyplaces.data.database.HappyPlacesDatabase
import com.example.happyplaces.data.model.HappyPlace
import com.example.happyplaces.repository.HappyPlacesRepository
import com.example.happyplaces.ui.screens.AddPlaceScreen
import com.example.happyplaces.ui.screens.PlacesMapScreen
import com.example.happyplaces.ui.theme.HappyPlacesTheme
import com.example.happyplaces.viewmodel.HappyPlacesViewModel
import com.example.happyplaces.viewmodel.HappyPlacesViewModelFactory
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()

        setContent {
            HappyPlacesTheme {
                HappyPlacesRoot()
            }
        }
    }
}

/**
 * Erstellt Datenbank, Repository und ViewModel.
 */
@Composable
private fun HappyPlacesRoot() {
    val context = LocalContext.current

    val database = remember {
        HappyPlacesDatabase.getDatabase(context)
    }

    val repository = remember(database) {
        HappyPlacesRepository(
            database.happyPlaceDao()
        )
    }

    val factory = remember(repository) {
        HappyPlacesViewModelFactory(repository)
    }

    val viewModel: HappyPlacesViewModel =
        viewModel(factory = factory)

    HappyPlacesScreen(viewModel = viewModel)
}

/**
 * Hauptansicht der Happy-Places-App.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HappyPlacesScreen(
    viewModel: HappyPlacesViewModel
) {
    val places by viewModel.places.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var showAddScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var showMapScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedPlaceForNote by remember {
        mutableStateOf<HappyPlace?>(null)
    }

    if (showMapScreen) {
        PlacesMapScreen(
            places = places,
            onBack = {
                showMapScreen = false
            }
        )

        return
    }

    if (showAddScreen) {
        AddPlaceScreen(
            onSave = {
                    title,
                    description,
                    imageUri,
                    latitude,
                    longitude ->

                viewModel.addPlace(
                    title = title,
                    description = description,
                    imageUri = imageUri,
                    latitude = latitude,
                    longitude = longitude
                )

                showAddScreen = false
            },
            onCancel = {
                showAddScreen = false
            }
        )

        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Happy Places")
                },
                actions = {
                    TextButton(
                        onClick = {
                            showMapScreen = true
                        }
                    ) {
                        Text("Karte")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddScreen = true
                }
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        if (places.isEmpty()) {
            EmptyPlacesContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            PlacesList(
                places = places,
                onDelete = viewModel::deletePlace,
                onNoteClick = { place ->
                    selectedPlaceForNote = place
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }

    selectedPlaceForNote?.let { place ->
        PersonalNoteDialog(
            place = place,
            onSave = { noteText ->
                viewModel.savePersonalNote(
                    place = place,
                    noteText = noteText
                )

                selectedPlaceForNote = null
            },
            onDismiss = {
                selectedPlaceForNote = null
            }
        )
    }
}

/**
 * Hinweis, wenn noch keine Orte gespeichert sind.
 */
@Composable
private fun EmptyPlacesContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Noch keine Orte gespeichert.",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Tippe auf +, um deinen ersten Lieblingsort hinzuzufügen."
        )
    }
}

/**
 * Zeigt alle gespeicherten Orte in einer scrollbaren Liste.
 */
@Composable
private fun PlacesList(
    places: List<HappyPlace>,
    onDelete: (HappyPlace) -> Unit,
    onNoteClick: (HappyPlace) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = places,
            key = { place -> place.id }
        ) { place ->
            PlaceCard(
                place = place,
                onDelete = {
                    onDelete(place)
                },
                onNoteClick = {
                    onNoteClick(place)
                }
            )
        }
    }
}

@Composable
private fun PlaceCard(
    place: HappyPlace,
    onDelete: () -> Unit,
    onNoteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            place.imageUri?.let { imageUri ->
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto von ${place.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = place.title,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Standort: %.5f, %.5f".format(
                        place.latitude,
                        place.longitude
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                if (place.personalNote.isNotBlank()) {
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Persönliche Notiz",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = place.personalNote,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onNoteClick
                    ) {
                        Text(
                            if (place.personalNote.isBlank()) {
                                "Notiz hinzufügen"
                            } else {
                                "Notiz bearbeiten"
                            }
                        )
                    }

                    TextButton(
                        onClick = onDelete
                    ) {
                        Text("Ort löschen")
                    }
                }
            }
        }
    }
}

/**
 * Dialog zum Hinzufügen, Bearbeiten oder Löschen
 * der persönlichen Notiz eines Ortes.
 *
 * Zum Löschen wird das Textfeld geleert und gespeichert.
 */
@Composable
private fun PersonalNoteDialog(
    place: HappyPlace,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var noteText by rememberSaveable(place.id) {
        mutableStateOf(place.personalNote)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Persönliche Notiz")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = place.title,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Notiz")
                    },
                    minLines = 3
                )

                if (place.personalNote.isNotBlank()) {
                    Text(
                        text = "Leere das Feld und speichere, um die Notiz zu löschen.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(noteText)
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Abbrechen")
            }
        }
    )
}