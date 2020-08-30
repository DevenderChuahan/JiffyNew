package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.model.DoingInfoModel
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.utils.Resourse
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WalletPayRepository(val api:ApiInterface) {
//    var walletpayResult=MutableLiveData<Resourse<>>()

    fun  doingfInfo()
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {

//                doingInfoResult.postValue(Resourse.loading())

                val response=api.findDoingInfoUsingGET()

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful)
                        {
//                            doingInfoResult.postValue(Resourse.success(response.body()) as Resourse<DoingInfoModel>?)

                        }else

                        {
//                            doingInfoResult.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())



                        }

                    }catch (e: HttpException)
                    {
                        e.printStackTrace()

                    }catch (e:Throwable)
                    {
                        e.printStackTrace()

                    }
                }




            }

        }catch (e:Exception)
        {
        }


    }
}