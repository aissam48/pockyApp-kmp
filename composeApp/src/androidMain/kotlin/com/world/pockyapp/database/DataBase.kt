package com.world.pockyapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.world.pockyapp.network.localDB.ProfileDB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ProfileDB> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("local.db")
    return Room.databaseBuilder<ProfileDB>(
        context = appContext,
        name = dbFile.absolutePath
    )

}
