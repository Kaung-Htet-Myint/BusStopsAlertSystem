package com.example.busstopsforybs78and94.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busstopsforybs78and94.BusStopsVO
import com.example.busstopsforybs78and94.Utils
import com.example.busstopsforybs78and94.adapters.BusStopsAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_bus_stops.*

class BusStopsActivity : AppCompatActivity() {

    companion object {
        var ss: String? = ""
        fun newIntent(context: Context,intent: Intent): Intent {
            ss  = intent.getStringExtra("bus number")
            return Intent(context, BusStopsActivity::class.java)
        }
    }

    //private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var busStopsAdapter: BusStopsAdapter
    lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.busstopsforybs78and94.R.layout.activity_bus_stops)

        tvBusNumberRoute.setText(ss)

        //Instance of  busStopAdpater
        busStopsAdapter = BusStopsAdapter()

        //add recycler view with the adapter
        with(rvBusStopsList){
            rvBusStopsList.setHasFixedSize(true)
            rvBusStopsList.layoutManager = LinearLayoutManager(applicationContext)
            rvBusStopsList.adapter = busStopsAdapter
        }

        utils = Utils()
        val jsonFileString: String?
        if (ss.equals("YBS 78 Route")){
            jsonFileString = utils.getJsonDataFromAsset(applicationContext,"YBS_78.json")
            Log.i("data", jsonFileString)
        }else{
            jsonFileString = utils.getJsonDataFromAsset(applicationContext,"YBS_94.json")
            Log.i("data", jsonFileString)
        }

        val gson = Gson()
        val listBusStopsType = object : TypeToken<List<BusStopsVO>>() {}.type

        val busStops: List<BusStopsVO> = gson.fromJson(jsonFileString, listBusStopsType)
        busStops.forEachIndexed { idx, busStops -> Log.i("data", "> Item $idx:\n$busStops")}

        busStopsListView(busStops)

        btnBack.setOnClickListener {
            navigateToMainActivity()
        }
    }

    fun busStopsListView(busStopsList: List<BusStopsVO>){
        busStopsAdapter.setNewData(busStopsList.toMutableList())
    }

    fun navigateToMainActivity(){
        startActivity(MainActivity.newIntent(this))
        finish()
    }

}