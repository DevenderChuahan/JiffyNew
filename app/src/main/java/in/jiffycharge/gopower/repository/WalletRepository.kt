package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.model.User_balance_model
import `in`.jiffycharge.gopower.model.Wallet_history_model
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException

class WalletRepository(private val api:ApiInterface) {

    val balance_data=MutableLiveData<User_balance_model>()
    val wallet_history_data=MutableLiveData<Wallet_history_model>()
    val response_message=MutableLiveData<String>()


    init {
        fetch_user_balance()
        fetch_wallet_history()

    }


    private fun fetch_user_balance() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response=api.getUserBalance()
                withContext(Dispatchers.Main)
                {

                    try {

                        if (response.isSuccessful)
                        {

                            response_message.postValue(response.code().toString())
                            balance_data.postValue(response.body())


                        }else
                        {
                            response_message.postValue(response.code().toString())


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

    private fun fetch_wallet_history() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response=api.getWalletHistory()
                withContext(Dispatchers.Main)
                {

                    try {

                        if (response.isSuccessful)
                        {

                            response_message.postValue(response.code().toString())
                            wallet_history_data.postValue(response.body())


                        }else
                        {
                            response_message.postValue(response.code().toString())


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



}