package com.dylanlange.beaconplay

import android.Manifest
import android.os.Bundle
import android.os.RemoteException
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dylanlange.beaconplay.test.Coord
import org.altbeacon.beacon.*
import org.altbeacon.beacon.powersave.BackgroundPowerSaver

/**
 * Created by dylanlange on 29/04/17.
 */
class MainActivity: AppCompatActivity(), BeaconConsumer {

    val TAG: String = MainActivity::class.java.simpleName
    val PERMISSION_REQUEST_CODE: Int = 69
    lateinit var mBeaconManager: BeaconManager
    lateinit var mBackgroundPowerSaver: BackgroundPowerSaver//apparently holding reference to this in the activity saves about 60% battery?
    lateinit var mBeaconToPositionMap: Map<Beacon, Coord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions(
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                , PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this)
            mBackgroundPowerSaver = BackgroundPowerSaver(this)
            mBeaconToPositionMap = HashMap<Beacon, Coord>()

            // To detect proprietary beacons, you must add a line like below corresponding to your beacon
            // type.  Do a web search for "setBeaconLayout" to get the proper expression.
            // beaconManager.getBeaconParsers().add(new BeaconParser().
            //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

            mBeaconManager.bind(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBeaconManager.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        setupMonitor()
        setupRangeNotifier()
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(Region("myMonitoringUniqueId", null, null, null))
            mBeaconManager.startRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private fun setupMonitor(){
        mBeaconManager.addMonitorNotifier(object: MonitorNotifier {

            override fun didDetermineStateForRegion(state: Int, region: Region?) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state)
            }

            override fun didEnterRegion(region: Region?) {
                Log.i(TAG, "I just saw a beacon for the first time!")
            }

            override fun didExitRegion(region: Region?) {
                Log.i(TAG, "I no longer see a beacon")
            }

        })
    }

    private fun setupRangeNotifier() {
        mBeaconManager.addRangeNotifier(object: RangeNotifier {

            override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                if(beacons == null) return
                Log.d(TAG, beacons.toString())
                /*if (beacons.isNotEmpty()) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                }*/
                if(beacons.size < 3) {
                    Log.e(TAG, "Not enough beacons to calculate approximate position")
                    return
                }
                for(b: Beacon in beacons){
                    Log.d(TAG + "DISTANCE:", b.distance.toString())
                }
            }

        })
    }

}