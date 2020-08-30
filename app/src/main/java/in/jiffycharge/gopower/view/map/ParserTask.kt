package `in`.jiffycharge.gopower.view.map

import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ParserTask:AsyncTask<Any,Int,List<List<LatLng>>>() {
    var map: GoogleMap? =null
    var url:String?=null
    var routes= ArrayList<List<LatLng>>()

    override fun doInBackground(vararg params: Any?): List<List<LatLng>> {

        if (params[0] is GoogleMap)
        {
            map= params[0] as GoogleMap

        }
        if (params[1] is String) {
            val jsonObject= JSONObject(params[1].toString())
            val dataparser= Dparse()
            routes= dataparser.parse(jsonObject)!!
        }
        return  routes

    }

    override fun onPostExecute(result: List<List<LatLng>>?) {
        var points: ArrayList<LatLng?>
        val lineOptions: PolylineOptions? = null


    }

}

class Dparse {

    fun parse(jObject: JSONObject): ArrayList<List<LatLng>>? {
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        var jRoutes:JSONArray?=null
        var jLegs:JSONArray?=null
        var jSteps:JSONArray?=null
        var path: ArrayList<List<LatLng>>? =null
        try {
            jRoutes=jObject.getJSONArray("routes")
            /** Traversing all routes */
            for (i in 0..jRoutes.length())
            {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
//                val path:ArrayList<Any> = ArrayList()
                 path = ArrayList()

                /** Traversing all legs */
                for (j in 0..jLegs.length())
                {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    /** Traversing all steps */
                    for (k in 0..jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps.get(k) as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        /** Traversing all points */
                        for (l in 0..list.size) {
//                            val hm: HashMap<String, String> = HashMap()
//                            hm.put("lat", list.get(l).latitude.toDouble().toString())
//                            hm.put("lng", list.get(l).longitude.toDouble().toString())
                            path.add(listOf(LatLng(list.get(l).latitude,list.get(l).longitude)))
                        }

                    }
//                    routes.add(path)



                }







            }



        }catch (e:JSONException)
        {

        }






return path


    }






}

    private fun decodePoly(encoded: String): MutableList<LatLng> {


        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len: Int = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = (encoded.get(index++) - 63).toInt()
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = (encoded.get(index++) - 63).toInt()
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


