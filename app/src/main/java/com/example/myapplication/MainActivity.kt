package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoGallery()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGallery() {
    var pictures by remember { mutableStateOf(generateSamplePictures()) }
    var searchText by remember { mutableStateOf("") }
    var isGrid by remember { mutableStateOf(false) }

    val filteredPictures = pictures.filter { picture ->
        picture.author.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Галерея") }
            )
        },
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка очистки
                FloatingActionButton(
                    onClick = { pictures = emptyList() }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Очистить всё")
                }
                // Кнопка добавления
                FloatingActionButton(
                    onClick = {
                        val newPicture = generateNewPicture(pictures)
                        pictures = pictures + newPicture
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Поле поиска
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Поиск по автору") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") }
            )

            // Переключатель режима отображения
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Режим отображения:")
                Switch(
                    checked = isGrid,
                    onCheckedChange = { isGrid = it }
                )
                Text(if (isGrid) "Сетка" else "Список")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredPictures.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет изображений")
                }
            } else {
                if (isGrid) {
                    // Отображение в виде сетки
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredPictures) { picture ->
                            GridPictureItem(
                                picture = picture,
                                onRemove = { pictures = pictures - picture }
                            )
                        }
                    }
                } else {
                    // Отображение в виде списка
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredPictures) { picture ->
                            ListPictureItem(
                                picture = picture,
                                onRemove = { pictures = pictures - picture }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListPictureItem(picture: Picture, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            AsyncImage(
                model = picture.url,
                contentDescription = "Изображение ${picture.author}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = picture.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Button(onClick = onRemove) {
                    Text("Удалить")
                }
            }
        }
    }
}

@Composable
fun GridPictureItem(picture: Picture, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column {
            AsyncImage(
                model = picture.url,
                contentDescription = "Изображение ${picture.author}",
                modifier = Modifier
                    .size(150.dp)
            )
            Text(
                text = picture.author,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp)
            )
            Button(
                onClick = onRemove,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Удалить")
            }
        }
    }
}

// Генерация тестовых данных с реальными картинками
fun generateSamplePictures(): List<Picture> {
    return listOf(
        Picture(
            id = 1,
            author = "Художник 1",
            url = "https://i.pinimg.com/736x/4e/53/a9/4e53a93987fbf7c50ef00fd1ced6334e.jpg"
        ),
        Picture(
            id = 2,
            author = "Художник 2",
            url = "https://i.pinimg.com/736x/e7/05/09/e70509b37a0241242798b8a2db663e41.jpg"
        ),
        Picture(
            id = 3,
            author = "Художник 3",
            url = "https://i.pinimg.com/736x/cc/c1/5d/ccc15dc2c841a7d3eeb092cd2606c90c.jpg"
        ),
        Picture(
            id = 4,
            author = "Художник 4",
            url = "https://i.pinimg.com/736x/33/76/2b/33762b2f1eda889046c0c39d47a88046.jpg"
        ),
        Picture(
            id = 5,
            author = "Художник 5",
            url = "https://i.pinimg.com/736x/d7/52/b7/d752b7b12c8b7999486c768a2b319011.jpg"
        )
    )
}

// Генерация новой картинки с проверкой на существование
fun generateNewPicture(existingPictures: List<Picture>): Picture {
    val allUrls = listOf(
        "https://i.pinimg.com/736x/4e/53/a9/4e53a93987fbf7c50ef00fd1ced6334e.jpg",
        "https://i.pinimg.com/736x/e7/05/09/e70509b37a0241242798b8a2db663e41.jpg",
        "https://i.pinimg.com/736x/cc/c1/5d/ccc15dc2c841a7d3eeb092cd2606c90c.jpg",
        "https://i.pinimg.com/736x/33/76/2b/33762b2f1eda889046c0c39d47a88046.jpg",
        "https://i.pinimg.com/736x/d7/52/b7/d752b7b12c8b7999486c768a2b319011.jpg"
    )

    // Находим URL, который еще не используется
    val unusedUrls = allUrls.filter { url ->
        existingPictures.none { it.url == url }
    }

    val newId = (existingPictures.maxByOrNull { it.id }?.id ?: 0) + 1

    return if (unusedUrls.isNotEmpty()) {
        // Используем неиспользованный URL из нашего списка
        Picture(
            id = newId,
            author = "Художник ${newId}",
            url = unusedUrls.first()
        )
    } else {
        // Если все URL использованы, создаем случайную картинку
        Picture(
            id = newId,
            author = "Случайный художник ${newId}",
            url = "https://picsum.photos/400/400?random=$newId"
        )
    }
}

data class Picture(
    val id: Int,
    val author: String,
    val url: String
)