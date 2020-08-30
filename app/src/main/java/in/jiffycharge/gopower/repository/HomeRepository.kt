package `in`.jiffycharge.gopower.repository

import `in`.jiffycharge.gopower.model.*
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.payment.PayResultActivity
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.view.wallet.WallerPayBean
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeRepository(val api: ApiInterface) {
    val _data = MutableLiveData<Resourse<UserProfileModel>>()
    val _datadeposit = MutableLiveData<Resourse<DepositModel>>()
    val _datapayment = MutableLiveData<Resourse<CashfreeModel>>()
    val voResult = MutableLiveData<Resourse<CashfreeResultResponseModel>>()
    val cabinetResult = MutableLiveData<Resourse<Cabinet_Profile_Model>>()
    val createOrderResult = MutableLiveData<Resourse<CreateOrderModel>>()
    val doingInfoResult = MutableLiveData<Resourse<DoingInfoModel>>()
    val payNotResult = MutableLiveData<Resourse<DoingInfoModel>>()
    val couponListResult = MutableLiveData<Resourse<CouponListModel>>()
    val systemsetResult = MutableLiveData<Resourse<SystemSetModel>>()
    val get_deposit_list_data = MutableLiveData<Resourse<DepositLogModel>>()
    val paymetDetails_list_data = MutableLiveData<Resourse<PayDetailsModel>>()

    val response_message = MutableLiveData<String>()


    init {
        fetchUserprofile()
        getdeposit()
    }


    private fun getdeposit() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getdeposit()
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful) {
//                        response_message.postValue(response.code().toString())
                        _datadeposit.postValue(Resourse.success(response.body()) as Resourse<DepositModel>?)

                    } else {
                        _datadeposit.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())


                    }

                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }


        }
    }

    fun fetchUserprofile() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getUserProfile()
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful) {
//                        response_message.postValue(response.code().toString())
//                        _data.postValue(response.body())
                        _data.postValue(Resourse.success(response.body()) as Resourse<UserProfileModel>?)


                    } else {
//                        response_message.postValue(response.code().toString())
                        _data.postValue(Resourse.error(response.errorBody().toString()))


                    }

                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }


        }


    }

    fun payDeposit(paymenttype: String, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.paymnetUsingPost(paymenttype, url)
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful) {

                            _datapayment.postValue(Resourse.success(response.body()) as Resourse<CashfreeModel>?)

                    } else {
                        _datapayment.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())


                    }

                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }


        }


    }

    fun getVO(resultVo: CashFreeVOModel) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.upCashFreePayResult(resultVo)
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful) {
                        voResult.postValue(Resourse.success(response.body()) as Resourse<CashfreeResultResponseModel>?)

                    } else {
                        voResult.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())


                    }

                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }


        }


    }

    fun getCabinet(qrcode: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = api.getCabinetProfile(qrcode)

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            cabinetResult.postValue(Resourse.success(response.body()) as Resourse<Cabinet_Profile_Model>?)

                        } else {
                            cabinetResult.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

    fun getDepositList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.depositLogUsingGET(null)
            withContext(Dispatchers.Main)
            {
                try {

                    if (response.isSuccessful) {
                        get_deposit_list_data.postValue(Resourse.success(response.body()) as Resourse<DepositLogModel>?)

                    } else {
                        get_deposit_list_data.postValue(
                            Resourse.error(
                                response.errorBody().toString()
                            )
                        )

//                        response_message.postValue(response.code().toString())


                    }

                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }
            }


        }


    }


    fun createOrder(qrcode: String, batteryType: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                createOrderResult.postValue(Resourse.loading())

                val response = api.getCreateOrder(qrcode, batteryType)

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            createOrderResult.postValue(Resourse.success(response.body()) as Resourse<CreateOrderModel>?)

                        } else {
                            createOrderResult.postValue(
                                Resourse.error(
                                    response.errorBody().toString()
                                )
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

    fun doingfInfo() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                doingInfoResult.postValue(Resourse.loading())

                val response = api.findDoingInfoUsingGET()

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            doingInfoResult.postValue(Resourse.success(response.body()) as Resourse<DoingInfoModel>?)

                        } else {
                            doingInfoResult.postValue(
                                Resourse.error(
                                    response.errorBody().toString()
                                )
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

    fun getNotPay() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                payNotResult.postValue(Resourse.loading())

                val response = api.findNotPayUsingGET()

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            payNotResult.postValue(Resourse.success(response.body()) as Resourse<DoingInfoModel>?)

                        } else {
                            payNotResult.postValue(Resourse.error(response.errorBody().toString()))

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

    fun getCouponList() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                couponListResult.postValue(Resourse.loading())

                val response = api.findCouponListUsingPOST()

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            couponListResult.postValue(Resourse.success(response.body()) as Resourse<CouponListModel>?)

                        } else {
                            couponListResult.postValue(
                                Resourse.error(
                                    response.errorBody().toString()
                                )
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

    fun getSystemSet() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                systemsetResult.postValue(Resourse.loading())

                val response = api.querySysSetUsingGET()

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            systemsetResult.postValue(Resourse.success(response.body()) as Resourse<SystemSetModel>?)

                        } else {
                            systemsetResult.postValue(
                                Resourse.error(
                                    response.errorBody().toString()
                                )
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }
    fun getPatmentDetails(
        orderCode: String,
        userCouponId: Int,
        payBean: WallerPayBean
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                systemsetResult.postValue(Resourse.loading())

                val response = api.paymetUsingPOST( orderCode, userCouponId,
                    payBean.mark.markStr,
                    PayResultActivity.RETURN_URL_PAY_ORDER)

                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful) {
                            paymetDetails_list_data.postValue(Resourse.success(response.body()) as Resourse<PayDetailsModel>?)

                        } else {
                            paymetDetails_list_data.postValue(
                                Resourse.error(
                                    response.errorBody().toString()
                                )
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }


            }

        } catch (e: Exception) {
        }


    }

}