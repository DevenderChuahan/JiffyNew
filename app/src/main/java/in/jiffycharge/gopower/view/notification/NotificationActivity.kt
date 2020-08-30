package `in`.jiffycharge.gopower.view.notification

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_notification.*
import `in`.jiffycharge.gopower.R

class NotificationActivity : AppCompatActivity() {
var context:Context?=null
    val map_list=HashMap<String,String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        context=this
        rv_notifcaton.adapter=NotificationAdapter(context as NotificationActivity,map_list)


    }
}
