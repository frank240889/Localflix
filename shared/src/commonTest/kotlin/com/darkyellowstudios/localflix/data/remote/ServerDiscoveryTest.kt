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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

/** Real UDP on loopback; a fake server stands in for Jellyfin's discovery responder. */
class ServerDiscoveryTest {
    private val living = DiscoveredServer("s1", "Living room", "http://192.168.1.10:8096")
    private val office = DiscoveredServer("s2", "Office", "http://192.168.1.20:8096")
    private val questions = mutableListOf<String>()

    @Test
    fun collectsOneEntryPerServerAndIgnoresOtherReplies() = discoveryTest(
        "not json", reply(living), reply(living), """{"Hello":"other app"}""", reply(office),
    ) { target ->
        assertEquals(listOf(living, office), ServerDiscovery(MulticastLock.None, target, WINDOW).discover())
        assertEquals(listOf(ServerDiscovery.QUESTION), questions)
    }

    @Test
    fun emptyWhenNothingAnswers() = discoveryTest() { target ->
        assertEquals(emptyList(), ServerDiscovery(MulticastLock.None, target, WINDOW).discover())
    }

    @Test
    fun holdsTheMulticastLockOnlyWhileListening() = discoveryTest(reply(living)) { target ->
        val events = mutableListOf<String>()
        val lock = MulticastLock { events += "acquire"; AutoCloseable { events += "release" } }

        ServerDiscovery(lock, target, WINDOW).discover()

        assertEquals(listOf("acquire", "release"), events)
    }

    /** Real sockets need real time, so the test leaves runTest's virtual clock. */
    private fun discoveryTest(vararg replies: String, block: suspend (SocketAddress) -> Unit) = runTest {
        withContext(Dispatchers.Default) {
            SelectorManager().use { selector ->
                aSocket(selector).udp().bind(InetSocketAddress("127.0.0.1", 0)).use { server ->
                    coroutineScope {
                        val responder = launch {
                            for (question in server.incoming) {
                                questions += question.packet.readText()
                                replies.forEach { server.send(Datagram(buildPacket { writeText(it) }, question.address)) }
                            }
                        }
                        block(server.localAddress)
                        responder.cancel()
                    }
                }
            }
        }
    }

    private fun reply(server: DiscoveredServer) =
        """{"Address":"${server.address}","Id":"${server.id}","Name":"${server.name}","EndpointAddress":null}"""

    private companion object {
        val WINDOW = 500.milliseconds
    }
}
