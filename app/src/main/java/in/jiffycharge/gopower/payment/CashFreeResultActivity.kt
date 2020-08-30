package `in`.jiffycharge.gopower.payment

import `in`.jiffycharge.gopower.model.CashFreeVOModel
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gocashfree.cashfreesdk.CFPaymentService
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap

class CashFreeResultActivity:AppCompatActivity() {
    val home_view_model by viewModel<HomeActivityViewModel>()

    private val TAG="CashFreehelper"
    companion object {
        internal var onPayResultListener: PayResultListener? = null
        internal var params: HashMap<String, String>? = null
        internal var token: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CFPaymentService.getCFPaymentServiceInstance().apply {
            val clients = getUpiClients(this@CashFreeResultActivity)
//            selectUpiClient()
            setConfirmOnExit(this@CashFreeResultActivity, true)
//            upiPayment(this@CashFreeResultActivity, params, token, "PROD"/*stage: "TEST" or "PROD" */)
            doPayment(this@CashFreeResultActivity, params, token, "PROD"/*stage: "TEST" or "PROD" */)
//            doAmazonPayment(this@CashFreeResultActivity, params, token, "PROD"/*stage: "TEST" or "PROD" */)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("ReqCode",CFPaymentService.REQ_CODE.toString())
        val resultMap = mutableMapOf<String, String?>()
        if (data != null) {
            val bundle: Bundle? = data.extras
            if (bundle != null) for (key in bundle.keySet()) {
                if (bundle.getString(key) != null) {
                    val value = bundle.getString(key)
                    resultMap[key] = value
                    Log.d(TAG, key + " : " + value)
                }
            }
        }
        Log.v("resultMap","{$resultMap}")
        when (resultMap["txStatus"]?.toUpperCase(Locale.getDefault())) {
//            "SUCCESS" -> {
//                onPayResultListener?.onSuccess(resultMap)
//            }
            "FAILED" -> {
                onPayResultListener?.onFail(resultMap["txMsg"])
                showErrorMessage(resultMap["txMsg"])
            }
            "CANCELLED" -> {
                onPayResultListener?.onCancel()
                finish()
            }
            else -> {
                upResult(resultMap)
            }
        }
    }

    private fun upResult(resultMap: MutableMap<String, String?>) {
        val gson = Gson()
       val resultVo= gson.fromJson<CashFreeVOModel>(gson.toJson(resultMap),CashFreeVOModel::class.java)

        home_view_model.repo.getVO(resultVo)
        home_view_model.repo.voResult.observe(this, androidx.lifecycle.Observer {
            when(it.status)
            {

                Resourse.Status.SUCCESS->
                {

                    if (it.data?.success==true)
                    {
                        onPayResultListener?.onSuccess(it)
                        toast("Payment Success")
                        finish()


                    }else
                    {
                        showErrorMessage(it.data?.error)

                    }

                }

                 Resourse.Status.ERROR->
                {
                    showErrorMessage(it.data?.error_description)

                }


            }



        })



    }


    private fun showErrorMessage(message: String?) {
//        progressDialog?.dismiss()
        AlertDialog.Builder(this@CashFreeResultActivity)
            .setMessage(message ?: "unKnown Error")
            .setPositiveButton(android.R.string.cancel, null)
            .setOnDismissListener {
                finish()
            }.show()
    }
    override fun onDestroy() {
        onPayResultListener = null
        params = null
        token = null
        super.onDestroy()
    }


}