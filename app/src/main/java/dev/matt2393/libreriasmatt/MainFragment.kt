package dev.matt2393.libreriasmatt

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.matt2393.libreriasmatt.databinding.FragmentMainBinding
import dev.matt2393.utils.location.LocPermission
import dev.matt2393.utils.permission.ReqPermission

//import dev.matt2393.utils.permission.PermissionRequest

class MainFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =  FragmentMainBinding.inflate(inflater, container, false)

        ReqPermission.init(this)
        LocPermission.init(this)

        binding.buttonLoc.setOnClickListener {
            LocPermission.launch(
                success = {
                    Log.e("ResultLoc", "Success")
                },
                error = {
                    when(it) {
                        LocPermission.ErrorType.PERMISSION -> TODO()
                        LocPermission.ErrorType.GPS -> TODO()
                    }
                    Log.e("ResultLocError", it.toString())
                }
            )
        }

        binding.buttonPermisos.setOnClickListener {

            ReqPermission.launch(
                permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                result =  {
                    Log.e("Result", it.toString())
                }
            )
            /*LocationPermission.with(this)
                .request(success = {
                    Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show()
                    Log.e("LOCATION", "Exito")
                }, error = {
                    Toast.makeText(requireContext(), "Error: ${it.name}", Toast.LENGTH_SHORT).show()
                    Log.e("LOCATIONError", it.name)
                })*/
            /*PermissionRequest.with(this)
                .request(arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)) {
                    // Map <String, Boolean>
                    // String -> nombre del permiso
                    // Boolean -> si se aprobo el permiso con el nombre
                    Log.e("PERMISSION", it.toString())
                }*/

        }

        return binding.root
    }
}