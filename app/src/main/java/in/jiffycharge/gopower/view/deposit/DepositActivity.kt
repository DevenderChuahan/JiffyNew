package `in`.jiffycharge.gopower.view.deposit

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.payment.PayResultActivity
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_deposit.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DepositActivity : AppCompatActivity() {
    var context: Context? = null
    val home_view_model by viewModel<HomeActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        context = this

        Update_adapter()
        swipeRefreshView.setOnRefreshListener {
            tv_no_data_found.visibility = View.GONE
            rv_deposit.visibility = View.GONE
            Update_adapter()

        }

        iv_back.setOnClickListener {
            onBackPressed()
        }



        home_view_model.repo._data.observe(this, Observer { userprofile ->
            (context as DepositActivity).runOnUiThread {

                home_view_model.repo._datadeposit.observe(this, Observer { deposit_data ->
                    (context as DepositActivity).runOnUiThread {
                        when (deposit_data.status) {
                            Resourse.Status.LOADING -> {

                            }

                            Resourse.Status.SUCCESS -> {
//                                            toast("Deposit Succuess")

                                tv_depoist.text = "${userprofile.data!!.item.deposit}".plus(" ")
                                    .plus(userprofile.data.item.currency)
                                if (userprofile.data.item.deposit > 0.0) {
                                    btn_deposit.text = "Deposit Refund"

                                    //show refund dialog/ api
                                    btn_deposit.setOnClickListener { }


                                } else {
                                    //show  recharge dialog/ api
                                    btn_deposit.text = "Deposit"

                                    btn_deposit.setOnClickListener {

                                        val builder = AlertDialog.Builder(this)
                                        //set title for alert dialog
                                        builder.setTitle("Deposit ")
                                        //set message for alert dialog
                                        builder.setMessage(
                                            "Deposit amount".plus(" ${(deposit_data.data?.item?.deposit)}")
                                                .plus(" ").plus(deposit_data.data?.item?.currency)
                                        )
                                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                                        //performing positive action
                                        builder.setPositiveButton("Pay") { dialog, which ->
                                            dialog.dismiss()

                                            home_view_model.repo.payDeposit(
                                                "cashfree",
                                                PayResultActivity.RETURN_URL_DEPOSIT
                                            )
                                            home_view_model.repo._datapayment.observe(this,
                                                Observer {
                                                    when (it.status) {
                                                        Resourse.Status.LOADING -> {

                                                        }
                                                        Resourse.Status.SUCCESS -> {

                                                            PayResultActivity(this).checkPayResult(
                                                                it.data!!
                                                            )
                                                            {
                                                                Update_adapter()

                                                            }


                                                        }
                                                        Resourse.Status.ERROR -> {
                                                            toast(it.data?.error_description.toString())


                                                        }


                                                    }


                                                })


                                        }
//                                    //performing cancel action
//                                    builder.setNeutralButton("Cancel"){dialog , which ->
//                                        Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
//                                        dialog.dismiss()
//
//                                    }
                                        //performing negative action
                                        builder.setNegativeButton("No") { dialog, which ->
                                            dialog.cancel()
                                        }
                                        // Create the AlertDialog
                                        val alertDialog: AlertDialog = builder.create()
                                        // Set other dialog properties
                                        alertDialog.setCancelable(false)
                                        alertDialog.show()
                                    }

                                }
                            }
                            Resourse.Status.ERROR -> {
                                toast(deposit_data.data?.error_description.toString())

                            }

                        }

                    }
                })

            }


        })


    }

    private fun Update_adapter() {
        //** Set the colors of the Pull To Refresh View
        swipeRefreshView.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        swipeRefreshView.setColorSchemeColors(Color.WHITE)
        swipeRefreshView.isRefreshing = true


        home_view_model.repo.getDepositList()
        home_view_model.repo.get_deposit_list_data.observe(this, Observer {
            when (it.status) {
                Resourse.Status.LOADING -> {

                }
                Resourse.Status.SUCCESS -> {
//                    rv_deposit.visibility=View.VISIBLE
//                    tv_no_data_found.visibility= View.GONE
//
//                    depositadapter= DepositAdapter(context as DepositActivity, it.data!!.content)
//                    rv_deposit.adapter=depositadapter
//                    swipeRefreshView.isRefreshing = false

                    val list = it.data?.content
                    if (list.isNullOrEmpty()) {
                        rv_deposit.visibility = View.GONE
                        tv_no_data_found.visibility = View.VISIBLE
                        swipeRefreshView.isRefreshing = false


                    } else {

                        rv_deposit.visibility = View.VISIBLE
                        tv_no_data_found.visibility = View.GONE
                        swipeRefreshView.isRefreshing = false
                        rv_deposit.adapter = DepositAdapter(context, list)
                    }

                }
                Resourse.Status.ERROR -> {
                    rv_deposit.visibility = View.GONE
                    tv_no_data_found.visibility = View.VISIBLE
                    swipeRefreshView.isRefreshing = false


                }

            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}