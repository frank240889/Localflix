package com.darkyellowstudios.localflix.di

import android.content.Context
import org.koin.android.ext.koin.androidContext

/** Android entry point: gives Koin the application Context (needed from Phase 1 for the multicast lock). */
fun initKoinAndroid(context: Context) = initKoin {
    androidContext(context.applicationContext)
}
