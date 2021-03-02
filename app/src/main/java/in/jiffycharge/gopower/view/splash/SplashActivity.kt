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
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.signUp.SignUpActivity
import `in`.jiffycharge.gopower.view.unlock.UnLockActivity
import `in`.jiffycharge.gopower.viewmodel.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException

class SplashActivity : AppCompatActivity() {
    //init all view models
    val home_view_model by viewModel<HomeActivityViewModel>()
    private val MapViewModel by viewModel<MapFragmentViewModel>()
    private  val order_view_Model by viewModel<Orders_view_model>()
    val signUpViewModel by viewModel<SignUpActivityViewModel>()
    private val Wallet_Pay_View_Model by viewModel<WalletPayViewModel>()
    private val Wallet_View_Model by viewModel<WalletViewModel>()


    //google sign in
//        lateinit var mGoogleSignInClient: GoogleSignInClient
//    lateinit var callbackManager: CallbackManager
//
//    lateinit var  token_tracker: AccessTokenTracker
//    lateinit var profile_tracker: ProfileTracker

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun eventBus(event: Any) {
        if (event is HttpException )
        {

            toast(event.toString())
            return

        }
     else if (event is Throwable )
        {
            toast(event.toString())
            return

        }
        else if (event.equals("modulefinfished"))
            {
                goToView()


            }




    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        //Init all repository
        home_view_model.repo
        MapViewModel.mapRepository


    }

    private fun goToView() {

        val  token= getsp("token","")


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


        },3000)
    }
}
