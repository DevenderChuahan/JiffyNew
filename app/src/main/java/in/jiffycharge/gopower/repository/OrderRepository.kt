package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.model.DepositModel
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.model.Order_Details_model
import `in`.jiffycharge.gopower.model.Order_list_model
import `in`.jiffycharge.gopower.utils.Resourse
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OrderRepository(private  val api:ApiInterface) {
      val _data= MutableLiveData<Resourse<Order_list_model>>()
      val _detailes_list= MutableLiveData<Resourse<Order_Details_model>>()
    val response_message=MutableLiveData<String>()

    init {
        fetchData()
    }


    private  fun fetchData()
    {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response=api.getOrderList("")
                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful && response.body()!!.success) {
                        

                            _data.postValue(Resourse.success(response.body()) as Resourse<Order_list_model>?)


                        }else

                        {

                            _data.postValue(Resourse.error(response.errorBody().toString()))


                        }

                    }catch (e:HttpException)
                    {
                        e.printStackTrace()

                    }catch (e:Throwable)
                    {
                        e.printStackTrace()

                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }







        }

    }
    fun  get_order_view_deatils(order_code:String)
    {
         CoroutineScope(Dispatchers.IO).launch {
             try {
                 val response = api.getOrderDetails(order_code)
                 withContext(Dispatchers.Main)
                 {
                     try {
                         if (response.isSuccessful && response.body()!!.success) {


                             _detailes_list.postValue(Resourse.success(response.body()) as Resourse<Order_Details_model>?)


                         } else {

                             _detailes_list.postValue(Resourse.error(response.errorBody().toString()))

                         }

                     } catch (e: HttpException) {
                         e.printStackTrace()

                     } catch (e: Throwable) {
                         e.printStackTrace()

                     }
                 }
             } catch (e: HttpException) {
                 e.printStackTrace()

             } catch (e: Throwable) {
                 e.printStackTrace()

             }





             }




    }





}