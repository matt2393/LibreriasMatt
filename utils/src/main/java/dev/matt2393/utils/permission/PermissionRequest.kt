package dev.matt2393.utils.permission

/*import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.matt2393.utils.aux.FragmentAux
import dev.matt2393.utils.aux.ResultListener

class PermissionRequest {
    private var activity: AppCompatActivity? =null
    private var fragment: Fragment? = null
    private lateinit var fragmentManager: FragmentManager

    companion object {
        fun with(activity: AppCompatActivity): PermissionRequest {
            return PermissionRequest(activity)
        }
        fun with(fragment: Fragment): PermissionRequest {
            return PermissionRequest(fragment)
        }
    }
    private constructor() {}
    private constructor(activity: AppCompatActivity) {
        this.activity = activity
        this.fragment = null
        fragmentManager = activity.supportFragmentManager
    }
    private constructor(fragment: Fragment) {
        this.fragment = fragment
        this.activity = null
        fragmentManager = fragment.childFragmentManager
    }

    private fun getContext() = if(activity==null) { fragment!!.requireContext() } else { activity!! }

    fun request(permissions: Array<String>, result:(permissionsMap: Map<String, Boolean>) -> Unit) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val aux = arrayListOf<String>()
            val map = HashMap<String, Boolean>()
            permissions.forEach {
                map[it] = true
                if(ContextCompat.checkSelfPermission(getContext(), it)
                    != PackageManager.PERMISSION_GRANTED) {
                    map[it] = false
                    aux.add(it)
                }

            }

            if(aux.size>0) {
                val resultListener = object: ResultListener {
                    override fun permissionResult(permissions: Map<String, Boolean>) {
                        result(map)
                    }
                }
                fragmentManager.beginTransaction()
                    .add(
                        FragmentAux.newInstance(
                            permissions, resultListener
                        ), FragmentAux.TAG
                    ).commitNowAllowingStateLoss()
            }else{
                result(map)
            }
        }
        else {
            val map = HashMap<String, Boolean>()
            permissions.forEach {
                map[it] = true
            }
            result(map)
        }
    }
}
*/