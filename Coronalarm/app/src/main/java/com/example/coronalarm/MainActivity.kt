package com.example.coronalarm

import android.Manifest
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.coronalarm.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.*

class MainActivity : AppCompatActivity(), RangeNotifier, BeaconConsumer {
    private var TAG:String = ".MainActivity"

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var viewModel: HomeViewModel
    lateinit var application: BackgroundBeaconScanning

    lateinit var beaconManager: BeaconManager
    lateinit var region: Region

    lateinit var mp: MediaPlayer
    var soundsArray = ArrayList<Sound>()
    var favouritedSoundsArray = ArrayList<Sound>()

    val permissions_all = 1
    @RequiresApi(Build.VERSION_CODES.Q)
    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.INTERNET,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application = (applicationContext as BackgroundBeaconScanning)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance(application))
            )
        )
            .get(HomeViewModel::class.java)

        // Ask for permissions.
        if (!hasPermissions(this, *permissions)) {
                requestPermissions(permissions, permissions_all)
        } else {
            // Initialize sound and media player for sound.
            Log.i(TAG, "Permissions fulfilled else.")
            val completePath = Environment.getExternalStorageDirectory()
                .toString() + "/" + application.sound.fileName
            mp = MediaPlayer.create(this, Uri.parse(completePath))
        }

        // Initialize view model and database.
        val viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance(application))
            )
        )
            .get(HomeViewModel::class.java)

        // Check for first run.
        val preferences = getSharedPreferences("myprefs", AppCompatActivity.MODE_PRIVATE)
        if (preferences.getBoolean("firstLogin", true)) {
            // First run.
            val editor = preferences.edit()
            editor.putBoolean("firstLogin", false)
            editor.apply()
            Log.i(TAG, "first run.")

            // Create database and put all value inside.
            viewModel.insertAll()
        }

        viewModel.getAllSounds().observe(this, Observer {
            soundsArray = it
            if (savedInstanceState == null) {
                checkFavourites()
            }
        })

        bottomNavigation = findViewById(R.id.bottomNav)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    checkFavourites()
                }
                R.id.navSounds -> {
                    val fragment = SoundsFragment()
                    openFragment(fragment)
                }
            }
            true
        }

        // Hide top bar.
        supportActionBar?.hide()

        // Initialize region and beacon manager.
        region = Region("region", null, null, null)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))

        createBeacon()
    }

    /**************************************************/
    /**                   UI Helper                  **/
    /**************************************************/
    private fun openFragment(fragment: Fragment) {
        // Function to open fragment based on selected tab bar.
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**************************************************/
    /**              Permissions Helpers             **/
    /**************************************************/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Permission request results handler.
        when (requestCode) {
            permissions_all -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    // Initialize sound and media player for sound.
                    val completePath = Environment.getExternalStorageDirectory()
                        .toString() + "/" + application.sound.fileName
                    mp = MediaPlayer.create(this, Uri.parse(completePath))
                }
            }
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        // Function to check if the required permissions are granted.
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    /**************************************************/
    /**                Beacon Activity               **/
    /**************************************************/
    override fun onBeaconServiceConnect() {
        // Function that handles behavior when a beacon service is connected.
        beaconManager.removeAllRangeNotifiers()
        beaconManager.addRangeNotifier { beacons, region ->
            didRangeBeaconsInRegion(beacons, region)
        }
        try {
            if (application.power){
                beaconManager.startRangingBeaconsInRegion(this.region)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun createBeacon() {
        // Function to create a new beacon with a specified ID.
        val beacon = Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
            .setTxPower(-59)
            .setDataFields(mutableListOf(0L))// Remove this for beacon layouts without d: fields
            .build()
        // Change the layout below for other beacon types
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(TAG, "Advertisement start succeeded.")
            }
        })
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>, region: Region) {
        // Function that is called when a beacon is detected in a region.
        for (beacon in beacons){
            Log.i(TAG, "Distance: " + beacon.distance + " m, RSSI: " + beacon.rssi)
            if (beacon.distance < 0.3){
                mp.start()
            }
        }
    }

    /**************************************************/
    /**                Beacon Helper                 **/
    /**************************************************/
    fun changeScanning(){
        // Turn beacon on and off depending on the state of the power button.
        if (application.power){
            beaconManager.removeAllMonitorNotifiers()
            beaconManager.removeAllRangeNotifiers()
            beaconManager.unbind(this)
            application.disableAlarm()

            beaconManager.bind(this)
            application.enableAlarm()
            Log.i(TAG, "changeScanning(): scanning turned ON")
        } else {
            beaconManager.removeAllMonitorNotifiers()
            beaconManager.removeAllRangeNotifiers()
            beaconManager.unbind(this)
            application.disableAlarm()
            Log.i(TAG, "changeScanning(): scanning turned OFF")
        }
    }

    /**************************************************/
    /**  Functions to define app behavior when the   **/
    /**           sound is changed.                  **/
    /**************************************************/
    fun setNewSound(newSound: Sound){
        // Change value of sound variable.
        if (newSound != application.sound){
            application.sound = newSound
            onSoundChanged()
        }
    }

    private fun onSoundChanged(){
        // Handle sound change.
        val completePath = Environment.getExternalStorageDirectory()
            .toString() + "/" + application.sound.fileName
        if (mp.isPlaying){
            mp.stop()
            mp.release()
            mp = MediaPlayer.create(this, Uri.parse(completePath))
            mp.start();
        } else {
            mp.release()
            mp = MediaPlayer.create(this, Uri.parse(completePath))
        }
    }

    private fun checkFavourites() {
        // Check if there are 5 favourites, if not remain on Sounds fragment and show message.
        favouritedSoundsArray.clear()
        favouritedSoundsArray.trimToSize()

        viewModel.getFavouritedSounds().observe(this, Observer {
            favouritedSoundsArray = it
            when {
                favouritedSoundsArray.size == 5 ->
                {
                    val fragment = HomeFragment()
                    openFragment(fragment)
                }
                favouritedSoundsArray.size > 5 ->
                {
                    val snackBar = Snackbar.make(
                        findViewById(android.R.id.content),
                        "You can only have 5 favourites.", Snackbar.LENGTH_LONG
                    )
                    snackBar.show()
                    val fragment = SoundsFragment()
                    openFragment(fragment)
                    bottomNavigation.setSelectedItemId(R.id.navSounds);
                }
                favouritedSoundsArray.size < 5 ->
                {
                    val snackBar = Snackbar.make(findViewById(android.R.id.content),
                        "You need to have at least 5 favourites.", Snackbar.LENGTH_LONG
                    )
                    snackBar.show()
                    val fragment = SoundsFragment()
                    openFragment(fragment)
                    bottomNavigation.setSelectedItemId(R.id.navSounds);
                }
            }
        })
    }
}
