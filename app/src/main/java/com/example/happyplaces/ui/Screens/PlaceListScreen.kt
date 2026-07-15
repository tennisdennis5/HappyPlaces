package com.example.happyplaces.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.happyplaces.data.model.HappyPlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(
    places: List<HappyPlace>,
    onAddClick: () -> Unit
) {

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Happy Places")
                }
            )

        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onAddClick
            ) {
                Text("+")
            }

        }

    ) { padding ->

        if (places.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                Text(
                    "Noch keine Orte vorhanden.",
                    modifier = Modifier.padding(24.dp)
                )

            }

        } else {

            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {

                items(places) { place ->

                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = place.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                Modifier.height(4.dp)
                            )

                            Text(
                                place.description
                            )

                        }

                    }

                }

            }

        }

    }

}
