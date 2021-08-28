package dev.matt2393.libreriasmatt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import dev.matt2393.utils.Location.LocationPermission

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textMain = findViewById<TextView>(R.id.textMain)
        //pedirPermisos()
        textMain.setOnClickListener {
            pedirPermisos()
        }
        textMain.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain, MainFragment())
            .commit()
    }

    private fun pedirPermisos() {
        LocationPermission.with(this)
            .request(success = {
                Toast.makeText(this, "Exito", Toast.LENGTH_SHORT).show()
                Log.e("LOCATION", "Exito")
            }, error = {

                Toast.makeText(this, "Error: ${it.name}", Toast.LENGTH_SHORT).show()
                Log.e("LOCATIONError", it.name)
            })
    }
}