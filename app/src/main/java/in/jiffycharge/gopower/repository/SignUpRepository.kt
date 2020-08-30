package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.model.OtpModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import retrofit2.HttpException

class SignUpRepository(private val api:ApiInterface) {


    var otp_data=MutableLiveData<OtpModel>()
    var response_message=MutableLiveData<String>()

     fun get_otp(country_code:String, mobile:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val response=api.getOtp(country_code,mobile)
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful)
                    {
                        response_message.postValue(response.code().toString())
                        otp_data.postValue(response.body())

                    }else
                    {
                        response_message.postValue(response.message())

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




}