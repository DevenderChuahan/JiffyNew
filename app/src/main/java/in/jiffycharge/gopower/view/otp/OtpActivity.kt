package `in`.jiffycharge.gopower.view.otp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.home.HomeActivity

import kotlinx.android.synthetic.main.activity_otp.*
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.csp
import `in`.jiffycharge.gopower.model.signup_mobile_model
import `in`.jiffycharge.gopower.setsp
import `in`.jiffycharge.gopower.utils.Constants
import `in`.jiffycharge.gopower.utils.Constants.Companion.initOkHttp
import `in`.jiffycharge.gopower.utils.OAuthOkHttpClient
import `in`.jiffycharge.gopower.view.signUp.SignUpActivity
import `in`.jiffycharge.gopower.view.signUp.SmsHelper
import android.util.Log
import android.view.WindowManager
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.jetbrains.anko.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {
    protected  val smsHelper by lazy { SmsHelper.create() }

    var code_sent:String?=null
    var code_sent2:String?=null
    var complete_no:String?=null
    var password:String?=null
    var country_code:String?=null
    var country_code_without_plus:String?=null
    var contact_no:String?=null
     var context:Context?=null
    var otp_flag:Boolean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        val intent=intent
        context=this

//        otp_flag=false
        initOkHttp(this)
        lifecycle.addObserver(smsHelper)

//        code_sent=intent.getStringExtra("sentcode")
//        complete_no=intent.getStringExtra("complete_no")
        password=intent.getStringExtra("password")
//        country_code=intent.getStringExtra("country_code")
        country_code_without_plus=intent.getStringExtra("country_code_without_plus")
        contact_no=intent.getStringExtra("contact_no")
        countDownTimer()

        btn_submit.setOnClickListener {
            val _1Digit=edt_1.text.toString()
            val _2Digit=edt_2.text.toString()
            val _3Digit=edt_3.text.toString()
            val _4Digit=edt_4.text.toString()
//            val _5Digit=edt_5.text.toString()
//            val _6Digit=edt_6.text.toString()

            if(_1Digit.isNullOrEmpty())
            {
                edt_1.requestFocus()
                return@setOnClickListener
            }else if(_2Digit.isNullOrEmpty())
            {
                edt_2.requestFocus()
                return@setOnClickListener
            } else if(_3Digit.isNullOrEmpty())
            {
                edt_3.requestFocus()
                return@setOnClickListener
            } else if(_4Digit.isNullOrEmpty())
            {
                edt_4.requestFocus()
                return@setOnClickListener
            }
//            else if(_5Digit.isNullOrEmpty())
//            {
//                edt_5.requestFocus()
//                return@setOnClickListener
//            } else if(_6Digit.isNullOrEmpty())
//            {
//                edt_6.requestFocus()
//                return@setOnClickListener
//            }
            else

            {
                val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(edt_1.windowToken,0)
                imm.hideSoftInputFromWindow(edt_2.windowToken,0)
                imm.hideSoftInputFromWindow(edt_3.windowToken,0)
                imm.hideSoftInputFromWindow(edt_4.windowToken,0)
//                imm.hideSoftInputFromWindow(edt_5.windowToken,0)
//                imm.hideSoftInputFromWindow(edt_6.windowToken,0)


                pbr_otp.visibility=View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )


//                val entered_code=_1Digit+_2Digit+_3Digit+_4Digit+_5Digit+_6Digit
                val entered_code=_1Digit+_2Digit+_3Digit+_4Digit



                smsHelper.verification(country_code_without_plus,contact_no,entered_code)
                {

                    runOnUiThread {
                        if (it)
                        {

                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            call_api()


                        }else
                        {
                            //Failed to send verification code

                            toast("Verification code Error")
                            pbr_otp.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                        }
                    }





                }

//                if (otp_flag==true)
//                {
//                    call_api()
//                }else
//                {
//                    val credential = PhoneAuthProvider.getCredential(code_sent!!, entered_code)
//
//                    signInWithPhoneAuthCredential(credential)

//                }


            }


        }

        txt_resend.setOnClickListener {
            pbr_otp.visibility=View.VISIBLE
            edt_1.setText("")
            edt_2.setText("")
            edt_3.setText("")
            edt_4.setText("")
//            edt_5.setText("")
//            edt_6.setText("")


            smsHelper.send(country_code_without_plus,contact_no)
            {

                runOnUiThread {
                    if (it)
                    {
                        //Verification code sent successfully

                        pbr_otp.visibility= View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    countDownTimer()



                    }else
                    {
                        //Failed to send verification code

                        toast("Verification code failed to send")
                        pbr_otp.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                        call_sign_up()


                    }
                }





            }



//            otp_flag=false



            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

//
//            val callbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks()
//            {
//                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
//
//
//                }
//
//                override fun onVerificationFailed(p0: FirebaseException) {
//
//                    toast(p0.toString())
//
//                }
//
//                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
//                    super.onCodeSent(p0, p1)
//                    code_sent=p0
//                    pbr_otp.visibility=View.GONE
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//
//                }
//
//            }
//
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                complete_no!!, // Phone number to verify
//                60, // Timeout duration
//                TimeUnit.SECONDS, // Unit of timeout
//                this, // Activity (for callback binding)
//                callbacks)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(smsHelper)
    }

    private fun countDownTimer() {
       object :CountDownTimer(20000,1000)
       {
           override fun onTick(millisUntilFinished: Long) {
               txt_resend.isClickable=false

               txt_timer.visibility=View.VISIBLE
               txt_resend.text= getString(R.string.resend_code_in)
               txt_timer.text= (millisUntilFinished/1000).toString().plus(" sec")

           }

           override fun onFinish() {
               txt_resend.text= getString(R.string.resend)
               txt_resend.setTextSize(16f)
               txt_resend.setTextColor(Color.parseColor("#25d0fc"))
               txt_timer.visibility=View.GONE
               txt_resend.isClickable=true


           }

       }.start()

    }

//    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth!!.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
////                    val user = task.result?.user
////                    otp_flag=true
//                    call_api()
//
//
//
//
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        Log.e("OTP ERROR","${task.exception}")
//                        toast(task.exception.toString())
//                    }
//
//                    pbr_otp.visibility=View.GONE
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                    csp().edit().clear().apply()
//
//                }
//            }
//    }

    private fun call_api() {
        create_token_using_moobile_no("$country_code_without_plus-$contact_no", password!!, GrantType.CLIENT_CREDENTIALS)
        { throwable: Throwable?, token ->
            val accessToken = token?.accessToken!!.trim()
            Log.e("OTP Access Oken", accessToken)

            runOnUiThread {
                if (accessToken!==null) {
                    context!!.setsp("token",accessToken)
                    mobile_sign_up_api()


                }
            }

        }
    }

    fun create_token_using_moobile_no(
        phone: String,
        psw: String,
        grantType: GrantType,
        result: (t: Throwable?, token: OAuthJSONAccessTokenResponse?) -> Unit
    ) {

        try {
            async {

                val request = OAuthClientRequest
                    .tokenLocation(Constants.token_Url)
                    .setClientId(Constants.app_client_Id)
                    .setClientSecret(Constants.app_Client_Secret)
                    .setUsername(phone)
                    .setPassword(psw)
                    .setGrantType(grantType)
                    .buildBodyMessage()
                val client = OAuthClient(OAuthOkHttpClient(Constants.okHttpClient))
                val accessTokenResponse = client.accessToken(request)
                val APP_TOKEN = accessTokenResponse?.accessToken ?: ""
                result.invoke(null, accessTokenResponse)
                client.shutdown()
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
            pbr_otp.visibility=View.GONE


        }



    }



    private fun mobile_sign_up_api() {
//        context!!.setsp("token","2f480081-04fe-4625-9bd7-931ceefe2a65")

        val service= context?.let { Constants.getApiService(it) }
        val call=service!!.signUp(country_code_without_plus,contact_no,"",password )
        call.enqueue(object : Callback<signup_mobile_model>
        {
            override fun onResponse(
                call: Call<signup_mobile_model>,
                response: Response<signup_mobile_model>
            ) {
                if(response.isSuccessful) {
                    Log.e("api Response","${response.body()}")
                    val body_response = response.body()!!.success
                    if (body_response) {
                        pbr_otp.visibility=View.GONE
                        toast("Login Success")
                        go_to_homme()
                    } else {
                        toast(response.body()!!.error_description.toString())
                        pbr_otp.visibility=View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        csp().edit().clear().apply()

                        call_sign_up()

                    }
                }else{
                    toast(response.body().toString())
                    pbr_otp.visibility=View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    csp().edit().clear().apply()

                    onBackPressed()
                }
            }

            override fun onFailure(call: Call<signup_mobile_model>, t: Throwable) {
                toast(t.toString())
                pbr_otp.visibility=View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                csp().edit().clear().apply()

            }

        })

    }

    private fun call_sign_up() {

        startActivity(Intent(this,SignUpActivity::class.java))
        finish()

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        call_sign_up()
//
    }


    private fun go_to_homme() {
        val intent = Intent (this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}
