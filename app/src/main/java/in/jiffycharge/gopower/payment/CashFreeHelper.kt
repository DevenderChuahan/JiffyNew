package `in`.jiffycharge.gopower.payment

import android.app.Activity
import android.content.Intent

class CashFreeHelper
{

    fun cashFreePay(activity:Activity,params:HashMap<String, String>, token:String,onPayResultListener: PayResultListener){
        CashFreeResultActivity.onPayResultListener=onPayResultListener
        CashFreeResultActivity.params=params
        CashFreeResultActivity.token=token
        activity.startActivity(Intent(activity,CashFreeResultActivity::class.java))
    }


}
