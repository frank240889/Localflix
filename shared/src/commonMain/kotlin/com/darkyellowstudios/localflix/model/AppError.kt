package com.darkyellowstudios.localflix.model

import com.darkyellowstudios.localflix.ConnectivityMonitor
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

/**
 * Every failure the app can show the user. Repositories throw these instead of raw
 * Ktor / IO exceptions, so screens only handle one type.
 */
sealed class AppError(cause: Throwable? = null) : Exception(cause) {
    class NoNetwork(cause: Throwable? = null) : AppError(cause)
    class ServerUnreachable(cause: Throwable? = null) : AppError(cause)
    class Timeout(cause: Throwable? = null) : AppError(cause)
    /** The saved token was revoked or expired (HTTP 401 on an authenticated call). */
    class Unauthorized(cause: Throwable? = null) : AppError(cause)
    class WrongCredentials(cause: Throwable? = null) : AppError(cause)
    class ServerError(val status: Int, cause: Throwable? = null) : AppError(cause)
    class PlaybackFailed(cause: Throwable? = null) : AppError(cause)
    class Unknown(cause: Throwable? = null) : AppError(cause)
}

/** Maps any exception from the network layer to an [AppError]. */
internal fun Throwable.toAppError(isOnline: Boolean): AppError = when (this) {
    is AppError -> this
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> AppError.Timeout(this)
    is ResponseException -> when (response.status) {
        HttpStatusCode.Unauthorized -> AppError.Unauthorized(this)
        else -> AppError.ServerError(response.status.value, this)
    }
    is IOException -> if (isOnline) AppError.ServerUnreachable(this) else AppError.NoNetwork(this)
    else -> AppError.Unknown(this)
}

/** Runs a network call and rethrows its failure as an [AppError]; cancellation passes through. */
internal suspend fun <T> ConnectivityMonitor.apiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        throw e.toAppError(isOnline.value)
    }
