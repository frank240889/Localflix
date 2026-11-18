package com.darkyellowstudios.localflix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkyellowstudios.localflix.data.sample.SampleMovies
import com.darkyellowstudios.localflix.model.Movie
import com.darkyellowstudios.localflix.ui.theme.*

@Composable
fun LibraryScreen(
    onMovieSelected: (Movie) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val genres = listOf("ALL MOVIES", "ACTION", "SCI-FI", "THRILLER", "DRAMA")
    var selectedGenre by remember { mutableStateOf("ALL MOVIES") }

    val movies = SampleMovies.movies

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom Search Bar as per Brief: Obsidian background, Lead border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(IndustrialObsidian)
                .border(
                    width = if (isSearchFocused) 2.dp else 1.dp,
                    color = if (isSearchFocused) IndustrialYellow else IndustrialLead,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = IndustrialSlate,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            "Search movies...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = IndustrialSlate
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = IndustrialWhite),
                        cursorBrush = SolidColor(IndustrialYellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isSearchFocused = it.isFocused }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Genre Chips (Standard 4px radius)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = genre == selectedGenre
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) IndustrialYellow else IndustrialCharcoal)
                        .clickable { selectedGenre = genre }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) IndustrialBlack else IndustrialWhite
                    )
                }
            }
        }

        // Section gap: 40px
        Spacer(modifier = Modifier.height(40.dp))

        // Movie Grid (12px gutter)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie, onClick = { onMovieSelected(movie) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(4.dp))
                .background(IndustrialLead)
        ) {
            // Placeholder for Poster
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold // Sora Bold as per brief
            ),
            color = IndustrialWhite,
            maxLines = 1
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = movie.genre,
                style = MaterialTheme.typography.labelSmall, // JetBrains Mono
                color = IndustrialSlate
            )
            Text(
                text = " • ",
                style = MaterialTheme.typography.labelSmall,
                color = IndustrialSlate
            )
            Text(
                text = movie.year.toString(),
                style = MaterialTheme.typography.labelSmall, // JetBrains Mono
                color = IndustrialSlate
            )
        }
    }
}
