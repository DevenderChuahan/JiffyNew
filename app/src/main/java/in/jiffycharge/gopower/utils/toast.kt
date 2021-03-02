package `in`.jiffycharge.gopower.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun Context.toast(message:String)
{
    Toast.makeText(this,message,Toast.LENGTH_SHORT).apply {

        this.setGravity(Gravity.CENTER,0,0)

    }.show()




}