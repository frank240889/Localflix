package com.darkyellowstudios.localflix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darkyellowstudios.localflix.model.UserProfile
import com.darkyellowstudios.localflix.ui.theme.IndustrialBlack
import com.darkyellowstudios.localflix.ui.theme.IndustrialLead
import com.darkyellowstudios.localflix.ui.theme.IndustrialYellow

@Composable
fun ProfileScreen(
    onProfileSelected: (UserProfile) -> Unit,
    notice: String? = null,
) {
    val profiles = listOf(
        UserProfile("1", "Dad", ""),
        UserProfile("2", "Mom", ""),
        UserProfile("3", "Kids", ""),
        UserProfile("4", "Add Profile", "", isAddProfile = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Who's Watching?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = if (notice == null) 40.dp else 12.dp) // Section gap: 40px
        )
        if (notice != null) {
            Text(
                text = notice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 28.dp),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            items(profiles) { profile ->
                ProfileItem(profile = profile, onClick = { onProfileSelected(profile) })
            }
        }
    }
}

@Composable
fun ProfileItem(
    profile: UserProfile,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                // Avatars are full-rounded, Add Profile is 4px radius
                .clip(if (profile.isAddProfile) RoundedCornerShape(4.dp) else CircleShape)
                .background(IndustrialLead),
            contentAlignment = Alignment.Center
        ) {
            if (profile.isAddProfile) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (profile.name == "Kids") Color(0xFF4DB6AC) else Color(0xFF303030)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile.name == "Kids") {
                        Text(
                            "K",
                            style = MaterialTheme.typography.headlineLarge.copy(color = IndustrialBlack)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = profile.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
