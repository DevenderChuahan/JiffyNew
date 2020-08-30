package `in`.jiffycharge.gopower.view.map

import `in`.jiffycharge.gopower.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.collections.HashMap

class GetNearbyPlacesData: AsyncTask<Any, String, String>() {

    var map: GoogleMap? =null
    var url:String?=null
    var googlePlacesData:String?=null
    var nearbyPlacesList:List<HashMap<String,String>>?=null
    var context:Context?=null
    var markerOptions:MarkerOptions?=null


    @SuppressLint("NewApi")
    override fun doInBackground(vararg params: kotlin.Any): String {
        if (params[0] is GoogleMap)
        {
             map= params[0] as GoogleMap

        }
        if (params[1] is String) {
            url = params[1] as String
        }
        if (params[2] is Context) {
            context = params[2] as Context
        }
        if (params[3] is MarkerOptions) {
            markerOptions = params[3] as MarkerOptions
        }
        try {

            val downloadUrl=DownloadUrl()
            googlePlacesData= downloadUrl.readUrl(url!!)


        }catch (e:Exception)
        {

        }
        return googlePlacesData.toString()









    }

    override fun onPostExecute(result: String?) {
        val dataParser=DataParser()
        nearbyPlacesList=dataParser.parse(result.toString())
        ShowNearbyPlaces(nearbyPlacesList!!)



    }

    private fun ShowNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>>) {
        for (  i in nearbyPlacesList.indices)
        {
            //get custom marker from drawable

            val bitmapDrawable= ContextCompat.getDrawable(context!!, R.drawable.group_11_copy_3)as BitmapDrawable
            val bitmap=bitmapDrawable.bitmap
            Bitmap.createScaledBitmap(bitmap,84,84,false)
            markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(bitmap))


           val googlePlaces= nearbyPlacesList.get(i)
            val lat= googlePlaces.get("lat")?.toDouble()
            val lng=googlePlaces.get("lng")?. toDouble()
            val place=googlePlaces.get("place_name")
            val vicinity=googlePlaces.get("vicinity")
            val lat_lng=LatLng(lat!!,lng!!)
            markerOptions!!.position(lat_lng)
            markerOptions!!.title(place.plus(":").plus(vicinity))
            map!!.addMarker(markerOptions)




//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//            map!!.addMarker(markerOptions.position(LatLng(lat,lng)).title(place))

//      //move camera
//            val camerapos= CameraPosition.Builder()
//                .target(latlng).zoom(12.5F).build()
//
//
//            val camupdate= CameraUpdateFactory.newCameraPosition(camerapos)
//
////            val projection= map!!.projection
////            val markerPosition=markerOptions.position
////            val markerPoint=projection.toScreenLocation(markerPosition)
////            markerPoint.set(markerPoint.x,markerPoint.y-30)
////            map!!.animateCamera(CameraUpdateFactory.newLatLng(map!!.projection.fromScreenLocation(markerPoint)))
//
//            map!!.animateCamera(camupdate)
////            map!!.moveCamera(camupdate)

        }





    }
}