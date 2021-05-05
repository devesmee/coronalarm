package com.example.coronalarm.database

class DatabaseHelperImpl(private val appDatabase: AppDatabase): DatabaseHelper {
    override suspend fun getPower(): Boolean = appDatabase.dataDao().getPower()

    override suspend fun getCurrentSound(): String = appDatabase.dataDao().getCurrentSound()

    override suspend fun setPower(power: Boolean) = appDatabase.dataDao().setPower(power)

    override suspend fun setCurrentSound(currentSound: String) = appDatabase.dataDao().setCurrentSound(currentSound)

    override suspend fun insertAll(data: Data) = appDatabase.dataDao().insertAll(data)

    override suspend fun getAllSounds(): List<Sound> = appDatabase.soundDao().getAllSounds()

    override suspend fun getSoundByName(name: String): Sound = appDatabase.soundDao().getSoundByName(name)

    override suspend fun getFavouritedSounds(): List<Sound> = appDatabase.soundDao().getFavouritedSounds()

    override suspend fun setFavourited(isFavourited: Boolean, name: String) = appDatabase.soundDao().setFavourited(isFavourited, name)

    override suspend fun addSoundToDatabase(sound: Sound) = appDatabase.soundDao().insertSound(sound)
}