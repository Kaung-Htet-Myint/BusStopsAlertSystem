package com.example.busstopsforybs78and94.activities

import android.Manifest
import android.animation.Animator
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busstopsforybs78and94.data.vos.BusStopsVO
import com.example.busstopsforybs78and94.Constants
import com.example.busstopsforybs78and94.R
import com.example.busstopsforybs78and94.Utils
import com.example.busstopsforybs78and94.adapters.BusStopsAdapter
import com.example.busstopsforybs78and94.intentservices.GeofenceBroadcastReceiver
import com.example.busstopsforybs78and94.intentservices.GeofenceTransitionsJobIntentService
import com.example.busstopsforybs78and94.mvp.presenters.BusStopsPresenter
import com.example.busstopsforybs78and94.mvp.views.BusStopsView
import com.example.busstopsforybs78and94.utils.GeofenceUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_bus_stops.*
import java.util.ArrayList

class BusStopsActivity : BaseActivity(), BusStopsView, OnCompleteListener<Void>, OnMapReadyCallback {

    companion object {
        var ss: String? = ""
        fun newIntent(context: Context,intent: Intent): Intent {
            ss  = intent.getStringExtra("bus number")
            return Intent(context, BusStopsActivity::class.java)
        }
    }

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private val TAG = BusStopsActivity::class.java.simpleName
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    //private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var busStopsAdapter: BusStopsAdapter
    lateinit var jsonFileString: String
    lateinit var utils: Utils

    /**
     * Provides access to the Geofencing API.
     */
    private var mGeofencingClient: GeofencingClient? = null

    /**
     * The list of geofences used in this sample.
     */
    lateinit var mGeofenceList: ArrayList<Geofence>

    /**
     * Used when requesting to add or remove geofences.
     */
    private var mGeofencePendingIntent: PendingIntent? = null

    private var mPendingGeofenceTask = PendingGeofenceTask.NONE

    lateinit var busStops : List<BusStopsVO>

    private lateinit var mBusStopsPresenter: BusStopsPresenter

    private lateinit var fragment: Fragment
    //var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stops)

        tvBusNumberRoute.setText(ss)

        mBusStopsPresenter = ViewModelProviders.of(this).get(BusStopsPresenter::class.java)
        mBusStopsPresenter.initPresenter(this)

        //Geofence Building
        mGeofencingClient = LocationServices.getGeofencingClient(this)
        mGeofenceList = ArrayList()

        //Instance of  busStopAdpater
        busStopsAdapter = BusStopsAdapter()

        //add recycler view with the adapter
        with(rvBusStopsList){
            rvBusStopsList.setHasFixedSize(true)
            rvBusStopsList.layoutManager = LinearLayoutManager(applicationContext)
            rvBusStopsList.adapter = busStopsAdapter
        }

        utils = Utils()

       /* if (ss.equals("YBS 78 Route")){
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_78_Up.json")
            Log.i("data", jsonFileString)
        }else{
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_94_Up.json")
            Log.i("data", jsonFileString)
        }
*/

        busStops = parseJsonDataByGson()

        populateGeofenceList()

        busStopsListView(busStops)

        //Geofence Building


        //populateGeofenceList()
        addGeofences()

        setUpListener()
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            addMarker(
                MarkerOptions()
                    .position(LatLng(16.779778, 96.170582))
                    .title("Marker")
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            performPendingGeofenceTask()
        }
    }

    fun parseJsonDataByGson(): List<BusStopsVO>{
        utils = Utils()
        //var jsonFileString: String

        if (ss.equals("YBS 78 Route Up")){
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_78_Up.json")
            //jsonFileString = utils.getJsonDataFromAsset(this,"testing.json")
            Log.i("data", jsonFileString)
        }else if(ss.equals("YBS 78 Route Down")){
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_78_Down.json")
            Log.i("data", jsonFileString)
        }else if(ss.equals("YBS 94 Route Up")){
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_94_Up.json")
            Log.i("data", jsonFileString)
        }else{
            jsonFileString = utils.getJsonDataFromAsset(this,"YBS_94_Down.json")
            Log.i("data", jsonFileString)
        }

        val gson = Gson()
        val listBusStopsType = object : TypeToken<List<BusStopsVO>>() {}.type

        val busStops: List<BusStopsVO> = gson.fromJson(jsonFileString, listBusStopsType)
        busStops.forEachIndexed { idx, busStops -> Log.i("data", "> Item $idx:\n$busStops")}

        return busStops
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(mGeofenceList)
        }.build()
    }

    private fun populateGeofenceList() {

        for (entry in busStops) {

            mGeofenceList.add(
                Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.BusStop_Name)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                        entry.Lat,
                        entry.Long,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    //.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                    // Create the geofence.
                    .build()
            )
        }
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    private fun addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions))
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
       /* mGeofencingClient?.addGeofences(getGeofencingRequest(), geofencePendingIntent)
            ?.addOnCompleteListener(this)*/

        mGeofencingClient?.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(this@BusStopsActivity,"geofences add success", Toast.LENGTH_SHORT).show()
                populateGeofenceList()
            }
            addOnFailureListener {
                Toast.makeText(this@BusStopsActivity,"Failed to add geofences",Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onComplete(task: Task<Void>) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded())

            val messageId = if (getGeofencesAdded())
                R.string.geofences_added
            else
                R.string.geofences_removed
            //Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            val errorMessage = GeofenceUtils.getErrorString(this, taskId)
            Log.w(TAG, errorMessage)
        }
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private fun getGeofencesAdded(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            Constants.GEOFENCES_ADDED_KEY, false
        )
    }

    /**
     * Stores whether geofences were added ore removed in [SharedPreferences];
     *
     * @param added Whether geofences were added or removed.
     */
    private fun updateGeofencesAdded(added: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
            .apply()
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private fun performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences()
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@BusStopsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@BusStopsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun showSnackbar(text: Int, ok: Int, onClickListener: View.OnClickListener) {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(text),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(ok), onClickListener).show()
    }


    /**
     * Shows a [Snackbar] using `text`.
     *
     * @param text The Snackbar text.
     */
    private fun showSnackbar(text: String) {
        val container = findViewById<View>(android.R.id.content)
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show()
        }
    }

    fun busStopsListView(busStopsList: List<BusStopsVO>) {
        busStopsAdapter.setNewData(busStopsList.toMutableList())
    }

    override fun showMap() {
        if (ss.equals("YBS 78 Route")){
            intent.putExtra("bus number","YBS 78 Route")
            startActivity(MapsActivity.newIntent(this,intent))
            finish()
        }else{
            intent.putExtra("bus number","YBS 94 Route")
            startActivity(MapsActivity.newIntent(this,intent))
            finish()
        }
    }


    override fun navigateToMain() {
        startActivity(MainActivity.newIntent(this))
        finish()
    }

    override fun showUpDown() {
        /*if (View.GONE == fabBGLayout.visibility) {
            showFABMenu()
        } else {
            closeFABMenu()
        }
        fabBGLayout.setOnClickListener { closeFABMenu() }*/
    }

    override fun onTapUp() {
        TODO("Not yet implemented")
    }

    override fun onTapDown() {
            utils = Utils()
            //var jsonFileString: String

            if (ss.equals("YBS 78 Route")){
                jsonFileString = utils.getJsonDataFromAsset(this,"YBS_78_Down.json")
                Log.i("data", jsonFileString)
            }else{
                jsonFileString = utils.getJsonDataFromAsset(this,"YBS_94_Down.json")
                Log.i("data", jsonFileString)
            }

        //closeFABMenu()
       /* busStops = parseJsonDataByGson()
        parseJsonDataByGson()
        busStopsListView(busStops)*/
    }

    /*private fun showFABMenu() {
        fabDown.visibility = View.VISIBLE
        fabUp.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        fab.animate().rotationBy(180F)
        fabDown.animate().translationY(-resources.getDimension(R.dimen.standard_75))
        fabUp.animate().translationY(-resources.getDimension(R.dimen.standard_120))

    }*/

   /* private fun closeFABMenu() {
        fabBGLayout.visibility = View.GONE
        fab.animate().rotation(0F)
        fabDown.animate().translationY(0f)
        fabUp.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (View.GONE == fabBGLayout.visibility) {
                        fabDown.visibility = View.GONE
                        fabUp.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })

    }*/

    private fun setUpListener(){
        btnBack.setOnClickListener {
            mBusStopsPresenter.onTapBackBtn()
        }

       /* fab.setOnClickListener {
            mBusStopsPresenter.onTapFab()
        }*/

        btn_map.setOnClickListener {
            mBusStopsPresenter.onTabMapBtn()
        }

        fab1.setOnClickListener {
            //utils = Utils()
            onTapDown()
        }
    }
}