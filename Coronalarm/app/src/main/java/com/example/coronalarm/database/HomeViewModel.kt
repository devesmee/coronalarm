package com.example.coronalarm.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(private val dbHelper: DatabaseHelper): ViewModel() {
    var power = true
    var currentSound = " "
    var sound : Sound? = null
    var allSounds = ArrayList<Sound>()
    var favouritedSounds = ArrayList<Sound>()

    fun getPower(): LiveData<Boolean> {
        val result: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                power = dbHelper.getPower()
                result.postValue(power)
            }
        }
        return result
    }

    fun getCurrentSound(): LiveData<String>{
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                currentSound = dbHelper.getCurrentSound()
                result.postValue(currentSound)
            }
        }
        return result
    }

    @JvmName("setCurrentSoundFun")
    fun setCurrentSound(currentSound: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                dbHelper.setCurrentSound(currentSound)
            }
        }
    }

    fun getAllSounds(): LiveData<ArrayList<Sound>> {
        val result = MutableLiveData<ArrayList<Sound>>()
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                allSounds = ArrayList(dbHelper.getAllSounds())
                result.postValue(allSounds)
            }
        }
        return result
    }

    fun getSoundByName(name: String): LiveData<Sound> {
        val result = MutableLiveData<Sound>()
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                sound = dbHelper.getSoundByName(name)
                result.postValue(sound)
            }
        }
        return result
    }

    fun addSound(newSound: Sound) {
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                dbHelper.addSoundToDatabase(newSound)
                getAllSounds()
            }
        }
    }

    fun setFavourited(isFavourited: Boolean, name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                dbHelper.setFavourited(isFavourited, name)
            }
        }
    }

    fun getFavouritedSounds(): LiveData<ArrayList<Sound>> {
        val result = MutableLiveData<ArrayList<Sound>>()
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){
                favouritedSounds = ArrayList(dbHelper.getFavouritedSounds())
                result.postValue(favouritedSounds)
            }
        }
        return result
    }

    @JvmName("setPower1")
    fun setPower(newPower: Boolean){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dbHelper.setPower(newPower)
            }
        }
    }

    fun insertAll(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val data = Data(1, false, "Woop Woop")
                val sound1 = Sound("Woop Woop", true, "woopwoop")
                val sound2 = Sound("Anderhalf meter!", true, "anderhalfmeter")
                val sound3 = Sound("Aura, bitch!", true, "aurabitch")
                val sound4 = Sound("Go away!", true, "goaway")
                val sound5 = Sound("Whistle!", true, "refereewhistle")
                dbHelper.insertAll(data)
                dbHelper.addSoundToDatabase(sound1)
                dbHelper.addSoundToDatabase(sound2)
                dbHelper.addSoundToDatabase(sound3)
                dbHelper.addSoundToDatabase(sound4)
                dbHelper.addSoundToDatabase(sound5)
            }
        }
    }


}