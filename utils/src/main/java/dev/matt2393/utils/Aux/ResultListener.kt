package dev.matt2393.utils.Aux

import androidx.activity.result.ActivityResult

internal interface ResultListener {
    fun permissionResult(permissions: Map<String, Boolean>){}
    fun gpsResult(result: ActivityResult){}
}