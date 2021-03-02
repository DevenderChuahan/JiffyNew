package `in`.jiffycharge.gopower.view.coupons

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.ContentXXX
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ViewUtils
import androidx.lifecycle.Observer
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.android.synthetic.main.activity_coupons.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CouponsActivity : AppCompatActivity() {
    val couponList=ArrayList<ContentXXX>()
    var context:Context?=null
    val home_view_model by viewModel<HomeActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)
        context=this
        val intent=intent
        btn_apply.isClickable=false
//        if (intent!=null)
//        {
//            val json=intent.getStringExtra("couponList")
//            if(json!=null) {
//                val token: TypeToken<List<ItemXXXXXXXXX?>?> =
//                    object : TypeToken<List<ItemXXXXXXXXX?>?>() {}
//                val couponList: List<ItemXXXXXXXXX> = Gson().fromJson(json, token.type)
//            }
//
//
//        }
        getCouponList()





    }

    private fun getCouponList() {
        home_view_model.repo.getMemberCouponList()
        home_view_model.repo.membercouponListResult.observe(this, Observer {
            runOnUiThread {


                when(it.status)
                {
                    Resourse.Status.LOADING -> {



                    }

                    Resourse.Status.SUCCESS->
                    {
                     val list=   it.data?.content
                        if (list.isNullOrEmpty()) {

//                            context?.toast("No available offers")
                            ll_no_coupon.visibility=View.VISIBLE
                            coupon_loader.visibility=View.GONE


                        }else{
                            coupon_loader.visibility=View.GONE

                            couponList.addAll( it.data?.content)
                            rv_coupon.adapter= CouponsAdapter(context!!,couponList)


                        }




                    }

                    Resourse.Status.ERROR -> {


                    }




                }



            }


        })





    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(context)

    }


    fun coupon_onBack(view: View) {
        onBackPressed()

    }
}
