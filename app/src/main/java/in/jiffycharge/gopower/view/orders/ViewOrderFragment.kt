package `in`.jiffycharge.gopower.view.orders


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentOrderBinding
import `in`.jiffycharge.gopower.databinding.FragmentViewOrderBinding
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.Orders_view_model
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_view_order.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class ViewOrderFragment : Fragment(), OnMapReadyCallback {
    private  val order_view_Model by viewModel<Orders_view_model>()
    lateinit var context: Activity

    var order_code: String? =null

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context= requireContext() as Activity

        // Inflate the layout for this fragment
        val bind= DataBindingUtil.inflate<FragmentViewOrderBinding>(inflater,R.layout.fragment_view_order,container,false)
            .apply {
                this.setLifecycleOwner(this@ViewOrderFragment)
                this.orderDetailsVmodel = order_view_Model

            }
        val  mMapview = childFragmentManager.findFragmentById(R.id.Order_map_frg) as SupportMapFragment
        mMapview.getMapAsync(this)

//         Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_view_order, container, false)
        return bind.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vieworder_back.setOnClickListener {
            context.onBackPressed()
        }

//       val bundle= arguments
//        Log.v("Bundle", bundle.toString())
//        if (bundle!=null)
//        {
//            order_code= bundle.getString("order_code")
//            tv_code.text=order_code.toString().trim()
//            Log.v("order_code",order_code)
//        }
//
//        order_view_Model.call_order_details(order_code!!)
    }

    override fun onMapReady(map: GoogleMap?) {

        try {
            val mop= MapStyleOptions.loadRawResourceStyle(context,R.raw.map_style)
            map!!.setMapStyle(mop)
        }catch (e: Resources.NotFoundException)
        {
            e.printStackTrace()
        }

        val bundle= arguments
        Log.v("Bundle", bundle.toString())
        if (bundle!=null)
        {
            order_code= bundle.getString("order_code")
            tv_code.text=order_code.toString().trim()
            Log.v("order_code",order_code)
            order_view_Model.call_order_details(order_code!!)
            order_view_Model.order_repo.response_message.observe(this,
                Observer {
                    if(it.equals("200"))
                    {
                        order_view_Model.order_repo._detailes_list.observe(this, Observer {orderlistmodel->
                            map?.clear()

                            // Changing map type
                            map?.mapType = GoogleMap.MAP_TYPE_NORMAL


                            val markerOptions= MarkerOptions()
                            val begin_lat_lng=LatLng(orderlistmodel.item.beginLocationLat, orderlistmodel.item.beginLocationLon)
                            Log.v("DClat", orderlistmodel.item.beginLocationLat.toString())
                            Log.v("DClon", orderlistmodel.item.beginLocationLat.toString())
                            markerOptions.position(begin_lat_lng)

                            val bitmapDrawable= ContextCompat.getDrawable(context, R.drawable.group_11_copy_3)as BitmapDrawable
                            val bitmap=bitmapDrawable.bitmap
                            Bitmap.createScaledBitmap(bitmap,84,84,false)
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            map?.addMarker(markerOptions)

                            if(!orderlistmodel.item.endLocationLat.equals(0.0) && !orderlistmodel.item.endLocationLon.equals(0.0))
                            {
                                val end_lat_lng=LatLng(orderlistmodel.item.endLocationLat, orderlistmodel.item.endLocationLon)

                                val markerOptions2= MarkerOptions()
                                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                markerOptions2.position(end_lat_lng)

                                map?.addMarker(markerOptions2)

                                val _lat_lon2=LatLng((orderlistmodel.item.beginLocationLat+orderlistmodel.item.endLocationLat)/2,(orderlistmodel.item.beginLocationLon + orderlistmodel.item.endLocationLon)/2)

                                map?.moveCamera(CameraUpdateFactory.newLatLng(_lat_lon2))
                                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(_lat_lon2,13.5f))
                            }else
                            {
                                map?.moveCamera(CameraUpdateFactory.newLatLng(begin_lat_lng))
                                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(begin_lat_lng,13.5f))
                            }

                        })
                    }else
                    {
                        context.toast(it.toString())

                    }


                })

        }







    }


}
