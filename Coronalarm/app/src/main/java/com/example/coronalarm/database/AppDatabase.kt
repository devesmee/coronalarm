package com.example.coronalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Data::class, Sound::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao
    abstract fun soundDao(): SoundDao
}