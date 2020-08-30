package `in`.jiffycharge.gopower.view.power

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.unlock.UnLockActivity
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_power_layout.*
import kotlinx.android.synthetic.main.orders_adapater_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectPowerActivity:AppCompatActivity() {
    val home_view_model by viewModel<HomeActivityViewModel>()

    private var qrcode:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_layout)
        val intent= intent
        qrcode= intent.getStringExtra("qrcode")

        power_back.setOnClickListener {
            onBackPressed()
        }

        btn_lend.setOnClickListener {

            val intent= Intent(this, UnLockActivity::class.java)
            intent.putExtra("qrcode",qrcode)
            startActivity(intent)



        }
        if (qrcode!=null) {
            home_view_model.repo.getCabinet(qrcode!!)

            home_view_model.repo.cabinetResult.observe(this, Observer {
                this.runOnUiThread {
                    when(it.status)
                    {
                        Resourse.Status.SUCCESS->
                        {
                            try {
                                tv_name.text=it.data!!.item.seller.sellerName
                                tv_rate.text=Html.fromHtml(it.data.item.chargeRuleDesc.replace("<s>", "<font color=\"#FF4200\">")
                                    .replace("</s>", "</font>"))
                                tv_phone_no.text=it.data.item.seller.contactMobile
                                val count=it.data.item.batteryType4Count
                                btn_lend.isEnabled=count>0

                                tv_type.text="$count".plus("available")

                            }catch (e:Exception)
                            {
                                Log.v("qrexception",e.toString())
                            }




                        }
                        Resourse.Status.ERROR->
                        {
                            toast(it.data!!.error_description)



                        }




                    }


                }




            })
        }



    }
}