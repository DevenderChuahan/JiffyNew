package `in`.jiffycharge.gopower.utils

import `in`.jiffycharge.gopower.view.signUp.SmsInterface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK

class MobSmsHelper: SmsInterface {
    private lateinit var sendCall: (Boolean) -> Unit
    private  var verificationCall: ((Boolean) -> Unit)? =null

    private val eventHandler: EventHandler = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any?) {
            super.afterEvent(event, result, data)

            val msg = Message()
            msg.arg1 = event
            msg.arg2 = result
            msg.obj = data

            Handler(Looper.getMainLooper(), Handler.Callback {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                      if (result == SMSSDK.RESULT_COMPLETE) {
                        sendCall.invoke(true)

                    } else {
                        (data as Throwable).printStackTrace()
                        sendCall.invoke(false)

                    }

                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        verificationCall?.invoke(true)

                    } else {
                        (data as Throwable).printStackTrace()
                        verificationCall?.invoke(false)


                    }
                }

                false
            }).sendMessage(msg)


        }


    }

    override fun send(country: String?, phone: String?, sendCall: (Boolean) -> Unit) {

if (country.isNullOrBlank() || phone.isNullOrBlank())
{
    sendCall.invoke(false)
    return
}
this.sendCall=sendCall
        SMSSDK.getVerificationCode(country,phone)


    }

    override fun verification(
        country: String?,
        phone: String?,
        code: String?,
        verificationCall: (Boolean) -> Unit
    ) {

        if (country.isNullOrBlank() ||phone.isNullOrBlank() || code.isNullOrBlank())
        {
            verificationCall.invoke(false)
            return

        }
        this.verificationCall = verificationCall
//Submit the verification code, where the code represents the verification code, such as "1239"
        SMSSDK.submitVerificationCode(country, phone, code)





    }

    override fun onCreate() {
        // Register an event callback to process the result of the smssdk interface request
        SMSSDK.registerEventHandler(eventHandler)
    }


    override fun onDestroy() {
        SMSSDK.unregisterEventHandler(eventHandler)
    }
}