package `in`.jiffycharge.gopower.view.coupons

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXX
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_coupons.*

class CouponsActivity : AppCompatActivity() {
    val map_list=HashMap<String,String>()
    var context:Context?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)
        context=this
        val intent=intent
        if (intent!=null)
        {
            val json=intent.getStringExtra("couponList")
            val token: TypeToken<List<ItemXXXXXXXXX?>?> = object : TypeToken<List<ItemXXXXXXXXX?>?>() {}
            val couponList: List<ItemXXXXXXXXX> = Gson().fromJson(json, token.type)


        }


        rv_coupon.adapter= CouponsAdapter(context!!,map_list)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun coupon_onBack(view: View) {
        onBackPressed()

    }
}
