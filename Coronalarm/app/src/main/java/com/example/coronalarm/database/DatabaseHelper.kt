package com.example.coronalarm.database

interface DatabaseHelper {

    suspend fun getPower(): Boolean
    suspend fun getCurrentSound(): String
    suspend fun setPower(power: Boolean)
    suspend fun setCurrentSound(currentSound: String)
    suspend fun insertAll(data: Data)

    suspend fun getAllSounds(): List<Sound>
    suspend fun getSoundByName(name: String): Sound
    suspend fun getFavouritedSounds(): List<Sound>
    suspend fun setFavourited(isFavourited: Boolean, name: String)
    suspend fun addSoundToDatabase(sound: Sound)
}