package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.model.Order_Details_model
import `in`.jiffycharge.gopower.repository.OrderRepository
import `in`.jiffycharge.gopower.view.orders.ViewOrderFragment
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.os.HandlerCompat.postDelayed
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.delay
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler

class Orders_view_model(val order_repo:OrderRepository):ViewModel() {
    var order_code_txt=ObservableField<String>()
    var duration_mins=ObservableField<String>()
    var duration_cost=ObservableField<String>()
    var strat_time=ObservableField<String>()
    var end_time=ObservableField<String>()
    var pick_up_address=ObservableField<String>()
    var drop_address=ObservableField<String>()



    val data= order_repo._data
//    val Order_details_list_data=order_repo.get_order_view_deatils("")

fun call_order_details(order_code:String)
    {

        try {
             order_repo.get_order_view_deatils(order_code)

            order_repo._detailes_list.observeForever(Observer<Order_Details_model> {


                order_code_txt.set(it.item.orderCode)

                val sdftime = SimpleDateFormat("h:mm a")
                val start_Date = Date(it.item.beginTime!!.toLong())
                val end_Date = Date(it.item.finishTime!!.toLong())
                val Start_time = sdftime.format(start_Date)
                val End_time = sdftime.format(end_Date)
//
//
//
                strat_time.set(Start_time.trim().toString())
                end_time.set(End_time.trim().toString())
//


            duration_mins.set(it.item.rideTime.toString())
                duration_cost.set(it.item.price.toString())
//
                if (it.item.beginLocationDetails.isNullOrEmpty())
                {
                    pick_up_address.set("N/A")

                }else
                {
                    pick_up_address.set(it.item.beginLocationDetails.toString())

                }


                if (it.item.endLocaitonDetails.isNullOrEmpty())
                {
                    drop_address.set("N/A")

                }else
                {
                    drop_address.set(it.item.endLocaitonDetails.toString())

                }





            })



        }catch (e:Exception)
        {
            e.printStackTrace()
        }


        }




}






