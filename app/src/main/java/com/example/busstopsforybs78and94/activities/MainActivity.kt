package com.example.busstopsforybs78and94.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.busstopsforybs78and94.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
    var busRouteNumber = arrayOf("choose bus","YBS 78 Up", "YBS 78 Down", "YBS 94 Up", "YBS 94 Down")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*btnBus78Up.setOnClickListener {
            navigateToBus78Route()
        }

        btnBus94Up.setOnClickListener {
            navigateToBus94Route()
        }*/

        var routeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, busRouteNumber)

        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(spinner){
            adapter = routeAdapter
            setSelection(0,false)
            onItemSelectedListener = this@MainActivity
            prompt = "Select your bus route"
            gravity = Gravity.CENTER
        }

    }


    fun navigateToBus78RouteUp(){

        intent.putExtra("bus number","YBS 78 Route Up")

        startActivity(BusStopsActivity.newIntent(this,intent))
        finish()
    }

    fun navigateToBus78RouteDown(){

        intent.putExtra("bus number","YBS 78 Route Down")

        startActivity(BusStopsActivity.newIntent(this,intent))
        finish()
    }

    fun navigateToBus94RouteUp(){

        intent.putExtra("bus number","YBS 94 Route Up")

        startActivity(BusStopsActivity.newIntent(this,intent))
        finish()
    }

    fun navigateToBus94RouteDown(){
        intent.putExtra("bus number","YBS 94 Route Down")

        startActivity(BusStopsActivity.newIntent(this,intent))
        finish()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        if (spinner.selectedItemPosition.equals(1)){
            navigateToBus78RouteUp()
        }else if (spinner.selectedItemPosition.equals(2)){
            navigateToBus78RouteDown()
        }else if (spinner.selectedItemPosition.equals(3)){
            navigateToBus94RouteUp()
        }else{
            navigateToBus94RouteDown()
        }

    }

}
