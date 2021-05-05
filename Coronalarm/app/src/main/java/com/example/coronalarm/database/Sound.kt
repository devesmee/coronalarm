package com.example.coronalarm.database

import androidx.room.*

@Entity(tableName = "sound")
data class Sound(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "isFavourited") var isFavourited: Boolean,
    @ColumnInfo(name = "fileName") var fileName: String
)

@Dao
interface SoundDao {
    @Query("SELECT * FROM sound")
    suspend fun getAllSounds(): List<Sound>

    @Query("SELECT * FROM sound WHERE `name` IS :name")
    suspend fun getSoundByName(name: String): Sound

    @Query("SELECT * FROM sound WHERE `isFavourited` IS 1")
    suspend fun getFavouritedSounds(): List<Sound>

    @Query("UPDATE sound SET isFavourited = :isFavourited WHERE name LIKE :name")
    suspend fun setFavourited(isFavourited: Boolean, name: String)

    @Insert
    suspend fun insertSound(sound: Sound)
}