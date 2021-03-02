package `in`.jiffycharge.gopower.view.home

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.csp
import `in`.jiffycharge.gopower.databinding.ActivityHomeBinding
import `in`.jiffycharge.gopower.utils.Constants
import `in`.jiffycharge.gopower.utils.Pref
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.coupons.CouponsActivity
import `in`.jiffycharge.gopower.view.feedback.FeedbackActivity
import `in`.jiffycharge.gopower.view.login.LoginActivity
import `in`.jiffycharge.gopower.view.map.Home_Interface
import `in`.jiffycharge.gopower.view.map.MapFragment
import `in`.jiffycharge.gopower.view.orders.OrderFragment
import `in`.jiffycharge.gopower.view.orders.ViewOrderFragment
import `in`.jiffycharge.gopower.view.power.SelectPowerActivity
import `in`.jiffycharge.gopower.view.profile.ProfileFragment
import `in`.jiffycharge.gopower.view.subscriptions.SubscriptionActivity
import `in`.jiffycharge.gopower.view.support.FreshChatUtil
import `in`.jiffycharge.gopower.view.unlock.UnLockActivity
import `in`.jiffycharge.gopower.view.wallet.WalletFragment
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.transition.FragmentTransitionSupport
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.razorpay.Checkout
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.home_bottom_sheet.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), Home_Interface {
    val fm=supportFragmentManager

    val mapfragment:MapFragment=MapFragment()
    val wallepFragment=WalletFragment()
    val profileFragment=ProfileFragment()
    val orderFragment=OrderFragment()

     var activeFragment:Fragment=MapFragment()



    val mapview=mapfragment.view

    val home_view_model by viewModel<HomeActivityViewModel>()
    private val needPermissionCode = 1452
    private var needPermissionCall: Function1<Boolean, Unit>? = null
    var context: Context? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var account: GoogleSignInAccount? = null
    var access_token: AccessToken? = null
    val HOME_TAG="HOME_TAG"
    private var mBackPressed: Long = 0
    var qrcode:String?=null

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)

        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
            .apply {
                this.setLifecycleOwner(this@HomeActivity)
                this.homeviewmodel = home_view_model

            }

        home_view_model.home_interface = this

        context = this
        handle_phone__google_and_facebook_signIn()

        val frag=fm.findFragmentById(R.id.fl_container)

        activeFragment=mapfragment
        fm.beginTransaction().add(R.id.fl_container,wallepFragment,"4").hide(wallepFragment).commit()
        fm.beginTransaction().add(R.id.fl_container,profileFragment,"3").hide(profileFragment).commit()
        fm.beginTransaction().add(R.id.fl_container,orderFragment,"2").hide(orderFragment).commit()
        fm.beginTransaction().add(R.id.fl_container,mapfragment,"1").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
//        updateLocation()

//        home_view_model.repo.cabinetResult.observe(this, Observer {
//            this.runOnUiThread {
//                when(it.status)
//                {
//                    Resourse.Status.SUCCESS->
//                    {
//                        try {
//                            val intent = Intent(context, SelectPowerActivity::class.java)
//                            intent.putExtra("qrcode", qrcode)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                            startActivity(intent)
//
//                        }catch (e:Exception)
//                        {
//                            Log.v("qrexception",e.toString())
//                        }
//
//
//
//
//                    }
//                    Resourse.Status.ERROR->
//                    {
////                                    toast(it.data?.error_description?:return@runOnUiThread)
//                        toast(it.exception?:return@runOnUiThread)
//
//
//
//
//
//                    }
//
//
//                    else ->{
//
//                    }
//                }
//
//
//
//
//            }
//
//
//
//
//        })


        home_view_model.repo.cabinetResult.observe(this, Observer {
            this.runOnUiThread {
                when(it.status)
                {
                    Resourse.Status.SUCCESS->
                    {
                        try {
                            home_bottom_sheet2.visibility = View.VISIBLE

                            val bottomSheetBehavior=BottomSheetBehavior.from(home_bottom_sheet2)
                            bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
//                            if (bottomSheetBehavior.state==BottomSheetBehavior.STATE_HIDDEN) {
//                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                            }
                            tv_name.text=it.data!!.item.seller.sellerName
                            tv_rate.text= Html.fromHtml(it.data.item.chargeRuleDesc.replace("<s>", "<font color=\"#FF4200\">")
                                .replace("</s>", "</font>"))
                            tv_phone_no.text=it.data.item.seller.contactMobile
                            val count=it.data.item.batteryType4Count
//                            mapview?.findViewById<Button>(R.id.btn_lend)?.isEnabled=count>0
                            btn_lend.isEnabled=count>0

                            tv_type.text="$count".plus(" ").plus("available")

                            btn_lend.setOnClickListener {

                                            val intent= Intent(this, UnLockActivity::class.java)
                                            intent.putExtra("qrcode",qrcode)
                                            startActivity(intent)


//                                toast("borrowClicked")

                            }





                        }catch (e:Exception)
                        {
                            Log.v("qrexception",e.toString())
                            home_bottom_sheet2.visibility = View.GONE

                        }

                    }
                    Resourse.Status.ERROR-> {

                        toast(it.exception.toString())
                        home_bottom_sheet2.visibility = View.GONE
                    }
//


                    else ->
                    {

                    }
                }


            }





        })

        home_view_model.repo._data.observe(this, Observer {profile_data->
            this.runOnUiThread {
                when(profile_data.status)
                {

                    Resourse.Status.SUCCESS->
                    {
                        txt_usename.setText(profile_data.data!!.item.nickname)
//                                    tv_email.setText("")
                        txt_contact.setText(profile_data.data.item.mobile)

                        if(profile_data.data.item.isVIP)
                        {
                            tv_gold_user.visibility=View.VISIBLE
                        }else
                        {
                            tv_gold_user.visibility=View.GONE

                        }
                        if(!profile_data.data.item.headImgPath.isNullOrBlank())
                        {
                            img_profile.setImageURI(null)
                            Log.v("profilepic","true")

//                                Picasso.with(context).load(Uri.parse(profile_data.item.headImgPath)).into(img_profile_pic)
                            Glide.with(this).load(profile_data.data.item.headImgPath).apply(
                                RequestOptions.bitmapTransform(CircleCrop()).diskCacheStrategy(
                                    DiskCacheStrategy.ALL)
                            ).into(img_profile)


                        }
                    }
                    Resourse.Status.ERROR->
                    {
                        txt_usename.setText("N/A")
                        txt_contact.setText("N/A")
                        img_profile.setImageURI(null)
                        tv_gold_user.visibility=View.GONE


                    }



                }




            }
        })

        home_view_model.repo._data.observe(this, Observer {
        this.runOnUiThread {
            when(it.status)
            {

                Resourse.Status.SUCCESS->{
                    it.data?.item?.apply {
                        FreshChatUtil.setUser(this)


                    }


                }

                Resourse.Status.ERROR->
                {

                }



                else->
                {

                }
            }





        }





    })

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {

                R.id.nav_home ->
                {
//                    if (activeFragment is MapFragment  )
//                    {
//                        return@setOnNavigationItemSelectedListener true
//                    }





                    ll_top.visibility=View.VISIBLE
                    bottom_navigation.menu.getItem(0).setChecked(true)

                    fm.beginTransaction().hide(activeFragment).show(mapfragment).commit()
                    activeFragment=mapfragment

                    return@setOnNavigationItemSelectedListener  true
                }
                R.id.nav_wallet->{
//                    if (activeFragment is WalletFragment  )
//                    {
//                        return@setOnNavigationItemSelectedListener true
//                    }

                    ll_top.visibility=View.GONE
                    fm.beginTransaction().hide(activeFragment).show(wallepFragment).commit()
                    activeFragment=wallepFragment
                    return@setOnNavigationItemSelectedListener  true

                }
                R.id.nav_profile->{
//                    if (activeFragment is ProfileFragment  )
//                    {
//                        return@setOnNavigationItemSelectedListener true
//                    }
                    ll_top.visibility=View.GONE

                    fm.beginTransaction().hide(activeFragment).show(profileFragment).commit()
                    activeFragment=profileFragment
                    return@setOnNavigationItemSelectedListener  true

                }
                R.id.nav_my_orders->{
//                    if (activeFragment is OrderFragment  )
//                    {
//                        return@setOnNavigationItemSelectedListener true
//                    }
                    ll_top.visibility=View.GONE
                    fm.beginTransaction().hide(activeFragment).show(orderFragment).commit()
                    activeFragment=orderFragment

                    return@setOnNavigationItemSelectedListener  true

                }


                else ->false
            }


        }

        iv_nav_drawer.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)

            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }

        }

        val firstMillis = System.currentTimeMillis() // alarm is set right away

//
//        val intent=Intent(context,JobService::class.java)
//        val penintent=PendingIntent.getService(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
//        val alarm=getSystemService(Context.ALARM_SERVICE)as AlarmManager
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP,firstMillis,30000,penintent)





    }


    private fun load_Fragment(fragment: Fragment) {
        ll_top.visibility=View.GONE


        val transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()




    }

    override fun onResume() {
        super.onResume()
        home_view_model.repo.fetchUserprofile()
        
    }


    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
//        if(myService!=null)
//        {
//            val intent=Intent(this,MyService::class.java)
//            this.stopService(intent)
//
//        }



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: String) {
        if (!event.isNullOrBlank()) {

            if (event.equals("photoUploded"))
            {
                home_view_model.repo.fetchUserprofile()


            }

            if(event.equals("qrcode"))
            {
                toast("Something went wrong, please try again")


            }

        }


    }

     fun open_ViewOrder(order_code: String)
    {
        Log.v("Home_code",order_code)

        ll_top.visibility=View.GONE
        val VOFragment=ViewOrderFragment()


        val bundle=Bundle()
        bundle.putString("order_code",order_code)
        VOFragment.arguments=bundle


        fm.beginTransaction().add(R.id.fl_container,VOFragment,"5").show(VOFragment).commit()

//        fm.beginTransaction().hide(activeFragment).show(VOFragment).commit()
        activeFragment=VOFragment

//        val transaction =supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fl_container,VOFragment)
//        transaction.addToBackStack(null)
//        transaction.commit()

    }

    override fun onBackPressed() {
//        var count=supportFragmentManager.backStackEntryCount
//        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
//        if (count>0) {
//            for (i in 0..count) {
//                if (frag is ViewOrderFragment  )
//                {
//                    super.onBackPressed()
//                    break
//
//                }
//                else if(i==count)
//                {
//                    Log.v("fragment","$frag $count")
//
//                    ll_top.visibility=View.VISIBLE
//                    break
//                }
//                else
//                {
//                    supportFragmentManager.popBackStack()
//                    bottom_navigation.menu.getItem(0).isChecked = true
//
//                }
//
//            }
//        }else
//
//        {
//            if (mBackPressed + Constants.TIME_INTERVAL > System.currentTimeMillis()) {
//            super.onBackPressed()
//            return
//        } else {
//            toast(resources.getString(R.string.press_again_to_exit))
//            mBackPressed = System.currentTimeMillis()
//
//        }
//
//        }


//        val frag=fm.findFragmentById(R.id.fl_container)

        if (activeFragment is ViewOrderFragment  )
                {

                    ll_top.visibility=View.GONE
                    fm.beginTransaction().hide(activeFragment).show(orderFragment).commit()
                    activeFragment=orderFragment

                }

        else if (activeFragment!=mapfragment) {
            ll_top.visibility=View.VISIBLE
            bottom_navigation.menu.getItem(0).setChecked(true)

            fm.beginTransaction().hide(activeFragment).show(mapfragment).commit()
            activeFragment=mapfragment

        }else

        {
            if (mBackPressed + Constants.TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            toast(resources.getString(R.string.press_again_to_exit))
            mBackPressed = System.currentTimeMillis()

        }

        }






    }

    private fun handle_phone__google_and_facebook_signIn() {
        home_view_model.repo.fetchUserprofile()
//        EventBus.getDefault().post("getProfile")


    }

    override fun log_out() {
        csp().edit().clear().apply()

        if (account != null) {

            csp().edit().clear().apply()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googlesigninclient = GoogleSignIn.getClient(context as HomeActivity, gso)
            googlesigninclient.signOut()
            Pref(context as HomeActivity).clear_google_login_pref()
            // erase customer Razorpay payment  data
            Checkout.clearUserData(this)



            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else if (access_token != null && !access_token!!.isExpired) {
            csp().edit().clear().apply()

            LoginManager.getInstance().logOut()
            Pref(context as HomeActivity).clear_facebook_login_pref()

            // erase customer Razorpay payment  data
            Checkout.clearUserData(this)




            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }else
        {
            csp().edit().clear().apply()
            // erase customer Razorpay payment  data
            Checkout.clearUserData(this)



            startActivity(Intent(this, LoginActivity::class.java))
            finish()


        }

    }

    override fun open_drawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }


    }

    override fun open_profile() {
        open_drawer()
        bottom_navigation.menu.getItem(2).setChecked(true)
        val frag=fm.findFragmentById(R.id.fl_container)
        if (frag is ProfileFragment  )
        {
            return
        }

        ll_top.visibility=View.GONE

        fm.beginTransaction().hide(activeFragment).show(profileFragment).commit()
        activeFragment=profileFragment

    }

    override fun go_to_Home() {
        open_drawer()
        ll_top.visibility=View.VISIBLE

        bottom_navigation.menu.getItem(0).setChecked(true)
        val frag=fm.findFragmentById(R.id.fl_container)

//        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (activeFragment is MapFragment  )
        {
            return
        }

        fm.beginTransaction().hide(activeFragment).show(mapfragment).commit()
        activeFragment=mapfragment
    }

    override fun go_to_subscription() {
        startActivity(Intent(this,SubscriptionActivity::class.java))
        Animatoo.animateCard(context)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Scan QR code / bar code
        if (requestCode == MapFragment.SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                 qrcode = data.getStringExtra(Constant.CODED_CONTENT)
//                 qrcode = "0000501"

                if (qrcode!=null) {
                    home_view_model.repo.getCabinet(qrcode!!)

                }


            }
        }
    }
    override fun go_to_my_orders() {
        open_drawer()
        ll_top.visibility=View.GONE
        bottom_navigation.menu.getItem(3).setChecked(true)
        val frag=fm.findFragmentById(R.id.fl_container)

        if (frag is OrderFragment  )
        {
            return
        }
        ll_top.visibility=View.GONE

        fm.beginTransaction().hide(activeFragment).show(orderFragment).commit()
        activeFragment=orderFragment
    }

    override fun go_to_wallet() {
        open_drawer()
        bottom_navigation.menu.getItem(1).setChecked(true)
        val frag=fm.findFragmentById(R.id.fl_container)

        if (frag is WalletFragment  )
        {
            return
        }
        ll_top.visibility=View.GONE
        fm.beginTransaction().hide(activeFragment).show(wallepFragment).commit()
        activeFragment=wallepFragment

    }

    override fun go_to_coupons() {
        startActivity(Intent(this,CouponsActivity::class.java))
        Animatoo.animateCard(context)


    }

    override fun go_to_support() {
        FreshChatUtil.start(this)
        Animatoo.animateCard(context)



    }

    override fun goToFeedback() {
        val intent=Intent(context, FeedbackActivity::class.java)
        startActivity(intent)
        Animatoo.animateCard(context)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == needPermissionCode) {
            var isGetAllPermission = true
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    isGetAllPermission = false
                }
            }
            needPermissionCall?.invoke(isGetAllPermission)
        }
    }

    fun getPermission(permission: MutableList<String>, needPermissionCall: (Boolean) -> Unit) {

        var isNeedRequest = false
        val list = mutableListOf<String>()
        permission.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                isNeedRequest = true
                list.add(it)
            }
        }
        if (isNeedRequest) {
            this.needPermissionCall = needPermissionCall
            if (list.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, list.toTypedArray(), needPermissionCode)
            } else {
                needPermissionCall.invoke(true)
            }
        } else {
            needPermissionCall.invoke(true)
        }

    }

     fun openScan()
    {

            val intent = Intent(context, CaptureActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            val config = ZxingConfig()
            config.reactColor = R.color.colorAccent
            config.isShowAlbum = false
            config.isShowFlashLight = false
            config.scanLineColor = R.color.colorAccent
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
            startActivityForResult(intent, MapFragment.SCAN_REQUEST_CODE)
        btn_scan.visibility=View.GONE


    }

}
