package com.darkyellowstudios.localflix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.ui.theme.IndustrialBlack
import com.darkyellowstudios.localflix.ui.theme.IndustrialLead
import com.darkyellowstudios.localflix.ui.theme.IndustrialObsidian
import com.darkyellowstudios.localflix.ui.theme.IndustrialSlate
import com.darkyellowstudios.localflix.ui.theme.IndustrialWhite
import com.darkyellowstudios.localflix.ui.theme.IndustrialYellow
import kotlinx.coroutines.CancellationException

/** What a screen that loads data is showing. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Content<T>(val data: T) : UiState<T>
    data class Error(val error: AppError) : UiState<Nothing>
}

/** Runs [block] and wraps the outcome as [UiState.Content] or [UiState.Error]. */
suspend fun <T> loadState(block: suspend () -> T): UiState<T> = try {
    UiState.Content(block())
} catch (e: CancellationException) {
    throw e
} catch (e: AppError) {
    UiState.Error(e)
} catch (e: Exception) {
    UiState.Error(AppError.Unknown(e))
}

/** Shows a spinner, the error screen with Retry, or [content], depending on [state]. */
@Composable
fun <T> StateContent(state: UiState<T>, onRetry: () -> Unit, content: @Composable (T) -> Unit) {
    when (state) {
        UiState.Loading -> Box(Modifier.fillMaxSize().background(IndustrialObsidian), Alignment.Center) {
            CircularProgressIndicator(color = IndustrialYellow)
        }
        is UiState.Error -> ErrorScreen(state.error, onRetry)
        is UiState.Content -> content(state.data)
    }
}

/** Full-screen error: what happened, a Retry button and an optional second way out. */
@Composable
fun ErrorScreen(
    error: AppError,
    onRetry: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {},
) {
    val (title, message) = error.userMessage()
    Column(
        modifier = Modifier.fillMaxSize().background(IndustrialObsidian).padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, color = IndustrialWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(message, color = IndustrialSlate, fontSize = 15.sp, textAlign = TextAlign.Center)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = IndustrialYellow, contentColor = IndustrialBlack),
        ) { Text("Retry", fontWeight = FontWeight.Bold) }
        if (secondaryLabel != null) {
            TextButton(onClick = onSecondary) { Text(secondaryLabel, color = IndustrialWhite) }
        }
    }
}

/** Thin banner over content that stays on screen, e.g. "Reconnecting…". */
@Composable
fun RetryBanner(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Row(
        modifier = modifier.fillMaxWidth().background(IndustrialLead).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(message, color = IndustrialWhite, fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (onRetry != null) {
            TextButton(onClick = onRetry) { Text("Retry", color = IndustrialYellow, fontWeight = FontWeight.Bold) }
        }
    }
}

/** Title and explanation shown to the user; every message says what to do next. */
fun AppError.userMessage(): Pair<String, String> = when (this) {
    is AppError.NoNetwork -> "No network" to "Connect to your home Wi-Fi, then try again."
    is AppError.ServerUnreachable -> "Can't reach the server" to "Check that your Jellyfin server is on and on the same network."
    is AppError.Timeout -> "The server is taking too long" to "It may be busy. Try again in a moment."
    is AppError.Unauthorized -> "Signed out" to "Your session ended. Choose your profile to sign in again."
    is AppError.WrongCredentials -> "Wrong password" to "Check the password and try again."
    is AppError.ServerError -> "Server problem" to "The server returned an error ($status). Try again later."
    is AppError.PlaybackFailed -> "Can't play this title" to "Try again, or pick something else."
    is AppError.Unknown -> "Something went wrong" to "Try again. If it keeps happening, restart the app."
}
