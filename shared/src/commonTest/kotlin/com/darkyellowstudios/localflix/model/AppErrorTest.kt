package com.darkyellowstudios.localflix.model

import com.darkyellowstudios.localflix.ui.components.UiState
import com.darkyellowstudios.localflix.ui.components.loadState
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class AppErrorTest {

    @Test
    fun mapsExceptionsToAppErrors() {
        assertIs<AppError.Timeout>(HttpRequestTimeoutException("http://jf/Items", 15_000).toAppError(isOnline = true))
        assertIs<AppError.ServerUnreachable>(IOException("refused").toAppError(isOnline = true))
        assertIs<AppError.NoNetwork>(IOException("refused").toAppError(isOnline = false))
        assertIs<AppError.Unknown>(IllegalArgumentException().toAppError(isOnline = true))
        val already = AppError.PlaybackFailed()
        assertSame(already, already.toAppError(isOnline = true))
    }

    @Test
    fun loadStateWrapsResultOrError() = runTest {
        assertEquals(UiState.Content(42), loadState { 42 })
        assertIs<AppError.Timeout>(assertIs<UiState.Error>(loadState { throw AppError.Timeout() }).error)
        assertIs<AppError.Unknown>(assertIs<UiState.Error>(loadState { error("boom") }).error)
    }
}
