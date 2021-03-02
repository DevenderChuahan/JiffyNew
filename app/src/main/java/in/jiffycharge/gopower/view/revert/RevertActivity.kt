package `in`.jiffycharge.gopower.view.revert

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.ItemXXXXXXXX
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXX
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.getCountTimeByLong
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.charge.ChargeActivity
import `in`.jiffycharge.gopower.view.coupons.CouponsActivity
import `in`.jiffycharge.gopower.view.deposit.DepositActivity
import `in`.jiffycharge.gopower.view.home.HomeActivity
import `in`.jiffycharge.gopower.view.orders.ViewOrderFragment
import `in`.jiffycharge.gopower.view.wallet.WallerPayBean
import `in`.jiffycharge.gopower.view.wallet.WallerPayMark
import `in`.jiffycharge.gopower.view.walletpay.WalletPay
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_revert.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RevertActivity : AppCompatActivity() {

    val home_view_model by viewModel<HomeActivityViewModel>()
    var context: Context? = null
    private var isOnlyBalancePay = false
    private  val payFragment:WalletPay
        get() = walletpay_fragment as WalletPay


    private var needPay: Double = 0.0
    var apiOrderBO: ItemXXXXXXXX? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revert)
        context = this


        val intent = intent
        if (intent != null) {
            if (intent.extras?.get("apiOrderBO") is ItemXXXXXXXX) {
                apiOrderBO = intent.extras?.get("apiOrderBO") as ItemXXXXXXXX?
                setData(apiOrderBO as ItemXXXXXXXX)
            } else if (intent.extras?.get("apiOrderBO") is ItemXXXXXXXXX) {
                setCouponChangeUi(apiOrderBO as ItemXXXXXXXXX)

            }

        }
        revert_back.setOnClickListener {
          onBackPressed()
        }

//        home_view_model.repo.payDeposit("cashfree", PayResultActivity.RETURN_URL_DEPOSIT)
//        home_view_model.repo._datapayment.observe(this,
//            androidx.lifecycle.Observer {
//                when (it.status) {
//                    Resourse.Status.LOADING -> {
//
//                    }
//                    Resourse.Status.SUCCESS -> {
//                        if (it.data!!.item!=null)
//                        {
//                            PayResultActivity(this).checkPayResult(
//                                it.data!!
//                            )
//                            {
//                                if (it) {
//                                    finishAfterTransition()
//
//
//                                }
//
//                            }
//                        }
//
//
//
//
//                    }
//                    Resourse.Status.ERROR -> {
////                        toast(it.data?.error_description.toString())
//
//
//                    }
//
//
//                }
//
//
//            })


//        getcoupponListandAppSetData()

        getcoupponListandAppSetData()

        payBt.setOnClickListener {
            startPay()

        }
        coupns_tv.setOnClickListener {
            toCouponListPage()

        }
        orderdetails.setOnClickListener {
//             HomeActivity().open_ViewOrder(apiOrderBO!!.orderCode)
           val code= apiOrderBO?.orderCode?:return@setOnClickListener

            val bundle = Bundle()
            bundle.putString("order_code", code)

            val VOFragment = ViewOrderFragment()
            VOFragment.arguments = bundle


            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fl_container,VOFragment)
            transaction.replace(R.id.rl_walletfrag, VOFragment)
            transaction.addToBackStack(null)
            transaction.commit()


        }



    }

    private fun toCouponListPage() {
        home_view_model.repo.getCouponList()
        home_view_model.repo.couponListResult.observe(this, androidx.lifecycle.Observer {
            runOnUiThread {

                when (it.status) {

                    Resourse.Status.SUCCESS -> {
                        val couponList = it.data!!.items
                        if (couponList.isNullOrEmpty()) {

                            context?.toast("No available offers")


                        } else {

                            val intent = Intent(context, CouponsActivity::class.java)
                            val couponlistAsjson = Gson().toJson(couponList)


                            intent.putExtra("couponList", couponlistAsjson)

                            intent.flags =
                                Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)


                        }


                    }


                }


            }


        })
    }

    private fun startPay() {
        val payBean = payFragment?.getWallerPayBean()

        if (isOnlyBalancePay) {
            payBean!!.mark = WallerPayMark.BALANCE
        }
        when (payBean!!.mark) {
            WallerPayMark.BALANCE -> {
                paymentUsingPost(apiOrderBO!!.orderCode, apiOrderBO!!.userCouponId, payBean)


            }
            WallerPayMark.DEFAULT -> {
                payByNoBalance(payBean)
            }
        }
    }

    private fun paymentUsingPost(orderCode: String, userCouponId: Long, payBean: WallerPayBean) {

        home_view_model.repo.getPatmentDetails(orderCode, userCouponId, payBean)
        home_view_model.repo.paymetDetails_list_data.observe(this, Observer {
            runOnUiThread {
                when (it.status) {
                    Resourse.Status.SUCCESS -> {
//                        it.data?.item?:toast(it.data?.error_description?:return@runOnUiThread)
                        if(it.data?.item !=null)
                        {
                            finishAfterTransition()

                        }else {

                            toast(it.data?.error_description ?: return@runOnUiThread)
                        }
                    }
                    Resourse.Status.ERROR -> {
                        Log.v("DCWalltetPAyError",it.data?.error_description?:"nothing")
                        Log.v("DCWalltetPAyError",it.data?.error?:"nono")

                        this.toast(it.data?.error_description?:return@runOnUiThread)

                    }
//                        Log.v("DCWalltetPAyError",it.data?.error_description?:"nothing")

//                    }

                }


            }

        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(context)

    }

    private fun payByNoBalance(payBean: WallerPayBean) {
        home_view_model.repo.getSystemSet()
        home_view_model.repo.systemsetResult.observe(this, Observer {
            runOnUiThread {

                when (it.status) {
                    Resourse.Status.SUCCESS -> {
                        val isOnlyBalance = it.data?.item?.isOpenBalance?:true
                        if (isOnlyBalance) {
//                            home_view_model.repo.fetchUserprofile()
                            home_view_model.repo._data.observe(this, Observer {
                                when (it.status) {

                                    Resourse.Status.SUCCESS -> {
                                        val balance = it.data?.item?.balance?:0.0
                                        if (needPay > balance) {
//                                            startActivity(Intent(this, ChargeActivity::class.java))

                                            val intent = Intent(context, ChargeActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                        } else {
                                            payBean.mark = WallerPayMark.BALANCE
                                            paymentUsingPost(
                                                apiOrderBO!!.orderCode,
                                                apiOrderBO!!.userCouponId,
                                                payBean)
                                        }


                                    }


                                }


                            })


                        } else {
                            payBean.mark = WallerPayMark.CASHFREE

                            paymentUsingPost(
                                apiOrderBO!!.orderCode,
                                apiOrderBO!!.userCouponId,
                                payBean
                            )


                        }


                    }


                }


            }


        })


    }

    @SuppressLint("SetTextI18n")
    private fun setCouponChangeUi(event: ItemXXXXXXXXX) {
        apiOrderBO!!.userCouponId = event.id.toLong()
        val couponAmount = "${event.amount} ${event.currency}"
        coupns_tv.text = "-$couponAmount"
        coupns_tv.setTextColor(ContextCompat.getColor(this, R.color.red))
        needPay = apiOrderBO!!.payPrice - event.amount
        if (needPay <= 0) {
            isOnlyBalancePay = true
            needPay = 0.0
        } else {
            isOnlyBalancePay = false
        }

//        needPayTv.text = "Pay${needPay}${event.currency}"
        needPayTv.text = "${needPay}"
        couponTv.text = "Coupons\n${couponAmount}"


    }

    @SuppressLint("SetTextI18n")
    private fun setData(apiOrderBO: ItemXXXXXXXX) {
        val usetime = apiOrderBO.finishTime - apiOrderBO.beginTime
        orderTv.text = apiOrderBO.orderCode
        totalFeeTv.text = "${apiOrderBO.payPrice}${apiOrderBO.currency}"
        tv_duration.text= getCountTimeByLong(usetime, true)

        needPay = apiOrderBO.payPrice
//        needPayTv.text = "Pay${apiOrderBO.payPrice}${apiOrderBO.currency}"
        needPayTv.text = "${apiOrderBO.payPrice}"
        couponTv.text = "Apply Coupon\n0${apiOrderBO.currency}"

        isOnlyBalancePay = !(apiOrderBO.payPrice != null && apiOrderBO.payPrice > 0)


    }

    override fun onResume() {
        super.onResume()
//        getcoupponListandAppSetData()
    }

    private fun getcoupponListandAppSetData() {

        //Coupon Api
        home_view_model.repo.getCouponList()
        home_view_model.repo.couponListResult.observe(this, androidx.lifecycle.Observer {
            runOnUiThread {

                when (it.status) {
                    Resourse.Status.LOADING -> {

                    }

                    Resourse.Status.SUCCESS -> {
                        val item = it.data?.items?:return@runOnUiThread
                        if (item.isEmpty()) {
                            coupns_tv.text = "no offer available"

                        } else {

                            coupns_tv.text = "${item.count()}"


                        }


                    }
                    Resourse.Status.ERROR -> {
                        coupns_tv.text = "no offer available"

                    }


                }


            }


        })


        //systemset api
//        home_view_model.repo.getSystemSet()
        home_view_model.repo.systemsetResult.observe(this, androidx.lifecycle.Observer {

            runOnUiThread {

                when (it.status) {
                    Resourse.Status.LOADING -> {

                    }

                    Resourse.Status.SUCCESS -> {


                    }
                    Resourse.Status.ERROR -> {
                        toast(it.data!!.error_description)

                    }


                }

            }


        })


    }
}