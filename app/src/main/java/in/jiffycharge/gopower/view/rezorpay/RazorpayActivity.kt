package `in`.jiffycharge.gopower.view.rezorpay

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.CashfreeResultResponseModel
import `in`.jiffycharge.gopower.model.RazorpayOrder
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.utils.Constants
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import okhttp3.Credentials
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RazorpayActivity : AppCompatActivity(),PaymentResultListener {
    private val TAG=RazorpayActivity::class.simpleName
    val home_view_model by viewModel<HomeActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_razorpay)
//        Checkout.preload(this)

        var basicAuth = Credentials.basic("rzp_test_YeQ6LGSXqFCfrm", "VEpsTBHBk8M4o2JSkgbBTQaw")
        Log.e(TAG,basicAuth)
        val map=HashMap<String,String>()
        map.put("amount","5000")
        map.put("currency","INR")
        map.put("receipt","receipt#1")

        val  retrofit= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.razorpay.com/").build()
        val apiService = retrofit.create(ApiInterface::class.java)
         apiService.getRazorpayOrderId(basicAuth,map).enqueue(object :Callback<RazorpayOrder>
         {

             override fun onResponse(call: Call<RazorpayOrder>, response: Response<RazorpayOrder>) {
                 if (response.isSuccessful)
                 {
                     val orderId=response.body()?.id
                     val amt=response.body()?.amount
                     val currency=response.body()?.currency
                     Log.e(TAG,orderId+","+amt+","+currency)


                     val checkout= Checkout()
                     //Set key id
                     checkout.setKeyID(resources.getString(R.string.razo_key_id));
                     //set image
                     checkout.setImage(R.mipmap.ic_launcher)
                     //init json object
                     val obj=JSONObject()
                     try {
                         //put name
                         obj.put("name","Jiffy")
                         //put description
                         obj.put("description","Test Payment")
                         //put color theme
                         obj.put("theme.color","#101010")
                         //put currency unit
                         obj.put("currency",currency)
                         //put amount
                         obj.put("amount",amt)
                         //put order id
                         obj.put("order_id",orderId)
                         //put mobile no
                         obj.put("prefill.contact","7018451823")
                         //put email
                         obj.put("prefill.email","devenderc6@gmail.com")
                         //open Razorpay checkout Activity
                         checkout.open(this@RazorpayActivity,obj)




                     }catch (e:JSONException)
                     {
                         e.printStackTrace()
                     }







                 }



             }

             override fun onFailure(call: Call<RazorpayOrder>, t: Throwable) {

             }

         })













    }

    override fun onPaymentError(p0: Int, s: String?) {
        s?.let { this.toast(it) }
        onBackPressed()


    }

    override fun onPaymentSuccess(s: String?) {
        //initialize alert dialog
        val builder=AlertDialog.Builder(this)
        //set tittle
        builder.setTitle("Payment ID")
        //set message
        builder.setMessage(s)
        //show dialog
        builder.show()




    }
}