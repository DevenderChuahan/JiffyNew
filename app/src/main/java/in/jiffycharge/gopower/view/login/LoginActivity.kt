package `in`.jiffycharge.gopower.view.login

import `in`.jiffycharge.gopower.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import `in`.jiffycharge.gopower.view.home.HomeActivity
import `in`.jiffycharge.gopower.view.otp.OtpActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import kotlinx.android.synthetic.main.activity_login.*
import `in`.jiffycharge.gopower.model.Third_party_login_model
import `in`.jiffycharge.gopower.model.signup_mobile_model
import `in`.jiffycharge.gopower.utils.*
import `in`.jiffycharge.gopower.view.signUp.SignUpActivity
import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import com.dd.processbutton.iml.ActionProcessButton
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {
    var country_code: String? = null
    var country_code_without_plus: String? = null
    var contact_no: String? = null
    var password: String? = null
    var context: Context? = null
    var complete_no:String?=null
    var code_sent:String?=null
    lateinit var callbackManager: CallbackManager

    lateinit var token_tracker: AccessTokenTracker
    lateinit var profile_tracker: ProfileTracker

    private var mBackPressed: Long = 0

    //google sign in
    lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        Constants.initOkHttp(this)


        btn_login.setOnClickListener {
            country_code = ccp.selectedCountryCodeWithPlus
            country_code_without_plus = ccp.selectedCountryCode
            contact_no = et_enter_no.text.toString()
            password = et_password.text.toString()
            complete_no=country_code+contact_no
            if(contact_no.isNullOrEmpty())
            {
                et_enter_no.setError("Phone number is required")
                et_enter_no.requestFocus()
                return@setOnClickListener
            } else if(password.isNullOrEmpty())
            {
                et_password.setError("Password is required")
                et_password.requestFocus()
                return@setOnClickListener
            }
              else if(contact_no!!.length<10){
                et_enter_no.setError("Please enter a valid number")
                et_enter_no.requestFocus()
                return@setOnClickListener



            }else
            {
//                pbr.visibility=View.VISIBLE

                btn_login.setMode(ActionProcessButton.Mode.ENDLESS)
                btn_login.progress=1
                btn_login.text="Loading..."

                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                et_enter_no.clearFocus()
                et_password.clearFocus()

                val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(et_enter_no.windowToken,0)

 val imm2=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(et_password.windowToken,0)


//                countDownTimer()

                create_token_using_moobile_no("$country_code_without_plus-$contact_no", password!!, GrantType.PASSWORD)
                { t, token ->
                    csp().edit().clear().apply()

//                    val accessToken = token?.accessToken!!.trim()
                    Log.e("DCLOGINTOKEN>>>>>>>>>",token?.accessToken.toString())

                    runOnUiThread {


                        if (token?.accessToken.isNullOrBlank()) {

                            pbr.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            toast(t!!.message.toString())
                            csp().edit().clear().apply()
                            btn_login.progress=0

                            btn_login.text="Login"




                        }else
                        {
                            context!!.setsp("token",token?.accessToken)
//                            mobile_login_api()
                            pbr.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                            toast("Login Success")
                            btn_login.progress=100
                            btn_login.text="Login Success"


                            go_to_homme()




                        }
                    }

                }


            }


        }
        btn_sign_up_.setOnClickListener {

            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left)

            finish()
        }

    }


    fun create_token_using_moobile_no(
        phone: String,
        psw: String,
        grantType: GrantType,
        result: (t: Throwable?, token: OAuthJSONAccessTokenResponse?) -> Unit
    ) {

//        try {

            Thread{
                tryError(handlerError = {
                    result.invoke(it, null)
                })
                {
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

            }.start()
//            async {
//
//                val request = OAuthClientRequest
//                    .tokenLocation(Constants.token_Url)
//                    .setClientId(Constants.app_client_Id)
//                    .setClientSecret(Constants.app_Client_Secret)
//                    .setUsername(phone)
//                    .setPassword(psw)
//                    .setGrantType(grantType)
//                    .buildBodyMessage()
//                val client = OAuthClient(OAuthOkHttpClient(Constants.okHttpClient))
//                val accessTokenResponse = client.accessToken(request)
//                val APP_TOKEN = accessTokenResponse?.accessToken ?: ""
//                result.invoke(null, accessTokenResponse)
//                client.shutdown()
//            }
//
//        }catch (e:Exception)
//        {
//            Log.v("DcException",e.toString())
//
//            e.printStackTrace()
//            result.invoke(e, null)
//
//            pbr.visibility=View.GONE
//
//
//        }



    }


    private fun go_to_homme() {
        val intent = Intent (this, HomeActivity::class.java)
        startActivity(intent)
        finish()    }

    private fun goToOtpActivity() {
        pbr.visibility=View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


        val intent=Intent(this, OtpActivity::class.java)
        intent.putExtra("sentcode",code_sent)
        intent.putExtra("complete_no",complete_no)
        startActivity(intent)
        finish()
    }




    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()


    }



}
