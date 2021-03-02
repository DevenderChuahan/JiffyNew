package `in`.jiffycharge.gopower.view.subscriptions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_subscription.*
import `in`.jiffycharge.gopower.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo

class SubscriptionActivity : AppCompatActivity() {
    var context:Context?=null
    val map_list=HashMap<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        context=this

        rv_subscription.adapter= SubscriptionAdapter(context!!,map_list)

        subscription_back.setOnClickListener {
            onBackPressed()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(context)
    }
}
