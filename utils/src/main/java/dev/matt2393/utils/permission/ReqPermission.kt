package dev.matt2393.utils.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object ReqPermission {
    private lateinit var regPer: ActivityResultLauncher<Array<String>>
    private lateinit var results: Results
    private lateinit var ac: ComponentActivity
    private lateinit var permissionsArray: Array<String>

    fun init(ac: ComponentActivity) {
        this.ac = ac
        regPer =
            ac.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                results.onPermissionResults(it)
            }
    }

    fun init(fr: Fragment) {
        this.ac = fr.requireActivity()
        regPer =
            fr.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                results.onPermissionResults(it)
            }
    }

    fun launch(
        permission: Array<String>,
        result: (res: Map<String, Boolean>) -> Unit
    ) {
        permissionsArray = permission
        results = object : Results {
            override fun onPermissionResults(res: Map<String, Boolean>) {
                result.invoke(res)
            }
        }
        regPer.launch(permission)
    }

    private interface Results {
        fun onPermissionResults(res: Map<String, Boolean>)
    }
}