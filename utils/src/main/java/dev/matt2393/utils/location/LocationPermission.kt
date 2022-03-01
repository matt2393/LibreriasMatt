package dev.matt2393.utils.location

/*import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dev.matt2393.utils.aux.FragmentAux
import dev.matt2393.utils.aux.ResultListener

object LocationPermission {

    fun with(activity: AppCompatActivity): LocationPermissionBuild {
        return LocationPermissionBuild(activity)
    }
    fun with(fragment: Fragment): LocationPermissionBuild {
        return LocationPermissionBuild(fragment)
    }

    enum class ErrorLocation {
        PERMISSION, GPS
    }

    class LocationPermissionBuild {
        private var activity: AppCompatActivity? =null
        private var fragment: Fragment? = null
        private var fragmentManager: FragmentManager
        private lateinit var locRequestSimple: LocationRequest
        private lateinit var locRequestMulti: LocationRequest
        private lateinit var locSettings : LocationSettingsRequest

        companion object {
            const val CODE_PERMISO_LOC = 12321
            const val CODE_ACTIVE_GPS = 32123
        }
        constructor(activity: AppCompatActivity) {
            this.activity = activity
            this.fragment = null
            fragmentManager = activity.supportFragmentManager
            init()
        }
        constructor(fragment: Fragment) {
            this.fragment = fragment
            this.activity = null
            fragmentManager = fragment.childFragmentManager
            init()
        }
        private fun init(){
            locRequestSimple = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(100)
                .setFastestInterval(100)
            locRequestMulti = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000)
            locSettings = LocationSettingsRequest.Builder()
                .addLocationRequest(locRequestSimple)
                .setAlwaysShow(true)
                .build()
        }
        private fun initFragment() {

        }

        private fun getContext() = if(activity==null) { fragment!!.requireContext() } else { activity!! }
        private fun getActiviyOrFragment() = if(activity==null) { fragment!! } else { activity!! }


        fun request(success: () -> Unit,
                     error: (ErrorLocation) -> Unit) {
            if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
                val resultListener = object: ResultListener {
                    override fun permissionResult(permissions: Map<String, Boolean>) {
                        if(permissions[Manifest.permission.ACCESS_FINE_LOCATION]!=null
                            && permissions[Manifest.permission.ACCESS_FINE_LOCATION]!!) {
                            verifHabilitarGps(success, error)
                        } else {
                            error(ErrorLocation.PERMISSION)
                        }
                    }
                }
                fragmentManager.beginTransaction()
                    .add(
                        FragmentAux.newInstance(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            resultListener
                        ), FragmentAux.TAG
                    ).commitNowAllowingStateLoss()
            }
            else {
                Log.e("aaa","si")
                verifHabilitarGps(success, error)
            }
        }

        private fun verifHabilitarGps(success: () -> Unit,
                                      error: (ErrorLocation) -> Unit) {

            val result = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(locSettings)

            result.addOnSuccessListener {
                val state = it.locationSettingsStates
                if(state!!.isLocationPresent) {
                    success()
                }
                else {
                    error(ErrorLocation.GPS)
                }
            }

            result.addOnFailureListener {
                if(it is ResolvableApiException) {
                    try {
                        val resultListener = object: ResultListener {
                            override fun gpsResult(result: ActivityResult) {
                                if (result.resultCode == Activity.RESULT_OK) {
                                    success()
                                } else {
                                    error(ErrorLocation.GPS)
                                }
                            }
                        }
                        val intentSenderRequest =  IntentSenderRequest.Builder(it.resolution).build()
                        fragmentManager.beginTransaction()
                            .add(
                                FragmentAux.newInstance(
                                    intentSenderRequest,
                                    resultListener
                                ), FragmentAux.TAG
                            ).commitNowAllowingStateLoss()
                    }catch (e: IntentSender.SendIntentException){
                        error(ErrorLocation.GPS)
                    }
                }
            }
        }
    }



    /**
     * GeoHash
     */

    private class LocationGeoHash {
        companion object {
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
        }
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

}*/