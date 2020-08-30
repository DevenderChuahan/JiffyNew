package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.model.Order_Details_model
import `in`.jiffycharge.gopower.model.Order_list_model
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OrderRepository(private  val api:ApiInterface) {
      val _data= MutableLiveData<Order_list_model>()
      val _detailes_list= MutableLiveData<Order_Details_model>()
    val response_message=MutableLiveData<String>()

    init {
        fetchData()
    }


    private  fun fetchData()
    {

        CoroutineScope(Dispatchers.IO).launch {
            val respomse=api.getOrderList("")
            withContext(Dispatchers.Main)
            {
                try {

                    if (respomse.isSuccessful)
                    {
                        response_message.postValue(respomse.code().toString())
                        _data.postValue(respomse.body())

                    }else

                    {
                        response_message.postValue(respomse.code().toString())



                    }

                }catch (e:HttpException)
                {
                    e.printStackTrace()

                }catch (e:Throwable)
                {
                    e.printStackTrace()

                }
            }




        }

    }
    fun  get_order_view_deatils(order_code:String)
    {

         CoroutineScope(Dispatchers.IO).launch {
                 val respomse = api.getOrderDetails(order_code)
                 withContext(Dispatchers.Main)
                 {
                     try {
                         if (respomse.isSuccessful) {

                             response_message.postValue(respomse.code().toString())


                             _detailes_list.postValue(respomse.body())

                         } else {

                             response_message.postValue(respomse.code().toString())

                         }

                     } catch (e: HttpException) {
                         e.printStackTrace()

                     } catch (e: Throwable) {
                         e.printStackTrace()

                     }
                 }


             }




    }





}