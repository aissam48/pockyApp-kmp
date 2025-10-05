package com.world.pockyapp.network.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.world.pockyapp.network.models.model.ProfileModel


@Database(
    entities = [ProfileModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ProfileDB : RoomDatabase() {

    abstract fun profileDoa(): ProfileDoa

}