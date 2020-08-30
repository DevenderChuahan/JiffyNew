package `in`.jiffycharge.gopower.view.map

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DataParser {

    fun parse(jsonData: String):List<HashMap<String,String>>
    {
        var jsonArray:JSONArray?=null
//        var jsonObject:JSONObject
        try {
           val jsonObject= JSONObject(jsonData)
            jsonArray=jsonObject.getJSONArray("results")

        }catch (e:JSONException)
        {
            e.printStackTrace()
        }
        return getPlaces(jsonArray)


    }

    private fun getPlaces(jsonArray: JSONArray?): List<HashMap<String, String>> {
        val placesList = ArrayList<HashMap<String, String>>()

        try {

            val placesCount = jsonArray!!.length()
            var placeMap: HashMap<String, String>? = null
            for (i in 0..placesCount - 1) {
                    placeMap = getPlaces(jsonArray.get(i) as JSONObject?)
                    placesList.add(placeMap!!)

            }
        }catch (e:Exception)
        {

        }


        return placesList

    }

    private fun getPlaces(googlePlaceJson: JSONObject?): HashMap<String, String>? {
val googlePlaceMap=HashMap<String,String>()
        var  placeName = "-NA-"
        var vicinity = "-NA-"
        var latitude = ""
        var longitude = ""
        var reference = ""

        try {
            if (!googlePlaceJson!!.isNull("name"))
            {
                placeName=googlePlaceJson.getString("name")
            }

            if (!googlePlaceJson!!.isNull("vicinity"))
            {
                vicinity=googlePlaceJson.getString("vicinity")
            }
            latitude=googlePlaceJson.getJSONObject("geometry")
                .getJSONObject("location").getString("lat")

            longitude=googlePlaceJson.getJSONObject("geometry")
                .getJSONObject("location").getString("lng")

            reference=googlePlaceJson.getString("reference")

            googlePlaceMap.put("place_name", placeName)
            googlePlaceMap.put("vicinity", vicinity)
            googlePlaceMap.put("lat", latitude)
            googlePlaceMap.put("lng", longitude)
            googlePlaceMap.put("reference", reference)



        }catch (e:JSONException)
        {
            e.printStackTrace()
        }
        return  googlePlaceMap


    }
}