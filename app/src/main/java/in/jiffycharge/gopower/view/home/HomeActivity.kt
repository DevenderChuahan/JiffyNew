package `in`.jiffycharge.gopower.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.csp
import `in`.jiffycharge.gopower.databinding.ActivityHomeBinding
import `in`.jiffycharge.gopower.utils.Constants
import `in`.jiffycharge.gopower.utils.Pref
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.coupons.CouponsActivity
import `in`.jiffycharge.gopower.view.login.LoginActivity
import `in`.jiffycharge.gopower.view.map.Home_Interface
import `in`.jiffycharge.gopower.view.map.MapFragment
import `in`.jiffycharge.gopower.view.orders.OrderFragment
import `in`.jiffycharge.gopower.view.orders.ViewOrderFragment
import `in`.jiffycharge.gopower.view.profile.ProfileFragment
import `in`.jiffycharge.gopower.view.subscriptions.SubscriptionActivity
import `in`.jiffycharge.gopower.view.wallet.WalletFragment
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), Home_Interface {

    val home_view_model by viewModel<HomeActivityViewModel>()

    var context: Context? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var account: GoogleSignInAccount? = null
    var access_token: AccessToken? = null
    val HOME_TAG="HOME_TAG"
    private var mBackPressed: Long = 0




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//        val model = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
            .apply {
                this.setLifecycleOwner(this@HomeActivity)
                this.homeviewmodel = home_view_model

            }

        home_view_model.home_interface = this

        context = this
        load_map_Fragment()
//        updateLocation()




        handle_phone__google_and_facebook_signIn()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_home ->
                {
                    val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
                    if (frag is MapFragment  )
                    {
                        return@setOnNavigationItemSelectedListener true
                    }

                    ll_top.visibility=View.VISIBLE
                    load_map_Fragment()
                    return@setOnNavigationItemSelectedListener  true
                }
                R.id.nav_wallet->{
                    val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
                    if (frag is WalletFragment  )
                    {
                        return@setOnNavigationItemSelectedListener true
                    }

                    ll_top.visibility=View.GONE
                    load_Fragment(WalletFragment())
                    return@setOnNavigationItemSelectedListener  true

                }
                R.id.nav_profile->{

                    val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
                    if (frag is ProfileFragment  )
                    {
                        return@setOnNavigationItemSelectedListener true
                    }
                    ll_top.visibility=View.GONE
                    load_Fragment(ProfileFragment())
                    return@setOnNavigationItemSelectedListener  true

                }
                R.id.nav_my_orders->{
                    val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
                    if (frag is OrderFragment  )
                    {
                        return@setOnNavigationItemSelectedListener true
                    }
                    ll_top.visibility=View.GONE
                    load_Fragment(OrderFragment())
//                    startActivity(Intent(this,OrderFragment::class.java))
                    return@setOnNavigationItemSelectedListener  true

                }


                else ->false
            }


        }


    }

    private fun load_map_Fragment() {
        ll_top.visibility=View.VISIBLE
        bottom_navigation.menu.getItem(0).setChecked(true)


        val transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container,MapFragment())
        transaction.commit()




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

    }

    override fun onPause() {
        super.onPause()

    }

    fun open_ViewOrder(order_code: String)
    {
        Log.v("Home_code",order_code)

        ll_top.visibility=View.GONE

        val bundle=Bundle()
        bundle.putString("order_code",order_code)
        val VOFragment=ViewOrderFragment()
        VOFragment.arguments=bundle


        val transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container,VOFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onBackPressed() {

        var count=supportFragmentManager.backStackEntryCount

        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (count>0) {
            for (i in 0..count) {
                if (frag is ViewOrderFragment  )
                {

                    super.onBackPressed()

                    break


                }
                else if(i==count)
                {
//                    load_map_Fragment()
                    ll_top.visibility=View.VISIBLE
                    break
//
                }
                else

                {
                    supportFragmentManager.popBackStack()

                    bottom_navigation.menu.getItem(0).setChecked(true)

                }

            }
        }else

        {
            if (mBackPressed + Constants.TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            toast(resources.getString(R.string.press_again_to_exit))
            mBackPressed = System.currentTimeMillis()

        }

//            super.onBackPressed()

        }





//        if (frag is ViewOrderFragment)
//        {
//
//
//            val transaction =supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fl_container,OrderFragment())
//            transaction.addToBackStack(null)
//            transaction.commit()
//
//        }else
//        {
//            if( count==0)
//            {
//                super.onBackPressed()
//
//            }else
//            {
//                supportFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                load_map_Fragment()
//
//
//            }
//        }


    }




    private fun handle_phone__google_and_facebook_signIn() {

        txt_usename.setText("")
        txt_contact.setText("")
        img_profile.setImageURI(null)



                    home_view_model.repo._data.observe(this, Observer {profile_data->
                        this.runOnUiThread {
                            when(profile_data.status)
                            {

                                Resourse.Status.SUCCESS->
                                {
                                    txt_usename.setText(profile_data.data!!.item.nickname)
//                                    tv_email.setText("")
                                    txt_contact.setText(profile_data.data.item.mobile)
                                    img_profile.setImageURI(null)

                                    if(profile_data.data.item.isVIP)
                                    {
                                        tv_gold_user.visibility=View.VISIBLE
                                    }else
                                    {
                                        tv_gold_user.visibility=View.GONE

                                    }
                                    if(!profile_data.data.item.headImgPath.isNullOrBlank())
                                    {
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





//        //check google loggin init
//        account = GoogleSignIn.getLastSignedInAccount(this)
//        val gso =
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
////facbok login init
//        access_token = AccessToken.getCurrentAccessToken()
//
//
//        //set firebse phone_auth login user data
//        if(FirebaseAuth.getInstance().currentUser!=null)
//        {
//
//            txt_usename.setText("")
//            txt_contact.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
//            img_profile.setImageURI(null)
//        }
//        else if (account != null) {
//            //set google login user data
//
//            val googl_login_list = Pref(context as HomeActivity)
//                .get_google_credentilas()
//
//            txt_usename.setText(googl_login_list.get("username"))
//            txt_contact.setText(googl_login_list.get("email"))
//            img_profile.setImageURI(null)
//            Picasso.with(this).load(Uri.parse(googl_login_list.get("photo"))).into(img_profile)
//
//        }
//        else if (access_token != null && !access_token!!.isExpired)
//        {
//            val profile = Profile.getCurrentProfile()
////            if (profile != null) {
//                val facebook_login_list = Pref(context as HomeActivity)
//                    .get_facebook_credentilas()
//
//                img_profile.setImageURI(null)
//                txt_usename.setText(facebook_login_list.get("username"))
//                txt_contact.setText(facebook_login_list.get("email"))
//
//                Picasso.with(this).load(facebook_login_list.get("photo")).into(img_profile)
//
//                //set facebook user data
////            setFacebookData()
////            } else {
////                LoginManager.getInstance().logOut()
////                startActivity(Intent(this,LoginActivity::class.java))
////                finish()
////            }
//
//        }


    }

    override fun log_out() {
        csp().edit().clear().apply()

        if (account != null) {

            csp().edit().clear().apply()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googlesigninclient = GoogleSignIn.getClient(context as HomeActivity, gso)
            googlesigninclient.signOut()
            Pref(context as HomeActivity).clear_google_login_pref()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else if (access_token != null && !access_token!!.isExpired) {
            csp().edit().clear().apply()

            LoginManager.getInstance().logOut()
            Pref(context as HomeActivity).clear_facebook_login_pref()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }else
        {
            csp().edit().clear().apply()

//            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()


        }

    }

    override fun open_drawer() {
//        drawer_layout.openDrawer(GravityCompat.START)
//
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }


    }

    override fun open_profile() {

        open_drawer()
        bottom_navigation.menu.getItem(2).setChecked(true)
        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (frag is ProfileFragment  )
        {
            return
        }

        load_Fragment(ProfileFragment())

    }

    override fun go_to_Home() {
        open_drawer()
        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (frag is MapFragment  )
        {
            return
        }
        load_map_Fragment()
    }

    override fun go_to_subscription() {
//        open_drawer()

        startActivity(Intent(this,SubscriptionActivity::class.java))

    }

    override fun go_to_my_orders() {
        open_drawer()
        ll_top.visibility=View.GONE
        bottom_navigation.menu.getItem(3).setChecked(true)

        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (frag is OrderFragment  )
        {
            return
        }

        load_Fragment(OrderFragment())

    }

    override fun go_to_wallet() {
        open_drawer()
        bottom_navigation.menu.getItem(1).setChecked(true)
        val frag=supportFragmentManager.findFragmentById(R.id.fl_container)
        if (frag is WalletFragment  )
        {
            return
        }

        load_Fragment(WalletFragment())


    }

    override fun go_to_coupons() {
//        open_drawer()

        startActivity(Intent(this,CouponsActivity::class.java))

    }

    override fun go_to_support() {
        open_drawer()

    }



//    private fun setFacebookData() {
//
//
//        val request = GraphRequest.newMeRequest(
//            AccessToken.getCurrentAccessToken(),
//            object : GraphRequest.GraphJSONObjectCallback {
//                override fun onCompleted(
//                    `object`: JSONObject?,
//                    response: GraphResponse?
//                ) {
//                    val id = `object`?.getString("id")
//                    val email = `object`?.getString("email")
//                    val first_name = `object`?.getString("first_name")
//                    val last_name = `object`?.getString("last_name")
//                    val gender = `object`?.getString("gender")
//                    val birthday = `object`?.getString("birthday")
//                    val imaage_url =
//                        "http://graph.facebook.com/$id/picture?type=large"
//
//
//                }
//
//
//            })
//
//
//
//    }





//    fun update_Map(
//        lat: Double,
//        logitude: Double,
//        location: Location
//    ) {
//
//        val marker= MarkerOptions()
//
//        val zoomLevel = 15f
//
//        val homeLatLng = LatLng(lat, logitude)
//        marker.position(homeLatLng)
////         marker.draggable(true)
//        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        mMap.addMarker(marker).title="Start"
//
//
//        var camerapos= CameraPosition.Builder()
//            .target(homeLatLng).zoom(15F).bearing(location.bearing).tilt(0F).build()
//        var camupdate= CameraUpdateFactory.newCameraPosition(camerapos)
//        mMap.animateCamera(camupdate)
//        val polyline=mMap.addPolyline(
//            PolylineOptions()
//                .add(homeLatLng)
//                .add(LatLng(31.149504, 76.970644)))
//        polyline.endCap= RoundCap()
//        polyline.width= 5F
//        polyline.color= Color.BLUE
//        polyline.jointType= JointType.ROUND
//
//    }




}
