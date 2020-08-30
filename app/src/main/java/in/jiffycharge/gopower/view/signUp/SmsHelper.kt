package `in`.jiffycharge.gopower.view.signUp

import `in`.jiffycharge.gopower.utils.MobSmsHelper

class SmsHelper(private val sms:SmsInterface):SmsInterface by sms {

    companion object
    {
        fun create():SmsInterface
        {

            return MobSmsHelper()


        }



    }
}