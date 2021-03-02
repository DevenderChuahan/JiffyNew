package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.model.CreateOrderModel
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.model.Find_Near_Shop_Location_Model
import `in`.jiffycharge.gopower.utils.Resourse
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.math.BigDecimal

class MapRepository(val api: ApiInterface) {

    val location_data= MutableLiveData<Resourse<Find_Near_Shop_Location_Model>>()
//    val response_message=MutableLiveData<String>()



    public fun find_near_location_shop_list(lat: Double?, lng: Double?) {

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response=api.getNearShopLocation(BigDecimal(lng!!), BigDecimal(lat!!), 5000)
                withContext(Dispatchers.Main)
                {

                    try {

                        if (response.isSuccessful && response.body()!!.success)
                        {

//                        response_message.postValue(response.code().toString())
//                        location_data.postValue(response.body())
                            location_data.postValue(Resourse.success(response.body()) as Resourse<Find_Near_Shop_Location_Model>?)



                        }else
                        {
//                        response_message.postValue(response.code().toString())

                            location_data.postValue(Resourse.error(response.body()!!.error_description))

                        }


                    }catch (e: HttpException)
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


}