package `in`.jiffycharge.gopower.view.map


import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentMapBinding
import `in`.jiffycharge.gopower.model.ItemXXXXXXXX
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.deposit.DepositActivity
import `in`.jiffycharge.gopower.view.power.SelectPowerActivity
import `in`.jiffycharge.gopower.view.revert.RevertActivity
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import `in`.jiffycharge.gopower.viewmodel.MapFragmentViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.beust.klaxon.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.home_bottom_sheet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MapFragment() : Fragment(), OnMapReadyCallback, MapInterface {
    private val MapViewModel by viewModel<MapFragmentViewModel>()
    val home_view_model by viewModel<HomeActivityViewModel>()


    private val usingOrderTimer by lazy { Timer() }
    private var usingOrderTask: TimerTask? = null
    val USING_ORDER_TIME = 10 * 1000L

    private lateinit var map: GoogleMap
    lateinit var context: Activity
    lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat: Double? = null
    var longitude: Double? = null
    private lateinit var locationCallback: LocationCallback
    val nearbyPlace: String = "School"
    val PROXIMITY_RADIUS = 5000
    var mOrigin: LatLng? = null
    var mDestination: LatLng? = null
    lateinit var mPolyline: Polyline
    private lateinit var CurrentLocation: Location
    val markoptions = MarkerOptions()
    var autocompleteFragment: AutocompleteSupportFragment? = null


    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private val REQUEST_LOCATION_PERMISSION = 101
    val points = ArrayList<LatLng>()
    val lines = ArrayList<Polyline>()
    val TAG = MapFragment::class.java.simpleName

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
        private const val SCAN_REQUEST_CODE = 8
        private const val CAMERA_REQUEST_CODE = 8001
        private const val REQUEST_SELECT_MAP_ADDRESS = 101


    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireContext() as Activity
//check weather google api service available or not
        CheckGooglePlayServices()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                CurrentLocation = p0.lastLocation
//                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

//        fetchLocation()
        createLocationRequest()

//        val viewmodel=ViewModelProvider(this).get(MapFragmentViewModel::class.java)

        val bind = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater,
            R.layout.fragment_map,
            container,
            false
        )
            .apply {
                this.setLifecycleOwner(this@MapFragment)
                this.mapviewmodel = MapViewModel

            }
        MapViewModel.map_interface = this

//        if (!isPermissionGranted())
//        {
//            requestpermission()
//        }
        val mMapview = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mMapview.getMapAsync(this)


//        //search location in google map
//         autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
//        autocompleteFragment!!.setPlaceFields(arrayListOf(Place.Field.ID,Place.Field.LAT_LNG))
//        autocompleteFragment!!.view?.setBackgroundColor(Color.WHITE)
//
//
//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener
//        {
//            override fun onPlaceSelected(place: Place) {
//                val place_name=place.name
//                val place_id=place.id
//                map.clear()
//                map.addMarker(MarkerOptions().position(place.latLng!!).title(place_name))
//                //find nearBy Places
//                val url:String=getUrl(place.latLng!!.latitude, place.latLng!!.longitude,nearbyPlace)
//
//
//                val getNearbyPlacesData= GetNearbyPlacesData()
//                getNearbyPlacesData.execute(map,url,context,markoptions)
//
//
//                map.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng,12.5f))
//
//
//            }
//
//            override fun onError(status: Status) {
//
//            }
//
//
//        })

//        context.setsp("token","aa343cd7-147f-4a24-bb08-8830aa136703")


//        startShareText("Hello Dc !! WE are from Jiffy !!")


        // Inflate the layout for this fragment
        return bind.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getOrderInfo()
//        showPopUpWindow()


        iv_search.setOnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(context, getString(R.string.google_maps_key))
                val placesClient = Places.createClient(context)

            }
            try {

                val fields = mutableListOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(context)
                startActivityForResult(intent, REQUEST_SELECT_MAP_ADDRESS)


            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            }


        }


    }

    private fun getOrderInfo() {
        try {
            //getOrderInfo
            home_view_model.repo.doingfInfo()
            home_view_model.repo.doingInfoResult.observe(viewLifecycleOwner, Observer {
                context.runOnUiThread {
                    when (it.status) {
                        Resourse.Status.LOADING -> {


                        }
                        Resourse.Status.SUCCESS -> {
                            val apiOrderBO = it.data?.item?:return@runOnUiThread

//10 creating, 20 creating failed, 30 in progress, 40 unpaid, 41 pending refund, normal process, 42 pending refund, unlocking failed, 50 paid
                            when (apiOrderBO?.status) {
                                10 -> {//creating

                                }
                                20 -> {//creation failed
                                }
                                30 -> {//processing
                                    orderUsing(apiOrderBO)
                                }
                                40 -> {//unpaid
                                    showNotPayDialog(apiOrderBO)
                                }
                                41, 42 -> {//Pending refund, normal process
//                                showRefundDialog(apiOrderBO)
                                }
//                    42 -> {//pending refund unlocking failed
//
//                    }
                                50 -> {//paid

                                }
                                null -> {
                                    noUsingOrder()
                                }
                                else -> {
                                    orderUsing(apiOrderBO)
                                }
                            }


                        }

                        Resourse.Status.ERROR -> {
                            requireActivity().btn_scan.visibility=View.VISIBLE


                        }


                    }


                }


            })

            // getPayNot
            home_view_model.repo.getNotPay()

            home_view_model.repo.payNotResult.observe(viewLifecycleOwner, Observer {
                context.runOnUiThread {
                    when (it.status) {
                        Resourse.Status.LOADING -> {


                        }
                        Resourse.Status.SUCCESS -> {
                            val apiOrderBO = it.data?.item?:return@runOnUiThread
//                            val apiOrderBO = it.data!!.item
                            showNotPayDialog(apiOrderBO)






                        }
                        Resourse.Status.ERROR -> {
//                            context.toast(it.data!!.error_description)


                        }

                    }


                }

            })

        } catch (e: Exception) {

        }

    }

    private fun showNotPayDialog(apiOrderBO: ItemXXXXXXXX) {
        val dialog = Dialog(context, R.style.WideDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.unpainorder)
//                            dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val btn = dialog.findViewById(R.id.btn_pay) as Button
        btn.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()

            val intent=Intent(context,RevertActivity::class.java)

            intent.putExtra("apiOrderBO",apiOrderBO)

            intent.flags= Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)

            dialog.dismiss()
        }
        dialog.show()


    }

    private fun noUsingOrder() {

        if (usingOrderTask != null) {
            usingOrderTask?.cancel()
            usingOrderTimer.cancel()
        }

        ll_pop?.visibility = View.GONE
        requireActivity().btn_scan.visibility = View.VISIBLE
    }


    /**
     * order is in progress
     */
    private fun orderUsing(apiOrderBO: ItemXXXXXXXX) {
        btn_scan.visibility = View.GONE
//        home_view_model.startUsingOrderTask()

        if (usingOrderTask == null) {
            usingOrderTask = object : TimerTask() {
                @SuppressLint("FragmentLiveDataObserve")
                override fun run() {
                    context.runOnUiThread {
                        home_view_model.repo.doingfInfo()
                        home_view_model.repo.doingInfoResult.observe(this@MapFragment, Observer {
                            context.runOnUiThread {
                                when (it.status) {
                                    Resourse.Status.SUCCESS -> {

                                        orderNumberTv.text = it.data!!.item.orderCode
                                        orderRateTv.text = Html.fromHtml(
                                            it.data.item.chargeRuleDesc.replace(
                                                "<s>",
                                                "<font color=\"#FF4200\">"
                                            ).replace("</s>", "</font>")
                                        )
                                        orderUsingTimeTv.text = it.data.item.rideTime.toString()
                                            .plus("\n${"Duration(min)"}")
                                        orderUsingFeeTv.text = it.data.item.price.toString()
                                            .plus("\n${"Fee"}(${it.data.item.currency})")
                                        showPopUpWindow()

                                    }
                                    Resourse.Status.ERROR -> {


                                    }


                                }

                            }


                        })

                    }

                }
            }
            val time = USING_ORDER_TIME
            usingOrderTimer.schedule(usingOrderTask, time, time)
        }


//        if (homeUsingPopupWindow == null) {
//            homeUsingPopupWindow?.dismiss()
//            homeUsingPopupWindow = HomeUsingPopupWindow(this, apiOrderBO)
//            if (!drawerLayout.isDrawerOpen(navigationView)) {
//                homeUsingPopupWindow?.showAsDropDown(toolbar)
//            }
//        } else {
//            homeUsingPopupWindow?.refresh(apiOrderBO)
//        }
    }

    private fun showPopUpWindow() {
        val drawerLayout = requireActivity().drawer_layout
        requireActivity().btn_scan.visibility = View.GONE


        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_IDLE && !drawerLayout.isDrawerOpen(
                        requireActivity().nav_view
                    )
                ) {
                    ll_pop.visibility = View.VISIBLE
                    requireActivity().btn_scan.visibility = View.GONE


                } else {
                    ll_pop.visibility = View.GONE
                    requireActivity().btn_scan.visibility = View.GONE


                }


            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                ll_pop.visibility = View.GONE
                requireActivity().btn_scan.visibility = View.GONE

            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {

            }


        })


        if (requireActivity().drawer_layout.isDrawerOpen(GravityCompat.START)) {
            ll_pop.visibility = View.GONE
        } else {
            ll_pop.visibility = View.VISIBLE

        }

//        // Initialize a new layout inflater instance
//        val inflater:LayoutInflater = requireActivity().layoutInflater
//
//        // Inflate a custom view using layout inflater
//        val view = inflater.inflate(R.layout.orderpopupwindow,null)
//
//        // Initialize a new instance of popup window
//        val popupWindow = PopupWindow(
//            view, // Custom view to show in popup window
//            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
//            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
//        )
//
//        // Set an elevation for the popup window
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            popupWindow.elevation = 10.0F
//        }
//
//
//        // If API level 23 or higher then execute the code
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            // Create a new slide animation for popup window enter transition
//            val slideIn = Slide()
//            slideIn.slideEdge = Gravity.TOP
//            popupWindow.enterTransition = slideIn
//
//            // Slide animation for popup window exit transition
//            val slideOut = Slide()
//            slideOut.slideEdge = Gravity.RIGHT
//            popupWindow.exitTransition = slideOut
//
//        }
//
//
//        // Get the widgets reference from custom view
////        val tv = view.findViewById<TextView>(R.id.text_view)
////        val buttonPopup = view.findViewById<Button>(R.id.button_popup)
////
////        // Set click listener for popup window's text view
////        tv.setOnClickListener{
////            // Change the text color of popup window's text view
////            tv.setTextColor(Color.RED)
////        }
////
////        // Set a click listener for popup's button widget
////        buttonPopup.setOnClickListener{
////            // Dismiss the popup window
////            popupWindow.dismiss()
////        }
//
//        // Set a dismiss listener for popup window
//        popupWindow.setOnDismissListener {
////            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
//        }
//
//
//        // Finally, show the popup window on app
//
// val root:FrameLayout=requireActivity().findViewById(R.id.frg) as FrameLayout
//        TransitionManager.beginDelayedTransition(root)
//        context.runOnUiThread {
//            popupWindow.showAtLocation(
//                (root), // Location to display popup window
//                Gravity.TOP, // Exact position of layout to display popup
//                0, // X offset
//                0 // Y offset
//            )
//        }
//


    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: String) {
        if (!event.isNullOrBlank()) {
//            context.toast(event.toString())

        }


    }


    private fun startShareText(text: String) {
        val toNumber = 917018451823

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_VIEW
        sendIntent.data = (Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text"))
        context.startActivity(sendIntent)


    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestpermission()

            return
        } else {
            val tsk = fusedLocationClient.lastLocation

            tsk.addOnSuccessListener {
                if (it != null) {
                    CurrentLocation = it

                    val mMapview =
                        childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
                    mMapview.getMapAsync(this)


                }


            }
        }

    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        try {
            val mop = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            map.setMapStyle(mop)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Style parsing failed")
        }


        setLocationonMap()

        map.setOnMarkerClickListener { mrk ->
            map.clear()
//            val bottom=BottomSheetBehavior.from(home_bottom_sheet2)
//            bottom.peekHeight = 70

            val lat = mrk!!.position.latitude
            val longitude = mrk.position.longitude
            mDestination = LatLng(lat, longitude)

            ll_navigation.setOnClickListener {

                val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$longitude&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }





            if (isPermissionGranted()) {
                if (mOrigin != null && mDestination != null) {

                    drawRoute(mrk)
                }
            } else {
                requestpermission()
            }
            true
        }


    }

    private fun drawRoute(mrk: Marker) {
        val mpolylineOption: PolylineOptions = PolylineOptions()
        mpolylineOption.color(ContextCompat.getColor(context, R.color.colorPrimary))
        mpolylineOption.width(16f)

//        moveMap()


        // build URL to call API
        val url = getURL(mOrigin, mDestination)

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()

            try {
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                val legsarray = routes["legs"][0]
                val distance = (legsarray as JsonArray<*>).get("distance").get("text").get(0)
                val duration = (legsarray as JsonArray<*>).get("duration").get("text").get(0)
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap {
                    decodePoly2(it.obj("polyline")?.string("points")!!)

                }
                uiThread {

                    // Add  points to polyline and bounds
                    mpolylineOption.add(mOrigin)
                    val LatLongB = LatLngBounds.Builder()
                    LatLongB.include(mOrigin)
                    for (point in polypts) {
                        mpolylineOption.add(point)
                        LatLongB.include(point)
                    }
                    mpolylineOption.add(mDestination)
                    LatLongB.include(mDestination)
                    // build bounds
                    val bounds = LatLongB.build()
                    // add polyline to the map
                    map.addPolyline(mpolylineOption)
                    // show map with route centered
//                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                    val tittle = mrk.title
                    markoptions.title(tittle)

//                marop.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    val marker = map.addMarker(
                        markoptions.position(
                            LatLng(
                                mrk.position.latitude,
                                mrk.position.longitude
                            )
                        ).title("Distance $distance")
                    )
                    marker.showInfoWindow()
                    tv_location_name.text = tittle
                    home_bottom_sheet2.visibility = View.VISIBLE

//               mPolyline=map.addPolyline(mpolylineOption)
//                mPolyline.remove()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


//        val downloadTask = DownloadTask()
////
////        // Start downloading json data from Google Directions API
//        downloadTask.execute(map,url)


    }

    private fun getURL(from: LatLng?, to: LatLng?): String {
        val origin = "origin=" + from!!.latitude + "," + from.longitude
        val dest = "destination=" + to!!.latitude + "," + to.longitude
        val key = "key=" + getString(R.string.google_maps_key)

        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service

        val parameters = "$origin&$dest&$key&$sensor&$mode"

        // Output format

        // Output format
        val output = "json"

        // Building the url to the web service

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"


//        val params = "$origin&$dest&$sensor"
//        return "https://maps.googleapis.com/maps/api/directions/json?$params"


    }

    private fun decodePoly2(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly

    }

    private fun getDirectionsUrl(origin: LatLng?, dest: LatLng?): String {
        // Origin of route

        // Origin of route
        val str_origin =
            "origin=" + origin?.latitude.toString() + "," + origin?.longitude


        // Destination of route
        val str_dest =
            "destination=" + dest?.latitude.toString() + "," + dest?.longitude

        // Key

        // Key
        val key = "key=" + getString(R.string.google_maps_key)

//        // Building the parameters to the web service
//
//        // Building the parameters to the web service
//        val parameters = "$str_origin&amp;$str_dest&amp;$key"
//
//        // Output format
//
//        // Output format
//        val output = "json"
//
//        // Building the url to the web service
//
//        // Building the url to the web service
//
//        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"


        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service

        val parameters = "$str_origin&$str_dest&$key&$sensor&$mode"

        // Output format

        // Output format
        val output = "json"

        // Building the url to the web service

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"


    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(context.application)
        val task = client.checkLocationSettings(builder.build())


//         5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
//            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        context,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }


    private fun CheckGooglePlayServices(): Boolean {
        val googleapi = GoogleApiAvailability.getInstance()
        val result = googleapi.isGooglePlayServicesAvailable(context)
        if (result != ConnectionResult.SUCCESS) {
            if (googleapi.isUserResolvableError(result)) {
                googleapi.getErrorDialog(context, result, 0)
            }
            return false
        }
        return true


    }

    override fun locate_to_location() {

//        moveMap()
        home_bottom_sheet2.visibility = View.GONE

        setLocationonMap()


    }

    override fun click_on_Scan() {
//        val scanner= IntentIntegrator(context)
//        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
//        scanner.setBeepEnabled(true)
//        scanner.setCameraId(0)
////        scanner.addExtra("Dc","SCAN SC")
//
//        scanner.initiateScan()
        /////////
//
//        val intentScan = Intent(context, CaptureActivity::class.java)
//
//        intentScan.action = "com.google.zxing.client.android.SCAN"
//        intentScan.addCategory(Intent.CATEGORY_DEFAULT)
//
//        intentScan.putExtra(
//            "SCAN_CAMERA_ID",
//            0
//        ) // Which camera would you prefer? 1 is for front-camera and 0 is for rear-camera.
//
//        intentScan.putExtra(
//            "SHOW_FLIP_CAMERA_BUTTON",
//            true
//        ) // true or false, whether you want to show flip camera button or not.
//
//        intentScan.putExtra(
//            "SHOW_TORCH_BUTTON",
//            true
//        ) // true or false, whether you want to show torch button or not.
//
//        intentScan.putExtra(
//            "TORCH_ON",
//            false
//        ) // true or false, whether you want flash torch ON by default.
//
//        intentScan.putExtra(
//            "BEEP_ON_SCAN",
//            true
//        ) //true or false, whether you want a beep sound on successful scan.
//
//        intentScan.putExtra("PROMPT_MESSAGE",
//            "Scan Jiffy Code "
//        ) // a text you want to show to user on scan screen.
//
//
//        startActivityForResult(intentScan, 1)

        //////////////////

//        startActivity(Intent(context,ScanActivity::class.java))


        if (camera_permission()) {
            home_view_model.repo.fetchUserprofile()
            home_view_model.repo._data.observe(viewLifecycleOwner, Observer {
                (requireActivity()).runOnUiThread {
                    when (it.status) {
                        Resourse.Status.SUCCESS -> {
                            val deposit = it.data!!.item.deposit
                            if (deposit <= 0) {
                                val dialog = Dialog(context, R.style.WideDialog)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(R.layout.map_custom_dialog)
//                            dialog.setCancelable(true)
                                val btn = dialog.findViewById(R.id.btn_deposit) as Button
                                btn.setOnClickListener {
                                    dialog.dismiss()
                                    dialog.cancel()

                                    val intent = Intent(context, DepositActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    dialog.dismiss()
                                }
                                dialog.show()


                            } else {
                                val intent = Intent(context, CaptureActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                val config = ZxingConfig()
                                config.reactColor = R.color.colorAccent
                                config.isShowAlbum = false
                                config.isShowFlashLight = false
                                config.scanLineColor = R.color.colorAccent
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
                                startActivityForResult(intent, SCAN_REQUEST_CODE)
                            }

                        }
                        Resourse.Status.ERROR -> {

                            context.toast(it.data?.error_description.toString())
                        }


                    }


                }

            })


        } else {
            get_camera_permission()

        }


    }

    override fun onResume() {
        super.onResume()
        getOrderInfo()


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setLocationonMap()

        }


        if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            click_on_Scan()

        } else {
//            context.toast("You need to allow access permissions")

        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


    }

    fun camera_permission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

    }


    private fun get_camera_permission() {
        requestPermissions(
            arrayOf<String>(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            CAMERA_REQUEST_CODE
        )

    }


    private fun setLocationonMap() {

        if (isPermissionGranted()) {
            map.clear()


            // Changing map type
            map.mapType = GoogleMap.MAP_TYPE_NORMAL


            // Enable / Disable zooming controls
//                map.uiSettings.isZoomControlsEnabled=true

            // Enable / Disable my location button
            map.uiSettings.isMyLocationButtonEnabled = false

            // Enable / Disable Compass icon
            map.uiSettings.isCompassEnabled = true

            // Enable / Disable Rotate gesture
            map.uiSettings.isRotateGesturesEnabled = true

            // Enable / Disable zooming functionality
            map.uiSettings.isZoomGesturesEnabled = true
            // Showing / hiding your current location
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestpermission()
            }
            map.isMyLocationEnabled = true


            fusedLocationClient.lastLocation.addOnSuccessListener {

                if (it != null) {
                    CurrentLocation = it

                    lat = it.latitude
                    longitude = it.longitude
                    mOrigin = LatLng(lat!!, longitude!!)



                    moveMap()

                }


            }


        } else {

            requestpermission()
        }


    }


    private fun moveMap() {


        //Creating a LatLng Object to store Coordinates

        val latLng = LatLng(CurrentLocation.latitude, CurrentLocation.longitude)
        //Adding marker to map
//        val marker = MarkerOptions()
//
//        //get custom marker from drawable
//        val bitmapdraw=ContextCompat.getDrawable(this,R.drawable.pic)as BitmapDrawable
//       val bimap=bitmapdraw.bitmap
//        Bitmap.createScaledBitmap(bimap,84,84,false)
//
//        //        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//
//        marker.icon(BitmapDescriptorFactory.fromBitmap(bimap))


//        map.addMarker(marker.position(latLng!!).title("DC HERE"))
        //Moving the camera
//        val titleStr = getAddress(latLng)  // add these two lines
//        marker.title(titleStr)

//        var camerapos = CameraPosition.Builder()
//            .target(latLng).zoom(12.5F).build()
//        var camupdate = CameraUpdateFactory.newCameraPosition(camerapos)
//        map.animateCamera(camupdate)


//        //find nearBy Places


//        val url:String=getUrl(lat,longitude,nearbyPlace)
//
//
//        val getNearbyPlacesData= GetNearbyPlacesData()
//        getNearbyPlacesData.execute(map,url,context,markoptions)


        try {
            MapViewModel.mapRepository.find_near_location_shop_list(
                CurrentLocation.latitude,
                CurrentLocation.longitude
            )

            MapViewModel.mapRepository.location_data.observe(
                viewLifecycleOwner,
                Observer { shop_location_list ->
                    context.runOnUiThread {
                        when (shop_location_list.status) {

                            Resourse.Status.SUCCESS -> {

                                val latLngs = mutableListOf<Pair<Double, Double>>()
                                val items = shop_location_list.data?.items
                                if (!items!!.isNotEmpty()) {

                                    items.forEach {
                                        latLngs.add(Pair(it.locationLat, it.locationLon))
                                    }

                                    for (i in items.indices) {
                                        val bitmapDrawable = ContextCompat.getDrawable(
                                            context,
                                            R.drawable.group_11_copy_3
                                        ) as BitmapDrawable
                                        val bitmap = bitmapDrawable.bitmap
                                        Bitmap.createScaledBitmap(bitmap, 84, 84, false)
                                        val markerOptions = MarkerOptions()
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))


                                        val lat_lng = LatLng(
                                            items.get(i).locationLat,
                                            items.get(i).locationLon
                                        )
                                        markerOptions.position(lat_lng)
                                        map.addMarker(markerOptions)

                                    }


                                }

                            }
                            Resourse.Status.ERROR -> {

                                context.toast(shop_location_list.data?.error_description.toString())


                            }


                        }

                    }


                })

        } catch (e: Exception) {
            e.printStackTrace()
        }


        //move camera
        val camerapos = CameraPosition.Builder()
            .target(latLng).zoom(14F).build()


        val camupdate = CameraUpdateFactory.newCameraPosition(camerapos)
        val markerOptions = MarkerOptions()
//        //get custom marker from drawable
//        val bitmapDrawable=ContextCompat.getDrawable(context,R.drawable.group_11_copy_3)as BitmapDrawable
//       val bitmap=bitmapDrawable.bitmap
//        Bitmap.createScaledBitmap(bitmap,84,84,false)
//        marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap))

//
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//            map.addMarker(markerOptions.position(latLng!!).title("DC HERE"))
        map.moveCamera(camupdate)
//        map.animateCamera(camupdate)


    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }


    private fun getUrl(latitude: Double?, longitude: Double?, nearbyPlace: String): String {
        var googlePlacesUrl: StringBuilder? = null
        try {
            googlePlacesUrl =
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
            googlePlacesUrl.append("location=".plus(latitude).plus(",").plus(longitude))
            googlePlacesUrl.append("&radius=".plus(PROXIMITY_RADIUS))
            googlePlacesUrl.append("&type=".plus(nearbyPlace))
            googlePlacesUrl.append("&sensor=true")
            googlePlacesUrl.append("&key=".plus(getString(R.string.google_maps_key)))


        } catch (e: Exception) {
            e.printStackTrace()
        }



        return (googlePlacesUrl.toString())

    }


    private fun isPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

    }

    private fun requestpermission() {
        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )

    }


    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Scan QR code / bar code
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val content = data.getStringExtra(Constant.CODED_CONTENT)
//                context.toast(content.toString())
//                SelectPowerActivity.start(this, content)

                val intent = Intent(context, SelectPowerActivity::class.java)
                intent.putExtra("qrcode", content)
                startActivity(intent)
            }
        }


        if (requestCode == REQUEST_SELECT_MAP_ADDRESS && resultCode == Activity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val name = place.name
                    val address = place.address
                    val latitude = place.latLng?.latitude ?: return
                    val longitude = place.latLng?.longitude ?: return

                    map.addMarker(MarkerOptions().position(place.latLng!!).title(name))
                    map.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12.5f))
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    context.toast(status.statusMessage.toString())
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }


            }


        }


//        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//        if (result != null) {
//            // If QRCode has no data.
//            if (result?.contents != null) {
//                context.toast(result?.contents!!)
//                Log.v("DCscan", result.contents)
//            }else
//            {
//                Log.v("DCscan null data", result.contents)
//
//            }
//        } else {
//            context.toast(result?.contents!!)
//            Log.v("DCscan scan  result null ", result.contents)
//
//
//
//    }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val resultString: String = data?.getStringExtra("SCAN_RESULT")!!
                context.toast(resultString.toString())
            } else
                if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the scan by pressing back button.
                }
        }






        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
//                startLocationUpdates()
            }
        }

    }
}





