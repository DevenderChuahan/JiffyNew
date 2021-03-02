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
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException

class HomeRepository(val api: ApiInterface) {
    val photoData = MutableLiveData<String>()
    val _data = MutableLiveData<Resourse<UserProfileModel>>()
    val feedbackTitleListData = MutableLiveData<Resourse<FeedbacktypelistModel>>()
    val adapteritemlist = MutableLiveData<ItemXXXXXXXXXXXX>()
    val _datadeposit = MutableLiveData<Resourse<DepositModel>>()
    val payamountlist = MutableLiveData<Resourse<PayAmountListModel>>()
    val payamtbalancelist = MutableLiveData<Resourse<CashfreeModel>>()
    val uploadFileresponse = MutableLiveData<Resourse<FileUploadResposeModel>>()
    val headeruploadFileresponse = MutableLiveData<Resourse<SimpleResponse>>()
    val _datapayment = MutableLiveData<Resourse<CashfreeModel>>()
    val voResult = MutableLiveData<Resourse<CashfreeResultResponseModel>>()
    val cabinetResult = MutableLiveData<Resourse<Cabinet_Profile_Model>>()
    val createOrderResult = MutableLiveData<Resourse<CreateOrderModel>>()
    val doingInfoResult = MutableLiveData<Resourse<DoingInfoModel>>()
    val payNotResult = MutableLiveData<Resourse<DoingInfoModel>>()
    val couponListResult = MutableLiveData<Resourse<CouponListModel>>()
    val membercouponListResult = MutableLiveData<Resourse<MemberCouponListModel>>()
    val systemsetResult = MutableLiveData<Resourse<SystemSetModel>>()
    val get_deposit_list_data = MutableLiveData<Resourse<DepositLogModel>>()
    val paymetDetails_list_data = MutableLiveData<Resourse<PayDetailsModel>>()

    val response_message = MutableLiveData<String>()


    init {
        fetchUserprofile()
        doingfInfo()
        getNotPay()
        getCouponList()
        getSystemSet()



        //        getDepositList()
//        getdeposit()


    }

    fun updatefeedback(feedbackvo: FeedbackVO) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.feedbackUsingPOST(feedbackvo)
                withContext(Dispatchers.Main)
                {

                    if (response.isSuccessful && response.body()!!.success) {
                        voResult.postValue(Resourse.success(response.body()) as Resourse<CashfreeResultResponseModel>?)

                    } else {
                        voResult.postValue(Resourse.error(response.body()!!.error_description))


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }


        }

    }

    fun findFeedbackTypeListUsingGET(type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.findFeedbackTypeListUsingGET("en", type)
                withContext(Dispatchers.Main)
                {


                    if (response.isSuccessful && response.body()!!.success) {
                        feedbackTitleListData.postValue(Resourse.success(response.body()) as Resourse<FeedbacktypelistModel>?)

                    } else {
                        feedbackTitleListData.postValue(
                            Resourse.error(
                                response.body()!!.error_description
                            )
                        )


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }




        }

    }

    fun updateUserHeadImage(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.modifyUserHeadUsingGET(url)
                withContext(Dispatchers.Main)
                {


                    if (response.isSuccessful && response.body()!!.success) {
                        headeruploadFileresponse.postValue(Resourse.success(response.body()) as Resourse<SimpleResponse>?)

                    } else {
                        headeruploadFileresponse.postValue(
                            Resourse.error(
                                response.body()!!.error_description
                            )
                        )


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }






        }

    }

    fun uploadFile(fileTypeDesc: String, requestBody: RequestBody) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.uploadFileV2UsingPOST(fileTypeDesc, requestBody)
                withContext(Dispatchers.Main)
                {

                    if (response.isSuccessful && response.body()!!.success) {
                        uploadFileresponse.postValue(Resourse.success(response.body()) as Resourse<FileUploadResposeModel>?)

                    } else {
                        uploadFileresponse.postValue(
                            Resourse.error(
                                response.body()!!.error_description
                            )
                        )


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }





        }

    }

    fun getpaymetBalanceUsingPOST(
        money: Int,
        type: String,
        returnUrlReCharge: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.paymetBalanceUsingPOST(money, type, returnUrlReCharge)
                withContext(Dispatchers.Main)
                {


                    if (response.isSuccessful && response.body()!!.success) {
                        payamtbalancelist.postValue(Resourse.success(response.body()) as Resourse<CashfreeModel>?)

                    } else {
                        payamtbalancelist.postValue(Resourse.error(response.body()!!.error_description))


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }





        }
    }

    fun getPayAmountListUsingGET() {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = api.findPayAmountListUsingGET()
                withContext(Dispatchers.Main)
                {


                    if (response.isSuccessful && response.body()!!.success) {
//                        response_message.postValue(response.code().toString())
                        payamountlist.postValue(Resourse.success(response.body()) as Resourse<PayAmountListModel>?)

                    } else {
                        payamountlist.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }





        }
    }

    fun getdeposit() {
        CoroutineScope(Dispatchers.IO).launch {


            try {
                val response = api.getdeposit()
                withContext(Dispatchers.Main)
                {


                    if (response.isSuccessful && response.body()!!.success) {
//                        response_message.postValue(response.code().toString())
                        _datadeposit.postValue(Resourse.success(response.body()) as Resourse<DepositModel>?)

                    } else {
                        _datadeposit.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                    }


                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }





        }
    }

    fun fetchUserprofile() {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = api.getUserProfile()
                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful && response.body()!!.success) {
//                        response_message.postValue(response.code().toString())
//                        _data.postValue(response.body())
                            _data.postValue(Resourse.success(response.body()) as Resourse<UserProfileModel>?)


                        } else {
//                        response_message.postValue(response.code().toString())
                            _data.postValue(Resourse.error(response.body()!!.error_description))


                        }

                    } catch (e: HttpException) {

                        e.printStackTrace()


                    } catch (e: Throwable) {

                        e.printStackTrace()

                    }
                }
            } catch (e: HttpException) {
                EventBus.getDefault().postSticky(e)

                e.printStackTrace()

            } catch (e: Throwable) {
                EventBus.getDefault().postSticky(e)

                e.printStackTrace()

            }





        }


    }

    fun payDeposit(paymenttype: String, url: String) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = api.paymnetUsingPost(paymenttype, url)
                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful && response.body()!!.success) {

                            _datapayment.postValue(Resourse.success(response.body()) as Resourse<CashfreeModel>?)

                        } else {
                            _datapayment.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }





        }


    }

    fun getVO(resultVo: CashFreeVOModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = api.upCashFreePayResult(resultVo)
                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful && response.body()!!.success) {
                            voResult.postValue(Resourse.success(response.body()) as Resourse<CashfreeResultResponseModel>?)

                        } else {
                            voResult.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }
        } catch (e: HttpException) {
            e.printStackTrace()

        } catch (e: Throwable) {
            e.printStackTrace()

        }

        }


    }

    fun getCabinet(qrcode: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val response = api.getCabinetProfile(qrcode)

                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {

                                cabinetResult.postValue(Resourse.success(response.body()) as Resourse<Cabinet_Profile_Model>?)

                            } else {
                                cabinetResult.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                            }

                        } catch (e: HttpException) {
                            e.printStackTrace()

                        } catch (e: Throwable) {
                            e.printStackTrace()

                        }
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }





            }

        } catch (e: Exception) {
        }


    }

    fun getDepositList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.depositLogUsingGET(null)
                withContext(Dispatchers.Main)
                {
                    try {

                        if (response.isSuccessful && response.body()!!.success) {
                            get_deposit_list_data.postValue(Resourse.success(response.body()) as Resourse<DepositLogModel>?)

                        } else {
                            get_deposit_list_data.postValue(Resourse.error(response.body()!!.error_description)
                            )

//                        response_message.postValue(response.code().toString())


                        }

                    } catch (e: HttpException) {
                        e.printStackTrace()

                    } catch (e: Throwable) {
                        e.printStackTrace()

                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: Throwable) {
                e.printStackTrace()

            }






        }


    }


    fun createOrder(qrcode: String, batteryType: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    createOrderResult.postValue(Resourse.loading())
                    val response = api.getCreateOrder(qrcode, batteryType)
                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                createOrderResult.postValue(Resourse.success(response.body()) as Resourse<CreateOrderModel>?)

                            } else {
                                createOrderResult.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }





            }

        } catch (e: Exception) {
        }


    }

    fun doingfInfo() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val response = api.findDoingInfoUsingGET()

                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                doingInfoResult.postValue(Resourse.success(response.body()) as Resourse<DoingInfoModel>?)

                            } else {
                                doingInfoResult.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }




            }

        } catch (e: Exception) {
        }


    }

    fun getNotPay() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    payNotResult.postValue(Resourse.loading())
                    val response = api.findNotPayUsingGET()
                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                payNotResult.postValue(Resourse.success(response.body()) as Resourse<DoingInfoModel>?)

                            } else {
                                payNotResult.postValue(Resourse.error(response.body()!!.error_description))

//                        response_message.postValue(response.code().toString())


                            }

                        } catch (e: HttpException) {
                            e.printStackTrace()

                        } catch (e: Throwable) {
                            e.printStackTrace()

                        }
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }






            }

        } catch (e: Exception) {
        }


    }

    fun getCouponList() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    couponListResult.postValue(Resourse.loading())

                    val response = api.findCouponListUsingPOST()

                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                couponListResult.postValue(Resourse.success(response.body()) as Resourse<CouponListModel>?)

                            } else {
                                couponListResult.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }





            }

        } catch (e: Exception) {
        }


    }
    fun getMemberCouponList() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    membercouponListResult.postValue(Resourse.loading())

                    val response = api.findMemberCouponListUsingPOST()

                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                membercouponListResult.postValue(Resourse.success(response.body()) as Resourse<MemberCouponListModel>?)

                            } else {
                                membercouponListResult.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }





            }

        } catch (e: Exception) {
        }


    }

    fun getSystemSet() {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    systemsetResult.postValue(Resourse.loading())
                    val response = api.querySysSetUsingGET()
                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
                                systemsetResult.postValue(Resourse.success(response.body()) as Resourse<SystemSetModel>?)

                            } else {
                                systemsetResult.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }





            }

        } catch (e: Exception) {
        }


    }

    fun getPatmentDetails(
        orderCode: String,
        userCouponId: Long,
        payBean: WallerPayBean
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val response = api.paymetUsingPOST(orderCode, payBean.mark.markStr, PayResultActivity.RETURN_URL_PAY_ORDER)
                    withContext(Dispatchers.Main)
                    {
                        try {

                            if (response.isSuccessful && response.body()!!.success) {
//                            Log.v("DCServerS",response.body().toString())

                                paymetDetails_list_data.postValue(Resourse.success(response.body()) as Resourse<PayDetailsModel>?)

                            } else {
//                            Log.v("DCServerE",response.body()?.error?:"emptyError")
                                paymetDetails_list_data.postValue(
                                    Resourse.error(
                                        response.body()!!.error_description
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
                } catch (e: HttpException) {
                    e.printStackTrace()

                } catch (e: Throwable) {
                    e.printStackTrace()

                }




            }

        } catch (e: Exception) {
        }


    }

}