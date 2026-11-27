package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.MulticastLock
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.SocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.writeText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** A Jellyfin server that answered discovery, as its reply describes it. */
@Serializable
data class DiscoveredServer(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
    @SerialName("Address") val address: String,
)

/**
 * Jellyfin's UDP discovery: broadcasts "who is JellyfinServer?" to port 7359 and collects
 * the JSON replies for [window], one per server. Returns an empty list when nothing answers
 * or UDP isn't available (web; iOS devices until the multicast entitlement is granted).
 */
class ServerDiscovery(
    private val multicastLock: MulticastLock,
    private val target: SocketAddress = InetSocketAddress("255.255.255.255", 7359),
    private val window: Duration = 2.seconds,
) {
    suspend fun discover(): List<DiscoveredServer> = try {
        multicastLock.acquire().use { broadcast() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        println("Localflix UDP discovery unavailable: $e")
        emptyList()
    }

    private suspend fun broadcast(): List<DiscoveredServer> = SelectorManager().use { selector ->
        aSocket(selector).udp().bind(InetSocketAddress("0.0.0.0", 0)) { broadcast = true }.use { socket ->
            socket.send(Datagram(buildPacket { writeText(QUESTION) }, target))
            val found = LinkedHashMap<String, DiscoveredServer>()
            withTimeoutOrNull(window) {
                for (reply in socket.incoming) reply.packet.readText().toServer()?.let { found.getOrPut(it.id) { it } }
            }
            found.values.toList()
        }
    }

    internal companion object {
        const val QUESTION = "who is JellyfinServer?"
        private val json = Json { ignoreUnknownKeys = true }

        /** Anything else on the port (other apps, garbage) is ignored. */
        fun String.toServer(): DiscoveredServer? = runCatching { json.decodeFromString<DiscoveredServer>(this) }.getOrNull()
    }
}
