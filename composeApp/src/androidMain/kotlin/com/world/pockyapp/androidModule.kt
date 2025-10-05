package com.world.pockyapp

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.world.pockyapp.database.getDatabaseBuilder
import createDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val androidModule = module {

    single<DataStore<Preferences>> { createDataStore(androidApplication()) }

    single <Activity>{ Activity() }

    single  { getDatabaseBuilder(androidApplication()) }

}