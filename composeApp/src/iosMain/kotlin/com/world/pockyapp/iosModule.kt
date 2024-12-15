package com.world.pockyapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import createDataStore
import org.koin.dsl.module


val iosModule = module {
    single<DataStore<Preferences>> { createDataStore() }
}