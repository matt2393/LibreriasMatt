package dev.matt2393.utils.Permission

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object PermissionRequest {
    const val CODE_PERMISOS = 12321

    fun permisos(activity: FragmentActivity,
                 permissionListener: PermissionListener,
                 vararg permissions: String
    ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val aux = arrayListOf<String>()
            permissions.forEach {
                if(ContextCompat.checkSelfPermission(activity, it)
                    != PackageManager.PERMISSION_GRANTED) {
                    aux.add(it)
                }

            }

            if(aux.size>0) {
                ActivityCompat.requestPermissions(
                    activity,
                    aux.toTypedArray(),
                    CODE_PERMISOS
                )
            }else{
                permissionListener.permissionGranted()
            }
        }
        else
            permissionListener.permissionGranted()
    }
    fun permisos(fragment: Fragment,
                 permissionListener: PermissionListener,
                 vararg permissions: String
    ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val aux = arrayListOf<String>()
            permissions.forEach {
                if(ContextCompat.checkSelfPermission(fragment.context!!, it)
                            != PackageManager.PERMISSION_GRANTED) {
                    aux.add(it)
                }

            }
            if(aux.size>0) {
                fragment.requestPermissions(
                    aux.toTypedArray(),
                    CODE_PERMISOS
                )
            }else{
                permissionListener.permissionGranted()
            }


        }
        else
            permissionListener.permissionGranted()
    }

    /**
     * comprobar en onRequestPermissionsResult
     * para verificar si se otorgaron los permisos necesarios
     */

    fun comprobarPermisos(requestCode: Int,
                          grantResults: IntArray,
                          permissionListener: PermissionListener) {

        if(requestCode == CODE_PERMISOS){
            var isGranted = true
            grantResults.forEach {
                if(it != PackageManager.PERMISSION_GRANTED){
                    isGranted = false
                    return@forEach
                }
            }
            if(isGranted){
                permissionListener.permissionGranted()
            }else {
                permissionListener.permissionDenied()
            }
        } else {
            permissionListener.permissionDenied()
        }
    }
}