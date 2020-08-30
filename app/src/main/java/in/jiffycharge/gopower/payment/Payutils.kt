package `in`.jiffycharge.gopower.payment

import android.app.Activity
import android.app.Application

object Payutils {
    private lateinit var cashFreeHelper: CashFreeHelper


    fun initSupportPayType(context:Application,vararg paytype:PayTypeEnum)
    {
        paytype.forEach {
            when(it)
            {
                PayTypeEnum.cashfree->
                {
                    cashFreeHelper = CashFreeHelper()

                }
                PayTypeEnum.Stripe->
                {

                }



            }



        }

    }

    fun payByCashFree(
        activity: Activity,
        params: HashMap<String, String>,
        token: String,
        onPayResultListener: PayResultListener
    ) {
        cashFreeHelper.cashFreePay(activity, params, token, onPayResultListener)
    }
}