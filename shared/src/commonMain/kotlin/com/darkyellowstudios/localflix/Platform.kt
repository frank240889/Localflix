package com.darkyellowstudios.localflix

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform