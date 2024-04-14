package com.example.projekat.cats.details

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.domain.Cat
import com.example.projekat.core.compose.AppIconButton
import com.example.projekat.core.compose.NoDataContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projekat.cats.api.model.ImageModel


fun NavGraphBuilder.catsDetails(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) { navBackStackEntry ->
    val dataId = navBackStackEntry.arguments?.getString("id")
        ?: throw IllegalArgumentException("id is required.")

    val catsDetailsViewModel = viewModel<CatsDetailsViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")

            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return CatsDetailsViewModel(catId = dataId) as VM
            }
        },
    )
    val state = catsDetailsViewModel.state.collectAsState()

    CatsDetailsScreen(
        state = state.value,
        onClose = {
            navController.navigateUp()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class) //za vizuelne zahteve
@Composable
fun CatsDetailsScreen(
    state: CatsDetailsState,
    onClose: () -> Unit,
) {
    Scaffold(                           //za izgradnju ekrana app
        topBar = {
        CenterAlignedTopAppBar(
                title = {
                    state.data?.let {
                        Text(
                            text = it.name,
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Yellow,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.fetching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Error")
                    }
                } else if (state.data != null) {
                    state.imageModel?.let {
                        CatColumn(
                            data = state.data,
                            image = it
                        )
                    }
                } else {
                    NoDataContent(id = state.catId)
                    Text(
                        text = "There is no cat",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

@Composable
private fun CatColumn(
    data: Cat,
    image: ImageModel,
) {
    Column {

        val painter: Painter =
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = image.url).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
            )

        Image(
            painter = painter,
            contentDescription = "Slika",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Name: ")
                }
                append(data.name)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if(data.alternativeNames.isNotEmpty()){
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Alternative names: ")
                    }
                    append(data.alternativeNames)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Description: ")
                }
                append(data.description)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Countries of origin: ")
                }
                append(data.origin)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Temperament: ")
                }
                append(data.temperament)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Life span: ")
                }
                append(data.lifeSpan)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Weight: ")
                }
                append("Metric: ${data.weight.metric}\n \t\t\t\t\t\t\t\t Imperial: ${data.weight.imperial}")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Rare: ")
                }
                append(data.rare.toString())
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Adaptability:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.adaptability)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Child Friendly:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.childFriendly)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Dog Friendly:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.dogFriendly)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Stranger Friendly:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.strangerFriendly)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Health Issues:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.healthIssues)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                text = "Intelligence:"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Rating(rating = data.intelligence)
        }

        Spacer(modifier = Modifier.height(16.dp))
        val openWikipedia = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        }
        TextButton(
            onClick = {
                val wikipediaUrl = data.wikipediaURL
                if (wikipediaUrl.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
                    openWikipedia.launch(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.Blue)
                .padding(vertical = 8.dp),
            shape = RectangleShape,
        ) {
            Text(
                text = "Go To Wikipedia",
                color = Color.White
            )

        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun Rating(
    rating: Int,
) {
    Row {
        val text: String
        if(rating==0){
            text = "Extremely Bad"
        }
        else if(rating==1){
            text = "Very Bad"
        }
        else if(rating==2){
            text = "Bad"
        }
        else if(rating==3){
            text = "Good"
        }
        else if(rating==4){
            text = "Very Good"
        }
        else if(rating==5){
            text = "Extremely Good"
        }
        else{
            text="No Rating"
        }
        Text(text = text)
    }
}