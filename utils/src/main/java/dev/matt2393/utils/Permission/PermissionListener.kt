package dev.matt2393.utils.Permission

interface PermissionListener {
    fun permissionGranted()
    fun permissionDenied()
}