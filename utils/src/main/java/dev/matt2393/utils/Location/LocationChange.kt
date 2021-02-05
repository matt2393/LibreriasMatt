package com.rayo.rayo.Tools

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dev.matt2393.utils.Location.LocationEnableListener

object LocationChange {
    var locRequestSimple: LocationRequest? = null
    var locRequestMulti: LocationRequest? = null
    var locSettings : LocationSettingsRequest? = null
    const val CODE_PERMISO_LOC = 12321
    const val CODE_ACTIVE_GPS = 32123
    init {
        locRequestSimple = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setInterval(100)
            .setFastestInterval(100)
        locRequestMulti = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(5000)
        locSettings = LocationSettingsRequest.Builder()
            .addLocationRequest(locRequestSimple!!)
            .setAlwaysShow(true)
            .build()

    }

    fun permisos(activity: FragmentActivity,
                 locationEnableListener: LocationEnableListener) {
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
            (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODE_PERMISO_LOC
            )
        }
        else
            verifHabilitarGps(activity, locationEnableListener)
    }
    fun permisos(fragment: Fragment,
                 locationEnableListener: LocationEnableListener) {
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
            (ContextCompat.checkSelfPermission(fragment.context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(fragment.context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {

            fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODE_PERMISO_LOC
            )
        }
        else
            verifHabilitarGps(fragment, locationEnableListener)
    }

    fun comprobarPermisos(requestCode: Int,
                          grantResults: IntArray,
                          activity: FragmentActivity,
                          locationEnableListener: LocationEnableListener) {

        if(requestCode == CODE_PERMISO_LOC){
            if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                verifHabilitarGps(activity, locationEnableListener)
            } else locationEnableListener.errorGpsOrPermisos()
        }
    }

    /**
     * comprobar en onRequestPermissionsResult
     * para verificar si se otorgaron los permisos necesarios
     */
    fun comprobarPermisos(requestCode: Int,
                          grantResults: IntArray,
                          fragment: Fragment,
                          locationEnableListener: LocationEnableListener) {

        if(requestCode == CODE_PERMISO_LOC){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                verifHabilitarGps(fragment, locationEnableListener)
            }else locationEnableListener.errorGpsOrPermisos()
        }
    }

    private fun verifHabilitarGps(activity: FragmentActivity, locationEnableListener: LocationEnableListener) {
        val result = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(locSettings)

        result.addOnSuccessListener {
            val state = it.locationSettingsStates
            if(state.isLocationPresent)
                locationEnableListener.activeGps()
            else locationEnableListener.errorGpsOrPermisos()
        }

        result.addOnFailureListener {
            if(it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(activity, CODE_ACTIVE_GPS)
                }catch (e: IntentSender.SendIntentException){
                    locationEnableListener.errorGpsOrPermisos()
                }
            }
        }

        Unit
    }

    private fun verifHabilitarGps(fragment: Fragment, locationEnableListener: LocationEnableListener) {
        val result = LocationServices.getSettingsClient(fragment.context!!)
            .checkLocationSettings(locSettings)

        result.addOnSuccessListener {
            val state = it.locationSettingsStates
            if(state.isLocationPresent)
                locationEnableListener.activeGps()
            else locationEnableListener.errorGpsOrPermisos()
        }

        result.addOnFailureListener {
            if(it is ResolvableApiException) {
                try {
                    fragment.startIntentSenderForResult(it.resolution.intentSender, CODE_ACTIVE_GPS, null,0,0,0,null)
                    //it.startResolutionForResult(activity, CODE_ACTIVE_GPS)
                }catch (e: IntentSender.SendIntentException){
                    locationEnableListener.errorGpsOrPermisos()
                }
            }
        }

        Unit
    }

    /**
     * Comprobar en OnActivityResult si el GPS se activo
     */
    fun comprobarGps(requestCode: Int,
                     resultCode: Int,
                     locationEnableListener: LocationEnableListener) {
        if(requestCode == CODE_ACTIVE_GPS){
            if( resultCode ==  Activity.RESULT_OK) {
                locationEnableListener.activeGps()
            } else locationEnableListener.errorGpsOrPermisos()
        }
    }


    /**
     * GeoHash
     */

    private const val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    // Cell width x height
    const val a5000k = 1 // ≤ 5000km	×	5000km
    const val a1250k_k = 2 // ≤ 1250km	×	625km
    const val a156k = 3  // ≤ 156km	×	156km
    const val a39_1k = 4  // ≤ 39.1km	×	19.5km
    const val a4_89k = 5  // ≤ 4.89km	×	4.89km
    const val a1_22k = 6  // ≤ 1.22km	×	0.61km
    const val a153m = 7  // ≤ 153m	×	153m
    const val a38_2m = 8  // ≤ 38.2m	×	19.1m
    const val a4_77m = 9  // ≤ 4.77m	×	4.77m
    const val a1_19m = 10  // ≤ 1.19m	×	0.596m
    const val a149mm = 11  // ≤ 149mm	×	149mm
    const val a37_2mm = 12  // ≤ 37.2mm	×	18.6mm


    fun encodeGeoHash(lat: Double, lon: Double, precision: Int): String {
        if (precision==0) throw Exception()

        var idx = 0
        var bit = 0
        var evenBit = true
        var geohash = ""

        var latMin =  -90.0
        var latMax =  90.0
        var lonMin = -180.0
        var lonMax = 180.0

        while (geohash.length < precision) {
            if (evenBit) {
                val lonMid = (lonMin + lonMax) / 2.0
                if (lon >= lonMid) {
                    idx = idx*2 + 1
                    lonMin = lonMid
                } else {
                    idx *= 2
                    lonMax = lonMid
                }
            } else {
                val latMid = (latMin + latMax) / 2
                if (lat >= latMid) {
                    idx = idx*2 + 1;
                    latMin = latMid;
                } else {
                    idx *= 2;
                    latMax = latMid;
                }
            }
            evenBit = !evenBit;

            if (++bit == 5) {
                geohash += base32[idx]
                bit = 0;
                idx = 0;
            }
        }

        return geohash
    }

    fun adjacent(geohashParm: String, directionParm: String): String {
        // based on github.com/davetroy/geohash-js

        val geohash = geohashParm.toLowerCase();
        val direction = directionParm.toLowerCase();

        if (geohash.length == 0) throw Exception()
        if ("nsew".indexOf(direction) == -1) throw Exception()

        val neighbour = HashMap<String, ArrayList<String>>()
        neighbour["n"]= arrayListOf("p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx")
        neighbour["s"]= arrayListOf("14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp")
        neighbour["e"]= arrayListOf("bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy")
        neighbour["w"]= arrayListOf("238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb")

        val border = HashMap<String, ArrayList<String>>()
        border["n"] = arrayListOf("prxz","bcfguvyz")
        border["s"] = arrayListOf("028b","0145hjnp")
        border["e"] = arrayListOf("bcfguvyz", "prxz")
        border["w"] = arrayListOf("0145hjnp", "028b")
        ;

        val lastCh = geohash.last()   // last character of hash
        var parent = geohash.substring(0, geohash.length-1); // hash without last character

        val type = geohash.length % 2;

        // check for edge-cases which don't share common prefix
        if (border[direction]!![type].indexOf(lastCh) != -1 && parent != "") {
            parent = adjacent(parent, direction);
        }

        // append letter for direction to parent
        return parent + base32[neighbour[direction]!![type].indexOf(lastCh)];
    }


    /**
     * Retorna todas las celdas adyacentes al geohash.
     *
     * @param   {string} geohash.
     * @returns {{n,ne,e,se,s,sw,w,nw: string}}
     * @throws  Invalid geohash.
     */
    fun neighbours(geohash: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["n"] = adjacent(geohash, "n")
        map["ne"] = adjacent(adjacent(geohash, "n"), "e")
        map["e"] = adjacent(geohash, "e")
        map["se"] = adjacent(adjacent(geohash, "s"), "e")
        map["s"] = adjacent(geohash, "s")
        map["sw"] = adjacent(adjacent(geohash, "s"), "w")
        map["w"] = adjacent(geohash, "w")
        map["nw"] = adjacent(adjacent(geohash, "n"), "w")
        return map
        /*return {
            "n":  Geohash.adjacent(geohash, 'n'),
            'ne': Geohash.adjacent(Geohash.adjacent(geohash, 'n'), 'e'),
            'e':  Geohash.adjacent(geohash, 'e'),
            'se': Geohash.adjacent(Geohash.adjacent(geohash, 's'), 'e'),
            's':  Geohash.adjacent(geohash, 's'),
            'sw': Geohash.adjacent(Geohash.adjacent(geohash, 's'), 'w'),
            'w':  Geohash.adjacent(geohash, 'w'),
            'nw': Geohash.adjacent(Geohash.adjacent(geohash, 'n'), 'w'),
        };*/
    }
}