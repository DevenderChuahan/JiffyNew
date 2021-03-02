package `in`.jiffycharge.gopower.utils

import android.app.Service
import android.app.job.JobParameters
import android.content.Intent
import android.os.IBinder
import android.util.Log

class JobService:Service() {
    override fun onCreate() {
        super.onCreate()
        toast("Hello DC")
        Log.v("DCSERVICE","DCSTART")
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("Hello DC")
        Log.v("DCSERVICE","DCDESTROY")


    }

    override fun onBind(intent: Intent?): IBinder? {

        return null

    }

}