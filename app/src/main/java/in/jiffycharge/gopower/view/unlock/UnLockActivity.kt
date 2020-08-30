package `in`.jiffycharge.gopower.view.unlock

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_un_lock.*
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnLockActivity : AppCompatActivity() {
    val home_view_model by viewModel<HomeActivityViewModel>()
    private var qrcode: String? = null
    private var isCanFinish = true

    companion object
    {

        //Maximum polling time
        private const val MAX_LOOP_TIME = 30_000L
        //how often to poll order status
        private const val OFF_LOOP_TIME = 5_000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_lock)
        val intent = intent
        qrcode = intent.getStringExtra("qrcode")
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)


//intit looper

        if (qrcode != null) {
            orderLoopTimer.start()
            animTimer.start()
        }

        if (qrcode != null) {
            //At present, it's only type C, so it's dead here

            home_view_model.repo.createOrder(qrcode!!, "4")

            home_view_model.repo.createOrderResult.observe(this, Observer {
                this.runOnUiThread {
                    when (it.status) {
                        Resourse.Status.LOADING -> {
                            isCanFinish = false


                        }
                        Resourse.Status.SUCCESS -> {
                            unlockSuccess()

                        }
                        Resourse.Status.ERROR -> {
                            isCanFinish = true
                            finish()

                        }


                    }


                }


            })


        }


    }


    private val orderLoopTimer = object : CountDownTimer(MAX_LOOP_TIME, OFF_LOOP_TIME) {
        override fun onFinish() {
            //default order creation failed
            unlockFail()
        }

        override fun onTick(millisUntilFinished: Long) {
            //query order status
            getUsingOrder()
        }
    }

    private val offProgress = 100_000 / MAX_LOOP_TIME.toInt()
    private val animTimer = object : CountDownTimer(MAX_LOOP_TIME, 1000) {
        override fun onFinish() {
        }

        override fun onTick(millisUntilFinished: Long) {
            progress.progress = progress.progress + offProgress
            progressTv.text = "${progress.progress}%"
        }
    }

    private fun getUsingOrder() {
        if (qrcode != null) {
            //At present, it's only type C, so it's dead here

            home_view_model.repo.doingfInfo()

            home_view_model.repo.doingInfoResult.observe(this, Observer {
                this.runOnUiThread {
                    when (it.status) {
                        Resourse.Status.LOADING -> {


                        }
                        Resourse.Status.SUCCESS -> {
                            val apiOrderBO=it.data?.item
                            if (apiOrderBO != null) {
                                unlockSuccess()
                            }

                        }

                        Resourse.Status.ERROR -> {


                        }


                    }


                }


            })


        }


    }





    private fun unlockSuccess() {
        progress.progress = 100
        progressTv.text = "100%"
        isCanFinish = true
        finish()
    }

    private fun unlockFail() {
//        MessageBus.post(ConsMessageCode.UNLOCK_FAIL, qrCode, false, MainActivity::class.java)
//        EventBus.getDefault().post("$qrcode")
        EventBus.getDefault().post("$qrcode")

        finish()
    }


    override fun onBackPressed() {
        if (isCanFinish) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        orderLoopTimer.cancel()
        animTimer.cancel()
        super.onDestroy()
    }
}