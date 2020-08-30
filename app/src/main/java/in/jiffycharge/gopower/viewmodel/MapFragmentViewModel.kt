package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.repository.MapRepository
import android.view.View
import androidx.lifecycle.ViewModel
import `in`.jiffycharge.gopower.view.map.MapInterface

class MapFragmentViewModel(val mapRepository: MapRepository):ViewModel() {
    var map_interface:MapInterface?=null






    fun locate_to_location(view: View)
    {
        map_interface?.locate_to_location()

    }
    fun click_on_Scan(view:View)
    {
        map_interface?.click_on_Scan()

    }


}