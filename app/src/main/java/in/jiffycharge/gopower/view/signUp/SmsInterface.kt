package `in`.jiffycharge.gopower.view.signUp

interface SmsInterface:NoParamsLifecycleObserver {

    fun send(country: String?, phone: String?, sendCall: (Boolean) -> Unit)

    fun verification(
        country: String?,
        phone: String?,
        code: String?,
        verificationCall: (Boolean) -> Unit
    )
}