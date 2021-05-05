package com.example.coronalarm

import android.app.*
import android.content.Intent
import android.util.Log
import com.example.coronalarm.database.Sound
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap


class BackgroundBeaconScanning: Application(), BootstrapNotifier {

    /**************************************************/
    /**               Application class              **/
    /**************************************************/

    private val TAG = ".BackgroundBeaconScanning"

    private var regionBootstrap: RegionBootstrap? = null
    lateinit var beaconManager: BeaconManager

    private var backgroundPowerSaver: BackgroundPowerSaver? = null
    private lateinit var notificationManager: NotificationManager

    var power = true
    var sound = Sound("Woop Woop", true, "woopwoop")

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "App started up")

        beaconManager = BeaconManager.getInstanceForApplication(this)
        val builder = Notification.Builder(this)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentTitle("Scanning is active.")
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        val channel = NotificationChannel(
            "My Notification Channel ID",
            "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "My Notification Channel Description"
        notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        builder.setChannelId(channel.id)
        beaconManager.enableForegroundServiceScanning(builder.build(), 456)
        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100

        val region = Region("region", null, null, null)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        regionBootstrap = RegionBootstrap(this, region)
        backgroundPowerSaver = BackgroundPowerSaver(this)

    }

        override fun didDetermineStateForRegion(arg0: Int, arg1: Region?) {
        // Don't care
    }

    override fun didEnterRegion(arg0: Region?) {
        Log.d(TAG, "Got a didEnterRegion call")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // Make sure application is only started if power is true.
        if (power)
        {
            this.startActivity(intent)
        }
        else{
            beaconManager.disableForegroundServiceScanning()
        }
    }

    override fun didExitRegion(arg0: Region?) {
        // Don't care
    }

    fun disableAlarm() {
        if (regionBootstrap != null) {
            regionBootstrap!!.disable()
            regionBootstrap = null
        }
        beaconManager.removeAllRangeNotifiers()
        beaconManager.removeAllMonitorNotifiers()
        notificationManager.cancelAll()
    }

    fun enableAlarm() {
        val region = Region(
            "region",
            null, null, null
        )
        regionBootstrap = RegionBootstrap(this, region)
    }

}