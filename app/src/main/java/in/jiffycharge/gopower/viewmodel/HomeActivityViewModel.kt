package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.repository.HomeRepository
import android.view.View
import androidx.lifecycle.ViewModel
import `in`.jiffycharge.gopower.view.map.Home_Interface
import androidx.databinding.ObservableField
import java.util.*

class HomeActivityViewModel(val repo:HomeRepository):ViewModel() {
    private val usingOrderTimer by lazy { Timer() }
    private var usingOrderTask: TimerTask? = null
     val USING_ORDER_TIME=10*1000L

    var wallet_balance= ObservableField<String>()
    var deposit_amount= ObservableField<String>()



    var home_interface:Home_Interface?=null


    fun startUsingOrderTask()
    {

        if (usingOrderTask == null) {
            usingOrderTask = object : TimerTask() {
                override fun run() {
                    repo.doingfInfo()
                }
            }
            val time = USING_ORDER_TIME
            usingOrderTimer.schedule(usingOrderTask, time, time)
        }

    }


    fun log_out(view: View)
    {
        home_interface?.log_out()

    }



    fun open_profile(view:View)
    {
        home_interface?.open_profile()

    }

 fun go_to_Home(view:View)
    {
        home_interface?.go_to_Home()

    }

 fun go_to_subscription(view:View)
    {
        home_interface?.go_to_subscription()

    }

 fun go_to_my_orders(view:View)
    {
        home_interface?.go_to_my_orders()

    }

 fun go_to_wallet(view:View)
    {
        home_interface?.go_to_wallet()

    }

 fun go_to_coupons(view:View)
    {
        home_interface?.go_to_coupons()

    }

 fun go_to_support(view:View)
    {
        home_interface?.go_to_support()

    }

 fun open_drawer(view:View)
    {
        home_interface?.open_drawer()

    }




}