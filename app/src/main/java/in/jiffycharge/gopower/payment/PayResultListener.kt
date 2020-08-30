package `in`.jiffycharge.gopower.payment

interface PayResultListener {
    fun onSuccess(result:Any?=null)
    fun onFail(error:String?){}
    fun onCancel(){}
}