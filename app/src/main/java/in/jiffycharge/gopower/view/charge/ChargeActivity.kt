package `in`.jiffycharge.gopower.view.charge

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.CashfreeModel
import `in`.jiffycharge.gopower.model.RazorpayOrder
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.payment.PayResultActivity
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_charge.*
import okhttp3.Credentials
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class ChargeActivity : AppCompatActivity(), PaymentResultListener {
    val home_view_model by viewModel<HomeActivityViewModel>()
    lateinit var chargeAdapter:ChargeAdapter
    var amtflag=false
    var selectedAmt:Int=0
    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)
        chargeAdapter= ChargeAdapter(this,home_view_model)

        home_view_model.repo.getPayAmountListUsingGET()
        home_view_model.repo.payamountlist.observe(this, Observer {
            runOnUiThread {
                when(it.status)
                {
                    Resourse.Status.SUCCESS->
                    {
                        val data=it.data?.items?:return@runOnUiThread

                        walletpay_loader.visibility=View.GONE
//                        rv_charge.adapter=ChargeAdapter(this)
//                        ChargeAdapter(this).refreshList(data)
                        rv_charge.adapter=chargeAdapter.apply {
                            this.refreshList(data)
                            notifyDataSetChanged()

                        }







                    }

                    Resourse.Status.ERROR->
                    {
                        toast(it.exception?:return@runOnUiThread)


                    }




                }



            }


        })


        home_view_model.repo.adapteritemlist.observe(this, Observer {

            selectedAmt= it.id.toInt()





        })


        home_view_model.repo.payamtbalancelist.observe(this, Observer {
            runOnUiThread {

                when (it.status) {

                    Resourse.Status.SUCCESS -> {



                      payUsingRazorpay(it?.data?:return@runOnUiThread)

// PayResultActivity(this).checkPayResult(
//                            it?.data?:return@runOnUiThread
//                        )
//                        {
//                            home_view_model.repo.fetchUserprofile()
//
//                            finish()
//                        }


                    }
                    Resourse.Status.ERROR -> {

                        toast(it.exception?:return@runOnUiThread)


                    }


                    else -> {

                    }
                }


            }


        })


        chargeBt.setOnClickListener {
            if(amtflag)
            {
                    home_view_model.repo.getpaymetBalanceUsingPOST(
                        selectedAmt,
                        "cashfree",
                        PayResultActivity.RETURN_URL_RE_CHARGE
                    )




            }else
            {
                toast("Please select an amount !!")
            }
                  }

        charge_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun payUsingRazorpay(
        payResult: CashfreeModel
    ) {

        val cashFreeBo=payResult.item.cashfreePaymentBO?:return
        var basicAuth = Credentials.basic("rzp_test_YeQ6LGSXqFCfrm", "VEpsTBHBk8M4o2JSkgbBTQaw")
        val map=HashMap<String,String>()
        val amount=(cashFreeBo.orderAmount).replace(".00","")
        Log.v("JiffyOrderID",cashFreeBo.orderId)

        map.put("amount", (amount.toInt()*100).toString())
        map.put("currency",cashFreeBo.orderCurrency)
        map.put("receipt","receipt#1")

        val  retrofit= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.razorpay.com/").build()
        val apiService = retrofit.create(ApiInterface::class.java)
        apiService.getRazorpayOrderId(basicAuth,map).enqueue(object : Callback<RazorpayOrder>
        {

            override fun onResponse(call: Call<RazorpayOrder>, response: Response<RazorpayOrder>) {
                if (response.isSuccessful)
                {
                    val orderId=response.body()?.id
                    val amt=response.body()?.amount
                    val currency=response.body()?.currency


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
                        obj.put("prefill.contact",cashFreeBo.customerPhone)
                        //put email
                        obj.put("prefill.email",cashFreeBo.customerEmail)
                        //open Razorpay checkout Activity
                        checkout.open(this@ChargeActivity,obj)




                    }catch (e:JSONException)
                    {

                        e.printStackTrace()
                    }


                }



            }

            override fun onFailure(call: Call<RazorpayOrder>, t: Throwable) {
                this@ChargeActivity.toast(t.toString())

            }

        })



    }


    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: String) {
        if (!event.isNullOrBlank()) {

            amtflag = event.equals("ItemSelected")

        }


    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPaymentError(p0: Int, s: String?) {
        s?.let { this.toast(it) }

    }

    override fun onPaymentSuccess(s: String?) {
        //initialize alert dialog
        val builder= AlertDialog.Builder(this)
        //set tittle
        builder.setTitle("Payment ID")
        //set message
        builder.setMessage(s)
        //show dialog
        builder.show()

        home_view_model.repo.fetchUserprofile()

        finish()
    }
}