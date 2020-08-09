package com.example.busstopsforybs78and94.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.busstopsforybs78and94.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val routes = resources.getStringArray(R.array.Route)

        val spinner = findViewById<Spinner>(R.id.spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, routes)
            spinner.adapter = adapter

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {

                    Toast.makeText(this@MainActivity,
                        getString(R.string.selected_route) + " " +
                                "" + routes[position], Toast.LENGTH_SHORT).show()

                    /*if(parent.getItemAtPosition(1).equals("YBS 94")){
                        ybs94 = "YBS 94 Route"

                    }*/
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        btn_ok.setOnClickListener {
            navigateToBusNumberRouteActivity()

        }

    }

    fun navigateToBusNumberRouteActivity(){
        val intent = Intent(this,MainActivity::class.java)
        if (spinner.selectedItemPosition.equals(1)){
            intent.putExtra("bus number","YBS 94 Route")
        }else{
            intent.putExtra("bus number","YBS 78 Route")
        }
        startActivity(BusStopsActivity.newIntent(this,intent))
        finish()
    }

}
