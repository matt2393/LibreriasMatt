package dev.matt2393.utils.location

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object LocPermission {

    private lateinit var regPer: ActivityResultLauncher<Array<String>>
    private lateinit var regGps: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var result: Result
    private lateinit var ac: ComponentActivity

    fun init(ac: ComponentActivity) {
        this.ac = ac
        regPer =
            ac.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (
                    it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    it[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                    result.onPermissionSuccess()
                } else {
                    result.onPermissionError()
                }
            }

        regGps =
            ac.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    result.onGpsSuccess()
                } else {
                    result.onGpsError()
                }
            }
    }

    fun init(fr: Fragment) {
        this.ac = fr.requireActivity()
        regPer =
            ac.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (
                    it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    result.onPermissionSuccess()
                } else {
                    result.onPermissionError()
                }
            }

        regGps =
            ac.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    result.onGpsSuccess()
                } else {
                    result.onGpsError()
                }
            }
    }

    fun launch(success: () -> Unit, error: (err: ErrorType) -> Unit) {
        result = object : Result {
            override fun onPermissionSuccess() {
                val locRequestSimple = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setInterval(100)
                    .setFastestInterval(100)
                val locSettings = LocationSettingsRequest.Builder()
                    .addLocationRequest(locRequestSimple)
                    .setAlwaysShow(true)
                    .build()

                val result = LocationServices.getSettingsClient(ac)
                    .checkLocationSettings(locSettings)

                result.addOnSuccessListener {
                    val state = it.locationSettingsStates
                    if (state!!.isLocationPresent) {
                        success()
                    } else {
                        error(ErrorType.GPS)
                    }
                }
                result.addOnFailureListener {
                    if (it is ResolvableApiException) {
                        try {
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(it.resolution).build()
                            regGps.launch(intentSenderRequest)
                        } catch (e: IntentSender.SendIntentException) {
                            error(ErrorType.GPS)
                        }
                    }
                }

            }

            override fun onPermissionError() {
                error(ErrorType.PERMISSION)
            }

            override fun onGpsSuccess() {
                success()
            }

            override fun onGpsError() {
                error(ErrorType.GPS)
            }

        }

        regPer.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private interface Result {
        fun onPermissionSuccess()
        fun onPermissionError()
        fun onGpsSuccess()
        fun onGpsError()
    }

    enum class ErrorType {
        PERMISSION, GPS
    }
}