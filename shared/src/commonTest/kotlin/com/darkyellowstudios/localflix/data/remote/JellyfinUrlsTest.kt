package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import kotlin.test.Test
import kotlin.test.assertEquals

/** URLs and headers the app builds without calling the server. */
class JellyfinUrlsTest {
    private val server = FakeJellyfinServer()
    private val session = FakeJellyfinServer.SESSION

    @Test
    fun buildsStreamAndHlsUrls() {
        assertEquals(
            "http://jf.local:8096/Videos/m1/stream?static=true&api_key=tok123",
            server.api.streamUrl(session, "m1"),
        )
        assertEquals(
            "http://jf.local:8096/Videos/m1/master.m3u8?MediaSourceId=m1" +
                "&DeviceId=${server.store.deviceId}&api_key=tok123",
            server.api.hlsUrl(session, "m1"),
        )
    }

    @Test
    fun authHeaderMatchesJellyfinFormat() {
        assertEquals(
            """MediaBrowser Client="Localflix", Device="Localflix KMP", DeviceId="d1", Version="1.0.0"""",
            authHeader("d1", null),
        )
        assertEquals(
            """MediaBrowser Client="Localflix", Device="Localflix KMP", DeviceId="d1", Version="1.0.0", Token="t"""",
            authHeader("d1", "t"),
        )
    }
}
