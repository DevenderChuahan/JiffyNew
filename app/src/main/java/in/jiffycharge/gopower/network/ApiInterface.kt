package `in`.jiffycharge.gopower.network

import `in`.jiffycharge.gopower.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal

interface ApiInterface {

            // sign_up user using  mobile no
    @POST("member/registered")
    @FormUrlEncoded
    fun signUp(@Field("countryCode") countryCode: String?,
               @Field("mobile") mobile: String?,
               @Field("verifycode") verifycode: String?,
               @Field("password") password: String?
                ):Call<signup_mobile_model>

   // login user using third party
    @POST("member/third_party_login")
    @FormUrlEncoded
    fun loginViaThirdParty(@Field("openId") openId: String?,
                           @Field("accessToken") accessToken: String?,
                           @Field("third_party_type") third_party_type: String?,
                           @Field("clientId") clientId: String?
                ):Call<Third_party_login_model>


 // Order List api
    @POST("order/list")
    @FormUrlEncoded
    suspend fun getOrderList(@Field("ltTime") ltTime: String?):Response<Order_list_model>

// Order details api
    @GET("order/profile")
    suspend fun getOrderDetails(@Query("orderCode") orderCode: String?):Response<Order_Details_model>


// User Balance  api
    @GET("money/user_balance")
    suspend fun getUserBalance():Response<User_balance_model>


// User wallet History  api
    @GET("money/money_log_list")
    suspend fun getWalletHistory():Response<Wallet_history_model>

// User Profile  api
    @GET("member/userprofile")
    suspend fun getUserProfile():Response<UserProfileModel>


// Nearest Shop Location  api
    @GET("seller/find_by_location")
    suspend fun getNearShopLocation(@Query("x") x:BigDecimal, @Query("y") y:BigDecimal, @Query("distance") distance:BigDecimal):Response<Find_Near_Shop_Location_Model>

//get otp from server
@GET("member/send_modile_verifycode")
suspend fun getOtp(@Query("country_code")country_code:String, @Query("phone_number")phone_number:String):Response<OtpModel>

//get deposit
@GET("member/get_deposit")
suspend fun getdeposit():Response<DepositModel>

    //paymet cashefree
    @POST("member/paymet")
    suspend fun paymnetUsingPost(@Query("paymentMark")paymentMark:String,@Query("returnUrl")returnUrl:String):Response<CashfreeModel>

    //Cashfree payment result
    @POST("member/payNotify")
    suspend fun upCashFreePayResult(@Body result:CashFreeVOModel):Response<CashfreeResultResponseModel>

 //Refund Deposit
    @POST("member/refundDeposit")
    suspend fun refundDepositUsingPOST():Response<SimpleResponse>

 // Deposit payment flow
    @GET("member/depositLog")
    suspend fun depositLogUsingGET(@Query("ltTime") ltTime: Int?):Response<DepositLogModel>

// Cabinet profile
    @GET("cabinet/profile")
    suspend fun getCabinetProfile(@Query("qrCode") qrCode: String):Response<Cabinet_Profile_Model>


// Cabinet profile
    @POST("order/create")
    suspend fun getCreateOrder(@Query("qrCode") qrCode: String,@Query("batteryType")batteryType:String):Response<CreateOrderModel>
    // get findDoingInfoUsingGET
    @GET("order/doing_info")
    suspend fun findDoingInfoUsingGET():Response<DoingInfoModel>

    // get not pay
    @GET("order/not_pay")
    suspend fun findNotPayUsingGET():Response<DoingInfoModel>
 // get Coupon lIst
    @POST("order/coupon_list")
    suspend fun findCouponListUsingPOST():Response<CouponListModel>

    // get System Set
    @GET("param/query_sys_set")
    suspend fun querySysSetUsingGET():Response<SystemSetModel>
 // payment using post
    @POST("order/paymet")
    suspend fun paymetUsingPOST(@Query("orderCode")orderCode:String,@Query("cid")cid:Int,
                                @Query("paymentMark")paymentMark:String,
                                @Query("returnUrl")returnUrl:String):Response<PayDetailsModel>

}