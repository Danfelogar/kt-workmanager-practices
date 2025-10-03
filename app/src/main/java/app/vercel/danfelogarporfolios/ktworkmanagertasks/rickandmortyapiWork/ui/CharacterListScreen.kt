package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.vercel.danfelogarporfolios.ktworkmanagertasks.R
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.Character
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import coil3.request.error


@Composable
fun CharacterListScreen(
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is CharactersUiState.Loading -> {
            LoadingScreen(modifier = modifier.fillMaxSize())
        }

        is CharactersUiState.Success -> {
            val characters = (uiState as CharactersUiState.Success).values
            CharacterList(
                characters = characters,
            )
        }

        is CharactersUiState.Error -> {
            ErrorScreen(
                retryAction = viewModel::getResources,
                modifier = modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.loading_img),
        contentDescription = stringResource(id = R.string.loading),
        modifier = modifier.size(200.dp)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(
            onClick = retryAction
        ) {
            Text(
                text = stringResource(R.string.retry),
            )
        }
    }
}

@Composable
fun CharacterList(characters: List<Character>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(characters) { character ->
            CharacterCard(character)
        }
    }
}

@Composable
fun CharacterCard(character: Character) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.ic_connection_error),
                onSuccess = { // Opcional: para debugging
                    println("Image loaded successfully: ${character.image}")
                },
                onError = { // Opcional: para debugging
                    println("Failed to load image: ${character.image}")
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Species: ${character.species}",
                    style = typography.bodyMedium
                )
                Text(
                    text = "Gender: ${character.gender}",
                    style = typography.bodySmall
                )
            }
        }
    }
}
