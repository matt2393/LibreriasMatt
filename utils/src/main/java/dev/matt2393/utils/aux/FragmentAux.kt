package dev.matt2393.utils.aux

/*import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

internal class FragmentAux: Fragment() {

    companion object {
        val TAG = FragmentAux::class.java.name
        private const val PERMISSION_ARRAY = "PERMISSIONS"
        fun newInstance(permissions: Array<String>,
                        resultLis: ResultListener) = FragmentAux().apply {
            arguments = Bundle().apply {
                putStringArray(PERMISSION_ARRAY, permissions)
            }
            resultListener = resultLis
        }
        fun newInstance(intentSenderReq: IntentSenderRequest,
                        resultLis: ResultListener) = FragmentAux().apply {
            intentSenderRequest = intentSenderReq
            resultListener = resultLis
        }
    }

    private var resultListener: ResultListener? = null

    private var permissions: Array<String> = arrayOf()

    private var intentSenderRequest: IntentSenderRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getStringArray(PERMISSION_ARRAY)?.let { per ->
                permissions = per
            }
        }

        if(resultListener!=null) {
            if (permissions.isNotEmpty()) {
                initRegisterPermission()
            }
            if (intentSenderRequest != null) {
                initRegisterGps()
            }
        }
    }
    private fun initRegisterPermission() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            Log.e("FRAG",it.toString())
            resultListener?.permissionResult(it)
            onDestroy()
        }.launch(permissions)

    }
    private fun initRegisterGps() {
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            resultListener?.gpsResult(it)
            onDestroy()
        }.launch(intentSenderRequest)
    }
}*/