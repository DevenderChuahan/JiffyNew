package `in`.jiffycharge.gopower.view.login

import `in`.jiffycharge.gopower.utils.Constants
import `in`.jiffycharge.gopower.utils.toast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSBroadcastReceiver: BroadcastReceiver() {
    companion object
    {
        var level: Int? =null
    }


    private var otpReciever:OTPReceiveListener?=null
     fun iniOTPListener(reciever:OTPReceiveListener)
      {
          this.otpReciever=reciever

      }


    override fun onReceive(context: Context, intent: Intent) {


        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            if (extras != null) {
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as Status

//                val status = extras.get(SmsRetriever.EXTRA_STATUS) as AsyncTask.Status

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        var otp = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
////                        Extract the 4 digit integer from SMS
//                        val pattern = Pattern.compile("\\d{4}")
//                        val matcher = pattern.matcher(message)
//                        if (matcher.find()) {
////                                  Update UI
//                            Toast.makeText(context, "Your OTP is :" + matcher.group(0), Toast.LENGTH_SHORT).show()
//                        }



                        if (otpReciever != null) {
                            otp = otp.replace("<#> ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                            otpReciever!!.onOTPReceived(otp)
                        }

                    }
                    CommonStatusCodes.TIMEOUT -> {
                        if (otpReciever != null)
                            otpReciever!!.onOTPTimeOut()
                    }
                }// Waiting for SMS timed out (5 minutes)
                // Handle the error ...

            }

        }

    }


    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }

}