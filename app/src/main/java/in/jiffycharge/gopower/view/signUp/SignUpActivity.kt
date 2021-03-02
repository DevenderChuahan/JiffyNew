package `in`.jiffycharge.gopower.view.signUp

import `in`.jiffycharge.gopower.*
import `in`.jiffycharge.gopower.databinding.ActivitySignUpBinding
import `in`.jiffycharge.gopower.model.Third_party_login_model
import `in`.jiffycharge.gopower.model.signup_mobile_model
import `in`.jiffycharge.gopower.utils.*
import `in`.jiffycharge.gopower.utils.Constants.Companion.okHttpClient
import `in`.jiffycharge.gopower.view.home.HomeActivity
import `in`.jiffycharge.gopower.view.login.LoginActivity
import `in`.jiffycharge.gopower.view.otp.OtpActivity
import `in`.jiffycharge.gopower.viewmodel.SignUpActivityViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.dd.processbutton.iml.ActionProcessButton
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
import kotlinx.android.synthetic.main.activity_login.bt_facebook
import kotlinx.android.synthetic.main.activity_login.bt_google
import kotlinx.android.synthetic.main.activity_login.ccp
import kotlinx.android.synthetic.main.activity_login.et_enter_no
import kotlinx.android.synthetic.main.activity_login.pbr
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.jetbrains.anko.async
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    val signUpViewModel by viewModel<SignUpActivityViewModel>()
    protected val smsHelper by lazy { SmsHelper.create() }

    var country_code: String? = null
    var country_code_without_plus: String? = null
    var contact_no: String? = null
    var password: String? = null
    var context: Context? = null
    var complete_no: String? = null
    var code_sent: String? = null
    lateinit var callbackManager: CallbackManager

    lateinit var token_tracker: AccessTokenTracker
    lateinit var profile_tracker: ProfileTracker

    private var mBackPressed: Long = 0

    //google sign in
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var popupwindow: PopupWindow? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        DataBindingUtil.setContentView<ActivitySignUpBinding>(this, R.layout.activity_sign_up)
            .apply {
                this.lifecycleOwner = this@SignUpActivity
                this.signUpVmodel = signUpViewModel
            }
//        setContentView(R.layout.activity_sign_up)
//        Constants.initOkHttp(this)

        lifecycle.addObserver(smsHelper)

        btn_sign_up.setOnClickListener {

            country_code = ccp.selectedCountryCodeWithPlus.trim()
            country_code_without_plus = ccp.selectedCountryCode.trim()
            contact_no = et_enter_no.text.toString().trim()
            password = et_new_password.text.toString().trim()
            complete_no = country_code + contact_no
            if (contact_no.isNullOrEmpty()) {
                et_enter_no.setError("Phone number is required")
                et_enter_no.requestFocus()
                return@setOnClickListener

//                val intent=Intent(this, OtpActivity::class.java)
//                startActivity(intent)
//                finish()
            } else if (contact_no!!.length < 10) {
                et_enter_no.setError("Please enter a valid number")
                et_enter_no.requestFocus()
                return@setOnClickListener


            } else if (password!!.length < 6) {
                et_new_password.setError("Password format is incorrect, the length is not less than 6")
                et_new_password.requestFocus()
                return@setOnClickListener


            } else {
                val check = check_terms_and_conditions()
                if (check) {
                    pbr.visibility = View.GONE

                    btn_sign_up.setMode(ActionProcessButton.Mode.ENDLESS)
                    btn_sign_up.progress = 1
                    btn_sign_up.text = "Loading..."


                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    et_enter_no.clearFocus()
                    et_new_password.clearFocus()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_enter_no.windowToken, 0)

                    val imm2 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm2.hideSoftInputFromWindow(et_new_password.windowToken, 0)


                    smsHelper.send(country_code_without_plus, contact_no)
                    {

                        runOnUiThread {
                            if (it) {
                                //Verification code sent successfully

                                pbr.visibility = View.GONE
                                btn_sign_up.progress = 100

                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                                goToOtpActivity()


                            } else {
                                //Failed to send verification code

                                toast("Verification code failed to send")
                                btn_sign_up.progress = 0
                                btn_sign_up.text = "SIGN UP"

                                pbr.visibility = View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                            }
                        }


                    }

//                    create_token_using_moobile_no("$country_code_without_plus-$contact_no", password!!, GrantType.CLIENT_CREDENTIALS)
//                    { throwable: Throwable?, token ->
//                        val accessToken = token?.accessToken!!.trim()
//                        Log.e("OTP Access Oken", accessToken)
//
//                        runOnUiThread {
//                            if (accessToken!==null) {
//                                context!!.setsp("token",accessToken)
//
//
//
//
////                                signUpViewModel.signUpRepository.get_otp(country_code_without_plus!!,contact_no!!)
////                                signUpViewModel.signUpRepository.response_message.observe(this,
////                                    androidx.lifecycle.Observer {
////                                        if (it.equals("200"))
////                                        {
////
////
////
////                                            signUpViewModel.signUpRepository.otp_data.observe(this, androidx.lifecycle.Observer {
////
////                                                if (it.success)
////                                                {
////                                                    pbr.visibility= View.GONE
////                                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
////
////                                                    goToOtpActivity()
////
////                                                }else
////                                                {
////                                                    toast("Verification code failed to send")
////                                                    pbr.visibility = View.GONE
////                                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
////                                                         csp().edit().clear().apply()
////
////                                                }
////
////                                            })
////
////                                        }else
////                                        {
////                                            toast(it.toString())
////                                            pbr.visibility = View.GONE
////                                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
////                                            csp().edit().clear().apply()
////
////
////                                        }
////
////
////                                    }
////                                )
//
//                            }
//                        }
//
//                    }


                } else {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_enter_no.windowToken, 0)

                    val imm2 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                    imm2.hideSoftInputFromWindow(et_new_password.windowToken, 0)


                    toast("Please check terms and conditions")
                    checkbox.requestFocus()

                }
            }


            //


        }

        btn_already_user.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()

        }
        tv_agreement.setOnClickListener {


            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.agreement_layout, null)
            popupwindow = PopupWindow(
                view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            popupwindow?.isFocusable = true
            popupwindow?.update()

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupwindow!!.elevation = 10.0F
            }
            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupwindow!!.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.END
                popupwindow!!.exitTransition = slideOut

            }

            //show popup on screen
            TransitionManager.beginDelayedTransition(top_root)

            top_root.post {
                popupwindow!!.showAtLocation(
                    top_root, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
                )

            }


            val mywebview = view.findViewById<WebView>(R.id.mywebview)

//call webview


            // Get the web view settings instance
            val settings = mywebview.settings
//
            // Enable java script in web view
            settings.javaScriptEnabled = true
            val map_header = HashMap<String, String>()
            map_header.put("Accept-Language", "en")

//
            mywebview.loadUrl(Constants.agreement_url, map_header)
            mywebview.webViewClient = object : WebViewClient() {


                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    mywebview.loadUrl(Constants.agreement_url, map_header)
                    return false
                }


                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }


                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }


                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }


            }


        }

//        bt_google.setOnClickListener {
//            val check = check_terms_and_conditions()
//            if (check) {
//
//                //create token
////                Constants.create_app_level_token(this)
//
//                ////Google  init/////
//                val gso =
//                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestEmail()
//                        .build()
//                mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//
//                val intent = mGoogleSignInClient.signInIntent
//                startActivityForResult(intent, 101)
//
//            }else
//
//            {
//
//                toast("Please check terms and conditions")
//                checkbox.requestFocus()
//            }
//
//        }
//
//        bt_facebook.setOnClickListener {
//            val check = check_terms_and_conditions()
//            if (check) {
//                //create token
//                Constants.create_app_level_token(this)
//                callbackManager = CallbackManager.Factory.create()
//                LoginManager.getInstance()
//                    .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
//
//                LoginManager.getInstance()
//                    .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
//                        override fun onSuccess(loginResult: LoginResult?) {
//                            getUserProfile(
//                                loginResult?.accessToken,
//                                loginResult?.accessToken?.userId
//                            )
//
////                    gotoHome()
//
//                        }
//
//                        override fun onCancel() {
//                            toast("Facebook Cancel Login")
//                            csp().edit().clear().apply()
//
//
//                        }
//
//                        override fun onError(error: FacebookException?) {
//                            toast(error.toString())
//                            csp().edit().clear().apply()
//
//
//
//                        }
//
//
//                    })
//
//            }
//                else
//            {
//
//                    toast("Please check terms and conditions")
//                    checkbox.requestFocus()
//                }
//
//
//        }


//        object : AccessTokenTracker()
//        {
//            override fun onCurrentAccessTokenChanged(
//                oldAccessToken: AccessToken?,
//                currentAccessToken: AccessToken?
//            ) {
//                if (currentAccessToken!=null)
//                {
////                    loadFacebookProfile(currentAccessToken)
//
//                }
//
//
//
//
//            }
//
//
//        }


    }

    private fun check_terms_and_conditions(): Boolean {
        return checkbox.isChecked


    }

//    private fun mobile_sign_up_api() {
////        context!!.setsp("token","2f480081-04fe-4625-9bd7-931ceefe2a65")
//
//        val service= context?.let { Constants.getApiService(it) }
//        val call=service!!.signUp(country_code_without_plus,contact_no,"",password )
//        call.enqueue(object : Callback<signup_mobile_model>
//        {
//            override fun onResponse(
//                call: Call<signup_mobile_model>,
//                response: Response<signup_mobile_model>
//            ) {
//                if(response.isSuccessful) {
//                    val body_response = response.body()!!.success
//                    if (body_response) {
////                        go_to_homme()
//                    } else {
//                        toast(response.errorBody().toString())
//                        pbr_otp.visibility=View.GONE
//                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                    }
//                }else{
//                    toast(response.errorBody().toString())
//                    pbr_otp.visibility=View.GONE
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                }
//            }
//
//            override fun onFailure(call: Call<signup_mobile_model>, t: Throwable) {
//                toast(t.toString())
//                pbr_otp.visibility=View.GONE
//                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//            }
//
//        })
//
//    }

//    fun create_token_using_moobile_no(
//        phone: String,
//        psw: String,
//        grantType: GrantType,
//        result: (t: Throwable?, token: OAuthJSONAccessTokenResponse?) -> Unit
//    ) {
//
//        try {
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
//                val client = OAuthClient(OAuthOkHttpClient(okHttpClient))
//                val accessTokenResponse = client.accessToken(request)
//                val APP_TOKEN = accessTokenResponse?.accessToken ?: ""
//                result.invoke(null, accessTokenResponse)
//                client.shutdown()
//            }
//
//        }catch (e:Exception)
//        {
//            e.printStackTrace()
//            pbr_otp.visibility=View.GONE
//
//
//        }
//
//
//
//        }


//    @SuppressLint("LongLogTag")
//    private fun getUserProfile(f_token: AccessToken?, f_userId: String?) {
//        var facebookName=""
//        var facebookEmail=""
//        var facebookProfilePicURL=""
//
//        val parameters = Bundle()
//        parameters.putString(
//            "fields",
//            "id, first_name, middle_name, last_name, name, picture, email"
//        )
//        GraphRequest(f_token,
//            "/$f_userId/",
//            parameters,
//            HttpMethod.GET,
//            GraphRequest.Callback { response ->
//                val jsonObject = response.jsonObject
//
//                // Facebook Access Token
//                // You can see Access Token only in Debug mode.
//                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
////                if (BuildConfig.DEBUG) {
////                    FacebookSdk.setIsDebugEnabled(true)
////                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
////                }
//
//                // Facebook Id
//                if (jsonObject.has("id")) {
//                    val facebookId = jsonObject.getString("id")
//                    Log.i("Facebook Id: ", facebookId.toString())
//                } else {
//                    Log.i("Facebook Id: ", "Not exists")
//                }
//
//
//                // Facebook First Name
//                if (jsonObject.has("first_name")) {
//                    val facebookFirstName = jsonObject.getString("first_name")
//                    Log.i("Facebook First Name: ", facebookFirstName)
//                } else {
//                    Log.i("Facebook First Name: ", "Not exists")
//                }
//
//
//                // Facebook Middle Name
//                if (jsonObject.has("middle_name")) {
//                    val facebookMiddleName = jsonObject.getString("middle_name")
//                    Log.i("Facebook Middle Name: ", facebookMiddleName)
//                } else {
//                    Log.i("Facebook Middle Name: ", "Not exists")
//                }
//
//
//                // Facebook Last Name
//                if (jsonObject.has("last_name")) {
//                    val facebookLastName = jsonObject.getString("last_name")
//                    Log.i("Facebook Last Name: ", facebookLastName)
//                } else {
//                    Log.i("Facebook Last Name: ", "Not exists")
//                }
//
//
//                // Facebook Name
//                if (jsonObject.has("name")) {
//                    facebookName = jsonObject.getString("name")
//                    Log.i("Facebook Name: ", facebookName)
//                } else {
//                    Log.i("Facebook Name: ", "Not exists")
//                }
//
//
//                // Facebook Profile Pic URL
//                if (jsonObject.has("picture")) {
//                    val facebookPictureObject = jsonObject.getJSONObject("picture")
//                    if (facebookPictureObject.has("data")) {
//                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
//                        if (facebookDataObject.has("url")) {
//                            facebookProfilePicURL = facebookDataObject.getString("url")
//                            Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
//                        }
//                    }
//                } else {
//                    Log.i("Facebook Profile Pic URL: ", "Not exists")
//                }
//
//                // Facebook Email
//                if (jsonObject.has("email")) {
//                    facebookEmail = jsonObject.getString("email")
//                    Log.i("Facebook Email: ", facebookEmail)
//                } else {
//                    Log.i("Facebook Email: ", "Not exists")
//                }
//
//                Pref(context!!).save_facebook_credentials(facebookName!!, facebookEmail!!, facebookProfilePicURL)
//
//                val tokensp= context!!.getsp("token","").toString()
//
//                if (tokensp.isNotBlank() || tokensp.isNotEmpty())
//                {
//
//                    val service= context?.let { Constants.getApiService(it) }
//                    val call=service!!.loginViaThirdParty("$f_userId","$f_token","facebook","")
//                    call.enqueue(object : Callback<Third_party_login_model>
//                    {
//                        override fun onResponse(
//                            call: Call<Third_party_login_model>,
//                            response: Response<Third_party_login_model>
//                        ) {
//                            if(response.isSuccessful) {
//                                val body_response = response.body()!!.success
//                                if (body_response) {
//                                    val result:Third_party_login_model= response.body()!!
//                                    val item=result.item
//                                    go_to_home()
//
//                                } else {
//                                    toast(response.body().toString())
////                                        pbr_otp.visibility=View.GONE
//                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                                }
//                            }else{
//                                toast(response.body().toString())
////                                    pbr_otp.visibility=View.GONE
//                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                            }
//                        }
//
//                        override fun onFailure(call: Call<Third_party_login_model>, t: Throwable) {
//                            toast(t.toString())
////                                pbr_otp.visibility=View.GONE
//                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                        }
//
//                    })
//
//                }
//
//
//            }).executeAsync()
//
//
//
//    }

    private fun goToOtpActivity() {


        val intent = Intent(this, OtpActivity::class.java)
//        intent.putExtra("sentcode",code_sent)
//        intent.putExtra("complete_no",complete_no)
        intent.putExtra("password", password)
//        intent.putExtra("country_code",country_code)
        intent.putExtra("contact_no", contact_no)
        intent.putExtra("country_code_without_plus", country_code_without_plus)
        startActivity(intent)
        finish()
    }


    private fun gotoHome() {
        toast("Login Success")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(smsHelper)
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
            } else {
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
        val token = context!!.getsp("token", "").toString()

        if (token.isNotBlank() || token.isNotEmpty()) {

            val service = context?.let { Constants.getApiService(it) }
            val call = service!!.loginViaThirdParty("$g_id", "$g_token", "google", "")
            call.enqueue(object : Callback<Third_party_login_model> {
                override fun onResponse(
                    call: Call<Third_party_login_model>,
                    response: Response<Third_party_login_model>
                ) {
                    if (response.isSuccessful) {
                        val body_response = response.body()!!.success
                        if (body_response) {
                            val result: Third_party_login_model = response.body()!!
                            val item = result.item
                            go_to_home()

                        } else {
                            toast(response.errorBody().toString())
                            csp().edit().clear().apply()

//                                        pbr_otp.visibility=View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    } else {
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

    private fun go_to_home() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()

    }

}