package com.example.coronalarm.database

import androidx.room.*

@Entity(tableName = "data")
data class Data(
    @PrimaryKey val database: Int,
    @ColumnInfo(name = "power") val power: Boolean?,
    @ColumnInfo(name = "currentSound") val currentSound: String?
)

@Dao
interface DataDao {
    @Query("SELECT power FROM data WHERE `database` IS 1")
    suspend fun getPower(): Boolean

    @Query("SELECT currentSound FROM data WHERE `database` IS 1")
    suspend fun getCurrentSound(): String

    @Query("UPDATE data SET power = :power WHERE `database` IS 1")
    suspend fun setPower(power: Boolean)

    @Query("UPDATE data SET currentSound = :currentSound WHERE `database` IS 1")
    suspend fun setCurrentSound(currentSound: String)

    @Insert
    suspend fun insertAll(data: Data)
}

