package com.darkyellowstudios.localflix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkyellowstudios.localflix.model.CastMember
import com.darkyellowstudios.localflix.model.Movie
import com.darkyellowstudios.localflix.ui.theme.*

@Composable
fun PlayerScreen(
    movie: Movie,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialObsidian)
            .verticalScroll(scrollState)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = IndustrialWhite
                )
            }
            Text(
                text = movie.title.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontFamily = Sora,
                    fontWeight = FontWeight.Bold
                ),
                color = IndustrialWhite,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Profile icon placeholder
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(IndustrialLead)
            )
        }

        // Video Player Placeholder (16:9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(IndustrialBlack),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A))
            )
            
            // Play Button (Solid Yellow, 4px radius)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(IndustrialYellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = IndustrialBlack,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Progress Bar (Track #2A2A2A, Fill #E5DE00)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(IndustrialLead)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .fillMaxHeight()
                        .background(IndustrialYellow)
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = IndustrialWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata (JetBrains Mono)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(movie.year.toString(), style = MaterialTheme.typography.labelMedium, color = IndustrialSlate)
                Text("  •  ", color = IndustrialSlate)
                Text(movie.rating, style = MaterialTheme.typography.labelMedium, color = IndustrialSlate)
                Text("  •  ", color = IndustrialSlate)
                Text(movie.duration, style = MaterialTheme.typography.labelMedium, color = IndustrialSlate)
                Text("  •  ", color = IndustrialSlate)
                Icon(Icons.Default.Star, contentDescription = null, tint = IndustrialYellow, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(movie.score.toString(), style = MaterialTheme.typography.labelMedium, color = IndustrialYellow)
            }

            // Section gap: 40px
            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "My List",
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Download",
                    icon = Icons.Default.Download,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = movie.description,
                style = MaterialTheme.typography.bodyMedium,
                color = IndustrialSlate,
                lineHeight = 24.sp
            )

            // Section gap: 40px
            Spacer(modifier = Modifier.height(40.dp))

            // Cast (Horizontal Scroll)
            Text(
                text = "CAST",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = IndustrialWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val dummyCast = listOf(
                CastMember("Matthew M.", "Cooper", ""),
                CastMember("Anne H.", "Brand", ""),
                CastMember("Jessica C.", "Murph", "")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(dummyCast) { cast ->
                    CastCard(cast)
                }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = IndustrialLead,
            contentColor = IndustrialWhite
        ),
        modifier = modifier.height(48.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = IndustrialYellow, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun CastCard(cast: CastMember) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(IndustrialLead)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp)) // Machined look
                .background(IndustrialCharcoal)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(cast.name, style = MaterialTheme.typography.labelMedium, color = IndustrialWhite)
            Text(cast.role, style = MaterialTheme.typography.labelSmall, color = IndustrialSlate)
        }
    }
}
