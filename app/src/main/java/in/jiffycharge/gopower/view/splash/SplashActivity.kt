package `in`.jiffycharge.gopower.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import `in`.jiffycharge.gopower.view.home.HomeActivity
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.facebook.ProfileTracker
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.getsp
import `in`.jiffycharge.gopower.view.signUp.SignUpActivity
import `in`.jiffycharge.gopower.view.unlock.UnLockActivity

class SplashActivity : AppCompatActivity() {
    //google sign in
        lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var callbackManager: CallbackManager

    lateinit var  token_tracker: AccessTokenTracker
    lateinit var profile_tracker: ProfileTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        val token= getsp("token","")
        //init google sign in Account
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
//        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

//        //init facebook
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"))
//        callbackManager= CallbackManager.Factory.create()
        //Facebook Token
        val access_token= AccessToken.getCurrentAccessToken()
////check token
//        token_tracker=object :AccessTokenTracker()
//        {
//            override fun onCurrentAccessTokenChanged(
//                oldAccessToken: AccessToken?,
//                currentAccessToken: AccessToken?
//            ) {
//
//
//
//
//            }
//
//        }
//
//        //strat tracking
//        token_tracker.startTracking()



//        //check profile
//        profile_tracker=object : ProfileTracker()
//        {
//            override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
//
//                if (currentProfile!=null)
//                {
//                    val intent=Intent(this@SplashActivity,HomeActivity::class.java)
//                    startActivity(intent)
//
//                }
//            }
//
//        }
//
//        //check face book token and Tracker
//        token_tracker=object : AccessTokenTracker()
//        {
//            override fun onCurrentAccessTokenChanged(
//                oldAccessToken: AccessToken?,
//                currentAccessToken: AccessToken?
//            ) {
//
//
//
//
//            }
//
//        }
//
//        //strat tracking
//        profile_tracker.startTracking()
//        token_tracker.startTracking()


        Handler().postDelayed({

            if (!token!!.equals(""))
            {


                val intent=Intent(this@SplashActivity, HomeActivity::class.java)
//                val intent=Intent(this@SplashActivity, UnLockActivity::class.java)
                startActivity(intent)
                finish()
            }else
            {
                startActivity( Intent(this, SignUpActivity::class.java))
                finish()

            }
//            //check first Facebook with google
//            if(FirebaseAuth.getInstance().currentUser!=null)
//            {
//                val intent=Intent(this@SplashActivity, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//            else if (access_token!=null && !access_token.isExpired)
//            {
//                val intent=Intent(this@SplashActivity, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//
//            }else if(account!=null)
//            {
//                //Google Loogged In Check////////
//
//
//                val intent=Intent(this@SplashActivity, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//
//            }else
//            {
//                startActivity( Intent(this, SignUpActivity::class.java))
//                finish()}


        },3000)
    }
}
