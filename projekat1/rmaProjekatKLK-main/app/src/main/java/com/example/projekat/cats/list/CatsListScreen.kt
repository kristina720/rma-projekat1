package com.example.projekat.cats.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.domain.Cat

@ExperimentalMaterial3Api
fun NavGraphBuilder.catsListScreen(
    route: String,
    navController: NavController,
) = composable(route = route) {
    val catListViewModel = viewModel<CatsListViewModel>()
    val state by catListViewModel.state.collectAsState()

    CatListScreen(
        state = state,
        onItemClick = {
            navController.navigate(route = "cats/${it.id}")
        },
        onSearch = { searchText ->
            catListViewModel.searchCatsByName(searchText)
        },
        resetSearch = {
            catListViewModel.clearSearch()
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun CatListScreen(
    state: CatsListState?,
    onItemClick: (Cat) -> Unit,
    onSearch: (String) -> Unit,
    resetSearch: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    //val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(
                        text = "Cats",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ) },
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Yellow,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer, //ne radi nista
                    ),
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Yellow)
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = {
                                Text(
                                    text = "Search",
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            modifier = Modifier.weight(20f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    //focusManager.clearFocus()
                                    if (searchText.isNotBlank()) {
                                        onSearch(searchText)
                                    } else {
                                        searchText = ""
                                        resetSearch()
                                    }
                                }
                            ),
                        )
                        IconButton(onClick = { //kad kliknem x
                            searchText = ""
                            resetSearch()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.DarkGray,
                            )
                        }
                    }
                }
            }
        },
        content = {
            state?.let { currentState ->
                CatList(
                    paddingValues = it,
                    items = currentState.cats,
                    onItemClick = onItemClick,
                    fetching = currentState.fetching,
                    error = currentState.error,
                )
            }
        }
    )
}

@Composable
private fun CatList(
    items: List<Cat>,
    paddingValues: PaddingValues,
    onItemClick: (Cat) -> Unit,
    fetching: Boolean,
    error: CatsListState.ListError?,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        items.forEach {
            Column {
                key(it.id) {
                    OneItem(
                        data = it,
                        onClick = {
                            onItemClick(it)
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (items.isEmpty()) {
            if (fetching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    val errorMessage = "Error " + error
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No cats for your search",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun OneItem(
    data: Cat,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
            Column(
                modifier = Modifier.padding(1.dp)
        ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(textDecoration = TextDecoration.Underline),
                            block = {
                                append(data.name)
                            }
                        )
                        if (data.alternativeNames.isNotEmpty()) {
                            append(" (${data.alternativeNames})")
                        }
                    },
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.description.take(150).plus("..."),
                style = TextStyle(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val temperamentList = twoTemp(data).joinToString(", ")

            Text(
                text = temperamentList,
                style = TextStyle(fontStyle = FontStyle.Italic)
            )
        }
    }
    }
}

private fun twoTemp(data: Cat): List<String> {
    val temperamentList = data.temperament.split(",")
    return temperamentList.shuffled().take(2)
}
