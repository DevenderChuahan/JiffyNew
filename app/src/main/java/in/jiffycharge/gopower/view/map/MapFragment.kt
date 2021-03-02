package `in`.jiffycharge.gopower.view.map


import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentMapBinding
import `in`.jiffycharge.gopower.getsp
import `in`.jiffycharge.gopower.model.ItemXXXXXXXX
import `in`.jiffycharge.gopower.setsp
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.deposit.DepositActivity
import `in`.jiffycharge.gopower.view.feedback.FeedbackActivity
import `in`.jiffycharge.gopower.view.home.HomeActivity
import `in`.jiffycharge.gopower.view.power.SelectPowerActivity
import `in`.jiffycharge.gopower.view.revert.RevertActivity
import `in`.jiffycharge.gopower.view.rezorpay.RazorpayActivity
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
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.beust.klaxon.*
import com.blogspot.atifsoftwares.animatoolib.Animatoo
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
import com.spiddekauga.android.ui.showcase.MaterialShowcase
import com.spiddekauga.android.ui.showcase.MaterialShowcaseSequence
import com.spiddekauga.android.ui.showcase.MaterialShowcaseView
import com.spiddekauga.android.ui.showcase.ShowcaseConfig
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.home_bottom_sheet.*
import kotlinx.android.synthetic.main.power_countdown_bottomsheet_layout.*
import kotlinx.android.synthetic.main.power_countdown_bottomsheet_layout.view.*
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
     var deposit=0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
        const val SCAN_REQUEST_CODE = 8
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

        // Inflate the layout for this fragment
        return bind.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getOrderInfo()
//        home_view_model.repo.fetchUserprofile()

        home_view_model.repo._data.observe(viewLifecycleOwner, Observer {
            (requireActivity()).runOnUiThread {
                when (it.status) {
                    Resourse.Status.SUCCESS -> {
                         deposit = it.data!!.item.deposit



                    }
                    Resourse.Status.ERROR -> {
//                        context.toast(it.exception?:return@runOnUiThread)
                    }
                }
            }

        })

        requireActivity().iv_search.setOnClickListener {
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

        //observer

        MapViewModel.mapRepository.location_data.observe(viewLifecycleOwner, Observer { shop_location_list ->
                context.runOnUiThread {
                    when (shop_location_list.status) {

                        Resourse.Status.SUCCESS -> {

                            val latLngs = mutableListOf<Pair<Double, Double>>()
                            val items = shop_location_list.data?.items
                            Log.v("DCItems","$items")
                            if (!items!!.isNullOrEmpty()) {

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



        //getOrderInfo observer
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
                                context.setsp("popflag",true)


                                orderUsing(apiOrderBO)
                            }
                            40 -> {//unpaid
                                context.setsp("popflag",true)

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
                                context.setsp("popflag",false)


                            }
                            else -> {
                                context.setsp("popflag",true)

                                orderUsing(apiOrderBO)
                            }
                        }


                    }

                    Resourse.Status.ERROR -> {
                        requireActivity().btn_scan.visibility=View.VISIBLE
                        context.setsp("popflag",false)



                    }


                }


            }


        })

        // getPayNot observer

        home_view_model.repo.payNotResult.observe(viewLifecycleOwner, Observer {
            context.runOnUiThread {
                when (it.status) {
//                        Resourse.Status.LOADING -> {
//
//
//                        }
                    Resourse.Status.SUCCESS -> {
                        val apiOrderBO = it.data?.item?:return@runOnUiThread
//                            val apiOrderBO = it.data!!.item
                        requireActivity().btn_scan.visibility = View.GONE
                        context.setsp("popflag",true)

                        showNotPayDialog(apiOrderBO)






                    }
                    Resourse.Status.ERROR -> {
//                            context.toast(it.data!!.error_description)
                        requireActivity().btn_scan.visibility = View.VISIBLE
                        context.setsp("popflag",false)



                    }

                    else ->
                    {

                    }
                }


            }

        })





        //Showcase
        val config=ShowcaseConfig(context)
        config.delay=0

        val sequence =MaterialShowcaseSequence(context,"776")
        sequence.setConfig(config)

        sequence.addSequenceItem(  MaterialShowcaseView.Builder(context)
            .setTarget(context.iv_nav_drawer)
            .setTitleText("Drawer Items")
            .setContentText("Click here to get more drawer items ")
            .setDismissText("Got It")
            .setSingleUse("drawer")
            .build())

        config.delay=500


        sequence.addSequenceItem(  MaterialShowcaseView.Builder(context)
            .setTarget(iv_map_focus)
            .setTitleText("Current Location")
            .setContentText("Click to move to your current location")
            .setDismissText("Got It")
            .setSingleUse("focus")
            .build())

        config.delay=500

        sequence.addSequenceItem(  MaterialShowcaseView.Builder(context)
            .setTarget(btn_scan)
            .setTitleText("Scan Now")
            .setContentText("Click to scan Jiffy QR code ")
            .setDismissText("Got It")
            .setSingleUse("scan")
            .build()
        )


        sequence.show()







    }

    private fun getOrderInfo() {
        try {
            //getOrderInfo
            home_view_model.repo.doingfInfo()

            // getPayNot
            home_view_model.repo.getNotPay()


        } catch (e: Exception) {

        }

    }
    private var isShowUnPayOrderDialog = false

    private fun showNotPayDialog(apiOrderBO: ItemXXXXXXXX) {
//        if (isShowUnPayOrderDialog) {
//            return
//        }
//        isShowUnPayOrderDialog = true
        val dialog = Dialog(context, R.style.WideDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.unpainorder)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)
        val btn = dialog.findViewById(R.id.btn_pay) as Button
        btn.setOnClickListener {
//            isShowUnPayOrderDialog = false

            dialog.dismiss()
            dialog.cancel()

            val intent=Intent(context,RevertActivity::class.java)

            intent.putExtra("apiOrderBO",apiOrderBO)

            intent.flags= Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            Animatoo.animateCard(context)


            dialog.dismiss()
        }
        dialog.show()


    }

    private fun noUsingOrder() {

        if (usingOrderTask != null) {
            usingOrderTask?.cancel()
            usingOrderTimer.cancel()
        }

        power_bottom_sheet.visibility = View.GONE

        requireActivity().btn_scan.visibility = View.VISIBLE
    }


    /**
     * order is in progress
     */
    private fun orderUsing(apiOrderBO: ItemXXXXXXXX) {
//        btn_scan.visibility = View.GONE
//        home_view_model.startUsingOrderTask()
        requireActivity().btn_scan.visibility=View.GONE


        if (usingOrderTask == null) {
            usingOrderTask = object : TimerTask() {
                @SuppressLint("FragmentLiveDataObserve")
                override fun run() {
                    context.runOnUiThread {
//                        home_view_model.repo.doingfInfo()
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
//                                        orderUsingTimeTv.text = it.data.item.rideTime.toString()
//                                            .plus("\n${"Duration(min)"}")
                                        orderUsingTimeTv.text = it.data.item.rideTime.toString()

                                        orderUsingFeeTv.text = it.data.item.price.toString()

//                                        orderUsingFeeTv.text = it.data.item.price.toString()
//                                            .plus("\n${"Fee"}(${it.data.item.currency})")

//                                        (context as HomeActivity).showPopUpWindow()

                                        showPopUpWindow(apiOrderBO)





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

    private fun showPopUpWindow(apiOrderBO: ItemXXXXXXXX) {
        val drawerLayout = requireActivity().drawer_layout
        requireActivity().btn_scan.visibility = View.GONE
        home_bottom_sheet2.visibility = View.GONE

        power_bottom_sheet.visibility = View.VISIBLE
        Animatoo.animateZoom(context)




        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_IDLE && !drawerLayout.isDrawerOpen(
                        requireActivity().nav_view
                    )
                ) {
                    power_bottom_sheet?.visibility = View.VISIBLE?:return
                    requireActivity().btn_scan?.visibility = View.GONE?:return

                } else {
                    power_bottom_sheet.visibility = View.GONE?:return
                    requireActivity().btn_scan?.visibility = View.GONE?:return


                }


            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                power_bottom_sheet.visibility = View.GONE?:return

                requireActivity().btn_scan?.visibility = View.GONE?:return

            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {

            }


        })

       power_bottom_sheet. feedbackTv.setOnClickListener {
            val intent=Intent(requireContext(),FeedbackActivity::class.java)
            intent.putExtra("ordercode",apiOrderBO.orderCode)
            intent.putExtra("borrowsyscode",apiOrderBO.borrowSysCode)
            startActivity(intent)

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: String) {
        if (!event.isNullOrBlank()) {
//            context.toast(event.toString())

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

        setLocationonMap()
//        val intent=Intent(context,RazorpayActivity::class.java)
//        startActivity(intent)


    }

    override fun click_on_Scan() {
        if (camera_permission()) {

            if (deposit!=null) {
                if (deposit <= 0) {
                    val dialog = Dialog(context, R.style.WideDialog)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.map_custom_dialog)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
//                            dialog.setCancelable(true)
                    val btn = dialog.findViewById(R.id.btn_deposit) as Button
                    btn.setOnClickListener {
                        dialog.dismiss()
                        dialog.cancel()

                        val intent = Intent(context, DepositActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    dialog.show()


                } else {

                    (activity as HomeActivity).openScan()


                }
            }



        } else {
            get_camera_permission()

        }


    }

    override fun onResume() {
        val popflag= context.getsp("popflag",false)

        if(popflag as Boolean)
        {
            requireActivity().btn_scan.visibility = View.GONE

        }else{
            requireActivity().btn_scan.visibility = View.VISIBLE

        }
        getOrderInfo()
        //        context.toast("Resume Calling")




        super.onResume()


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

                    showNearestPowerBankStation(lat!!, longitude!!)


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

    private fun showNearestPowerBankStation(latitude: Double, longitude: Double) {
        try {
            MapViewModel.mapRepository.find_near_location_shop_list(latitude, longitude)


        } catch (e: Exception) {
            e.printStackTrace()
        }
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
//        // Scan QR code / bar code
//        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                val content = data.getStringExtra(Constant.CODED_CONTENT)
//                val intent = Intent(context, SelectPowerActivity::class.java)
//                intent.putExtra("qrcode", content)
////                intent.putExtra("qrcode", "0000800")
//                startActivity(intent)
//            }
//        }


        if (requestCode == REQUEST_SELECT_MAP_ADDRESS && resultCode == Activity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    map.clear()
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val name = place.name
                    val address = place.address
                    val latitude = place.latLng?.latitude ?: return
                    val longitude = place.latLng?.longitude ?: return
                    Log.v("DCLL","$latitude $longitude")

                    map.addMarker(MarkerOptions().position(place.latLng!!).title(name))
                    map.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12.5f))

//                    CurrentLocation.latitude=latitude
//                    CurrentLocation.longitude=latitude
                    showNearestPowerBankStation(latitude,longitude)

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





