package `in`.jiffycharge.gopower.view.deposit

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.CashFreeVOModel
import `in`.jiffycharge.gopower.model.CashfreeModel
import `in`.jiffycharge.gopower.model.RazorpayOrder
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.payment.PayResultActivity
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.activity_deposit.*
import okhttp3.Credentials
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.binary.Hex.encodeHexString
import org.apache.commons.codec.digest.DigestUtils
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class DepositActivity : AppCompatActivity(), PaymentResultWithDataListener {
    var context: Context? = null
    var deposit_Order: String? = null
    val home_view_model by viewModel<HomeActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        context = this

        Update_adapter()
        swipeRefreshView.setOnRefreshListener {
            tv_no_data_found.visibility = View.GONE
            rv_deposit.visibility = View.GONE
            Update_adapter()

        }

        iv_back.setOnClickListener {
            onBackPressed()
        }


        home_view_model.repo._data.observe(this, Observer { userprofile ->
            (context as DepositActivity).runOnUiThread {


                when (userprofile.status) {
                    Resourse.Status.LOADING -> {

                    }

                    Resourse.Status.SUCCESS -> {
//                                            toast("Deposit Succuess")

                        tv_depoist.text = "${userprofile.data!!.item.deposit}".plus(" ")
                            .plus(userprofile.data.item.currency)
                        if (userprofile.data.item.deposit > 0.0) {
                            btn_deposit.text = "Deposit Refund"

                            //show refund dialog/ api
                            btn_deposit.setOnClickListener { }


                        } else {

                            //show  recharge dialog/ api
                            btn_deposit.text = "Deposit"

                            btn_deposit.setOnClickListener {
                                home_view_model.repo.getdeposit()

                            }


                        }
                    }
                    Resourse.Status.ERROR -> {
                        toast(userprofile.data?.error_description.toString())

                    }

                }


            }


        })



        home_view_model.repo._datadeposit.observe(this, Observer { deposit_data ->
            (context as DepositActivity).runOnUiThread {
                when (deposit_data.status) {
                    Resourse.Status.LOADING -> {

                    }

                    Resourse.Status.SUCCESS -> {


                        val builder = AlertDialog.Builder(this)
                        //set title for alert dialog
                        builder.setTitle("Deposit ")
                        //set message for alert dialog
                        builder.setMessage(
                            "Deposit amount".plus(" ${(deposit_data.data?.item?.deposit)}")
                                .plus(" ").plus(deposit_data.data?.item?.currency)
                        )
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        //performing positive action
                        builder.setPositiveButton("Pay") { dialog, which ->
                            dialog.dismiss()

//                                            home_view_model.repo.payDeposit(
//                                                "cashfree",
//                                                PayResultActivity.RETURN_URL_DEPOSIT
//                                            )
                            home_view_model.repo.payDeposit("cashfree", PayResultActivity.RETURN_URL_DEPOSIT)
//                            home_view_model.repo.payDeposit("razorpay", PayResultActivity.RETURN_URL_DEPOSIT)

                        }
//                                    //performing cancel action
//                                    builder.setNeutralButton("Cancel"){dialog , which ->
//                                        Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
//                                        dialog.dismiss()
//
//                                    }
                        //performing negative action
                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.cancel()
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()


                    }
                    Resourse.Status.ERROR -> {
                        toast(deposit_data.data?.error_description.toString())

                    }

                }

            }
        })


        home_view_model.repo._datapayment.observe(this,
            Observer {
                when (it.status) {
                    Resourse.Status.LOADING -> {

                    }
                    Resourse.Status.SUCCESS -> {

                        //RazorPay  Payment

//                        runOnUiThread {
//                            payUsingRazorpay(it.data!!).apply {
////                                returnUrl = PayResultActivity.RETURN_URL_DEPOSIT
//
//                            }
//
//                        }

                        //Cashfree Payment

                                                            PayResultActivity(this).checkPayResult(
                                                                it.data!!
                                                            )
                                                            {
                                                                Update_adapter()

                                                            }


                    }
                    Resourse.Status.ERROR -> {
                        toast(it.data?.error_description.toString())


                    }


                }


            })

    }

    private fun payUsingRazorpay(payResult: CashfreeModel) {


        val cashFreeBo = payResult.item.cashfreePaymentBO ?: return
        deposit_Order = cashFreeBo.orderId
        var basicAuth = Credentials.basic("rzp_test_YeQ6LGSXqFCfrm", "VEpsTBHBk8M4o2JSkgbBTQaw")
        val map = HashMap<String, String>()
        val amount = (cashFreeBo.orderAmount).replace(".00", "")

//        map.put("amount", (amount.toInt() * 100).toString())
        map.put("amount", (3* 100).toString())
        map.put("currency", cashFreeBo.orderCurrency)
        map.put("receipt", "receipt#1")

        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.razorpay.com/").build()
        val apiService = retrofit.create(ApiInterface::class.java)
        apiService.getRazorpayOrderId(basicAuth, map).enqueue(object : Callback<RazorpayOrder> {

            override fun onResponse(call: Call<RazorpayOrder>, response: Response<RazorpayOrder>) {
                if (response.isSuccessful) {
                    val orderId = response.body()?.id
                    val amt = response.body()?.amount
                    val currency = response.body()?.currency


                    val checkout = Checkout()
                    //Set key id
                    checkout.setKeyID(resources.getString(R.string.razo_key_id));
                    //set image
                    checkout.setImage(R.mipmap.ic_launcher)
                    //init json object
                    val obj = JSONObject()
                    try {
                        //put name
                        obj.put("key", "1212")
                        obj.put("name", "Jiffy")
                        //put description
                        obj.put("description", "Test Payment")
                        //put color theme
                        obj.put("theme.color", "#101010")
                        //put currency unit
                        obj.put("currency", currency)
                        //put amount
                        obj.put("amount", amt)

                        obj.put("notifyUrl", cashFreeBo.notifyUrl)
                        //put order id
                        obj.put("order_id", orderId)
                        //put mobile no
                        obj.put("prefill.contact", cashFreeBo.customerPhone)
                        //put email
                        obj.put("prefill.email", cashFreeBo.customerEmail)

                        //open Razorpay checkout Activity
                        checkout.open(this@DepositActivity, obj)


                    } catch (e: JSONException) {

                        e.printStackTrace()
                    }


                }


            }

            override fun onFailure(call: Call<RazorpayOrder>, t: Throwable) {
                this@DepositActivity.toast(t.toString())

            }

        })

    }

    private fun Update_adapter() {
        //** Set the colors of the Pull To Refresh View
        swipeRefreshView.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        swipeRefreshView.setColorSchemeColors(Color.WHITE)
        swipeRefreshView.isRefreshing = true


        home_view_model.repo.getDepositList()
        home_view_model.repo.get_deposit_list_data.observe(this, Observer {
            when (it.status) {
                Resourse.Status.LOADING -> {

                }
                Resourse.Status.SUCCESS -> {
//                    rv_deposit.visibility=View.VISIBLE
//                    tv_no_data_found.visibility= View.GONE
//
//                    depositadapter= DepositAdapter(context as DepositActivity, it.data!!.content)
//                    rv_deposit.adapter=depositadapter
//                    swipeRefreshView.isRefreshing = false

                    val list = it.data?.content
                    if (list.isNullOrEmpty()) {
                        rv_deposit.visibility = View.GONE
                        tv_no_data_found.visibility = View.VISIBLE
                        swipeRefreshView.isRefreshing = false


                    } else {

                        rv_deposit.visibility = View.VISIBLE
                        tv_no_data_found.visibility = View.GONE
                        swipeRefreshView.isRefreshing = false
                        rv_deposit.adapter = DepositAdapter(context, list)
                    }

                }
                Resourse.Status.ERROR -> {
                    rv_deposit.visibility = View.GONE
                    tv_no_data_found.visibility = View.VISIBLE
                    swipeRefreshView.isRefreshing = false


                }

            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


//    override fun onPaymentError(p0: Int, s: String?) {
//        s?.let { this.toast(it) }

//    }

//    override fun onPaymentSuccess(s: String?) {
//        s?.let { this.toast("Payment successful") }
//
//        //initialize alert dialog
//        val builder = AlertDialog.Builder(this)
//        //set tittle
//        builder.setTitle("Payment ID")
//        //set message
//        builder.setMessage(s)
//        //show dialog
//        builder.show()
//
//        Update_adapter()

//    }

    override fun onPaymentError(
        errorCode: Int,
        errorDescription: String?,
        paymentData: PaymentData?
    ) {

        errorDescription?.let { this.toast(it) }


    }

    override fun onPaymentSuccess(rzpPaymentId: String?, paymentData: PaymentData?) {


            val resultMap = mutableMapOf<String, String?>()
        resultMap["orderAmount"] = "3.00"
//        resultMap["orderId"] = paymentData?.orderId.toString()
        resultMap["orderId"] = deposit_Order
        resultMap["paymentMode"] = "DEBIT_CARD"
        resultMap["referenceId"] = paymentData?.paymentId.toString()
        resultMap["signature"] = paymentData?.signature
        resultMap["txMsg"] = "Active"
        resultMap["txStatus"] = "success"
        resultMap["txTime"] = "2021-01-26"


            val gson = Gson()
            val resultVo =
                gson.fromJson<CashFreeVOModel>(gson.toJson(resultMap), CashFreeVOModel::class.java)

            home_view_model.repo.getVO(resultVo)
            home_view_model.repo.voResult.observe(this, androidx.lifecycle.Observer {
                when (it.status) {

                    Resourse.Status.SUCCESS -> {

                        if (it.data?.success == true) {
                            toast("Payment Success")
                            Log.v("deposit_Order", deposit_Order)
                            home_view_model.repo.fetchUserprofile()


                            Update_adapter()

//                        finish()


                        } else {
                            it.data?.error?.let { it1 -> toast(it1) }

                        }

                    }

                    Resourse.Status.ERROR -> {
                        it.data?.error_description?.let { it1 -> toast(it1) }

                    }


                }


            })


       

    }

    private fun varifySignature(data: String, orderId: String?): String? {



        val secret="rzp_test_YeQ6LGSXqFCfrm"

        val signingKey: SecretKeySpec = SecretKeySpec(secret.toByteArray(),"HmacSHA256")

        val mac: Mac =Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

//        return Hex.encodeHex(DigestUtils.md5(mac.doFinal(data.toByteArray()))).toString()

       return Hex.encodeHexString(mac.doFinal(data.toByteArray()))


    }
}