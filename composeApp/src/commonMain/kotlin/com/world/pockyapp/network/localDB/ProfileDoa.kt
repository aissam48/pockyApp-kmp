package com.world.pockyapp.network.localDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileModel)

    @Delete
    suspend fun delete(profile: ProfileModel)

    @Query("SELECT * FROM profiles")
    suspend fun getProfile(): List<ProfileModel>

}