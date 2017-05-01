package com.dylanlange.beaconplay

import android.app.Application
import android.content.Intent
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap

/**
 * Created by dylanlange on 30/04/17.
 */
class BeaconApplication: Application(), BootstrapNotifier {

    lateinit var mBeaconManager: BeaconManager
    lateinit var mRegionBootstrap: RegionBootstrap

    override fun onCreate() {
        super.onCreate()
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        mBeaconManager = BeaconManager.getInstanceForApplication(this)

        mBeaconManager.getBeaconParsers().add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        val region = Region("myMonitoringUniqueId", null, null, null)
        mRegionBootstrap = RegionBootstrap(this, region)
    }

    override fun didDetermineStateForRegion(p0: Int, p1: Region?) {

    }

    override fun didEnterRegion(p0: Region?) {
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        mRegionBootstrap.disable()
        val intent = Intent(this, MainActivity::class.java)
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    }

    override fun didExitRegion(p0: Region?) {

    }

}