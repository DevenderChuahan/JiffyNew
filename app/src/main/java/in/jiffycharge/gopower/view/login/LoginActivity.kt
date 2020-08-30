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
    var coundown:CountDownTimer?=null

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
                    Log.v("DCLOGINTOKEN",token?.accessToken.toString())

                    runOnUiThread {


                        if (token?.accessToken.isNullOrBlank()) {

                            pbr.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            toast(t!!.message.toString())
                            csp().edit().clear().apply()
                            btn_login.progress=-1
                            btn_login.text="Login Failed"




                        }else
                        {
                            if (coundown!==null) coundown!!.cancel()
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


        bt_google.setOnClickListener {


            //create token
//            Constants.create_app_level_token(this)

            ////Google  init/////
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent, 101)

        }
        bt_facebook.setOnClickListener {
            //create token
            Constants.create_app_level_token(this)

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {

                        getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)


//                    gotoHome()

                    }

                    override fun onCancel() {
                        toast("Facebook Cancel Login")
                        csp().edit().clear().apply()


                    }

                    override fun onError(error: FacebookException?) {
                        toast(error.toString())
                        csp().edit().clear().apply()



                    }


                })


        }




        object :AccessTokenTracker()
        {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                if (currentAccessToken!=null)
                {
//                    loadFacebookProfile(currentAccessToken)

                }




            }


        }


    }

    private fun countDownTimer() {
        coundown=  object : CountDownTimer(10000,1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                pbr.visibility=View.VISIBLE
            }

            override fun onFinish() {
                pbr.visibility=View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                toast("Something went Wrong,Please Check Entered Value")
                csp().edit().clear().apply()



            }

        }.start()

    }



    private fun mobile_login_api() {


        val service= context?.let { Constants.getApiService(it) }
        val call=service!!.signUp(country_code_without_plus,contact_no,"",password )
        call.enqueue(object : Callback<signup_mobile_model>
        {
            override fun onResponse(
                call: Call<signup_mobile_model>,
                response: Response<signup_mobile_model>
            ) {
                if(response.isSuccessful) {
                    val body_response = response.body()!!.success
                    if (body_response) {
                        pbr.visibility=View.GONE
                        go_to_homme()
                    } else {
                        toast(response.body()!!.error_description.toString())
                        pbr.visibility=View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }else{
                    toast(response.errorBody().toString())
                    pbr.visibility=View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }

            override fun onFailure(call: Call<signup_mobile_model>, t: Throwable) {
                toast(t.toString())
                pbr.visibility=View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })

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

    @SuppressLint("LongLogTag")
    private fun getUserProfile(f_token: AccessToken?, f_userId: String?) {
var facebookName=""
var facebookEmail=""
var facebookProfilePicURL=""

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(f_token,
            "/$f_userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
//                if (BuildConfig.DEBUG) {
//                    FacebookSdk.setIsDebugEnabled(true)
//                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
//                }

                // Facebook Id
                if (jsonObject.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    Log.i("Facebook Id: ", facebookId.toString())
                } else {
                    Log.i("Facebook Id: ", "Not exists")
                }


                // Facebook First Name
                if (jsonObject.has("first_name")) {
                    val facebookFirstName = jsonObject.getString("first_name")
                    Log.i("Facebook First Name: ", facebookFirstName)
                } else {
                    Log.i("Facebook First Name: ", "Not exists")
                }


                // Facebook Middle Name
                if (jsonObject.has("middle_name")) {
                    val facebookMiddleName = jsonObject.getString("middle_name")
                    Log.i("Facebook Middle Name: ", facebookMiddleName)
                } else {
                    Log.i("Facebook Middle Name: ", "Not exists")
                }


                // Facebook Last Name
                if (jsonObject.has("last_name")) {
                    val facebookLastName = jsonObject.getString("last_name")
                    Log.i("Facebook Last Name: ", facebookLastName)
                } else {
                    Log.i("Facebook Last Name: ", "Not exists")
                }


                // Facebook Name
                if (jsonObject.has("name")) {
                     facebookName = jsonObject.getString("name")
                    Log.i("Facebook Name: ", facebookName)
                } else {
                    Log.i("Facebook Name: ", "Not exists")
                }


                // Facebook Profile Pic URL
                if (jsonObject.has("picture")) {
                    val facebookPictureObject = jsonObject.getJSONObject("picture")
                    if (facebookPictureObject.has("data")) {
                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
                        if (facebookDataObject.has("url")) {
                             facebookProfilePicURL = facebookDataObject.getString("url")
                            Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
                        }
                    }
                } else {
                    Log.i("Facebook Profile Pic URL: ", "Not exists")
                }

                // Facebook Email
                if (jsonObject.has("email")) {
                     facebookEmail = jsonObject.getString("email")
                    Log.i("Facebook Email: ", facebookEmail)
                } else {
                    Log.i("Facebook Email: ", "Not exists")
                }

                Pref(context!!).save_facebook_credentials(facebookName, facebookEmail, facebookProfilePicURL)

                val tokensp= context!!.getsp("token","").toString()

                if (tokensp.isNotBlank() || tokensp.isNotEmpty())
                {

                    val service= context?.let { Constants.getApiService(it) }
                    val call=service!!.loginViaThirdParty("$f_userId","$f_token","facebook","")
                    call.enqueue(object : Callback<Third_party_login_model>
                    {
                        override fun onResponse(
                            call: Call<Third_party_login_model>,
                            response: Response<Third_party_login_model>
                        ) {
                            if(response.isSuccessful) {
                                val body_response = response.body()!!.success
                                if (body_response) {
                                    val result:Third_party_login_model= response.body()!!
                                    val item=result.item
                                    go_to_homme()

                                } else {
                                    toast(response.body().toString())
                                    csp().edit().clear().apply()

//                                        pbr_otp.visibility=View.GONE
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                }
                            }else{
                                toast(response.body().toString())
                                csp().edit().clear().apply()

//                                    pbr_otp.visibility=View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }

                        override fun onFailure(call: Call<Third_party_login_model>, t: Throwable) {
                            toast(t.toString())
                            csp().edit().clear().apply()

//                                pbr_otp.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }

                    })

                }



            }).executeAsync()



    }

    private fun goToOtpActivity() {
        pbr.visibility=View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


        val intent=Intent(this, OtpActivity::class.java)
        intent.putExtra("sentcode",code_sent)
        intent.putExtra("complete_no",complete_no)
        startActivity(intent)
        finish()
    }



    private fun gotoHome() {
        toast("Login Success")
        val intent = Intent (this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
//        if (mBackPressed + Constants.TIME_INTERVAL > System.currentTimeMillis()) {
//            super.onBackPressed()
//            return
//        } else {
//            toast(resources.getString(R.string.press_again_to_exit))
//            mBackPressed = System.currentTimeMillis()
//
//        }
        super.onBackPressed()
        finish()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {

            //Google Sign In code
            if (requestCode.equals(101)) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSighnResult(task)
            }else
            {
                callbackManager.onActivityResult(requestCode, resultCode, data)

            }



        } catch (e: Exception) {
            toast(e.toString())


        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleGoogleSighnResult(task: Task<GoogleSignInAccount>?) {
//        Constants.create_app_level_token(this)

        val account = task?.getResult(ApiException::class.java)
        val g_id = account?.id
        val g_token = account?.zaa() // facebook obfuscated  code
        val name = account?.displayName
        val email = account?.email
        val photo = account?.photoUrl.toString()
        Pref(context!!).save_google_credentials(name!!, email!!, photo)
        //get token
        val token= context!!.getsp("token","").toString()

        if (token.isNotBlank() || token.isNotEmpty())
        {

            val service= context?.let { Constants.getApiService(it) }
            val call=service!!.loginViaThirdParty("$g_id","$g_token","google","")
            call.enqueue(object : Callback<Third_party_login_model>
            {
                override fun onResponse(
                    call: Call<Third_party_login_model>,
                    response: Response<Third_party_login_model>
                ) {
                    if(response.isSuccessful) {
                        val body_response = response.body()!!.success
                        if (body_response) {
                            val result:Third_party_login_model= response.body()!!
                            val item=result.item
                            go_to_homme()
                        } else {
                            toast(response.errorBody().toString())
                            csp().edit().clear().apply()

//                                        pbr_otp.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }else{
                        toast(response.errorBody().toString())
                        csp().edit().clear().apply()

//                                    pbr_otp.visibility=View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }

                override fun onFailure(call: Call<Third_party_login_model>, t: Throwable) {
                    toast(t.toString())
                    csp().edit().clear().apply()

//                                pbr_otp.visibility=View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            })

        }


    }


}
