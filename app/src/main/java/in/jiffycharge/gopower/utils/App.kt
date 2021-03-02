package `in`.jiffycharge.gopower.utils

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.di.*
import `in`.jiffycharge.gopower.payment.PayTypeEnum
import `in`.jiffycharge.gopower.payment.Payutils
import android.app.Application
import android.content.Context
import com.mob.MobSDK
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    companion object
    {
        lateinit var app: Context

    }

    override fun onCreate() {
        super.onCreate()
        app=applicationContext
        startKoin {
            androidContext(this@App)
            modules(netModule)
            modules(retrofitServiceModule)

            modules(Home_repository)
            modules(Map_repository)
            modules(order_repo_module)
            modules(SignUpRepo)
            modules(WalletPayRepo)
            modules(wallet_repository)

            modules(HomeViewModel)
            modules(MapViewModel)
            modules(orderViewModel)
            modules(wallet_view_model)
            modules(SignUpViewModel)
            modules(walletpaymodel)

            EventBus.getDefault().postSticky("modulefinfished")




        }

        //SMS verification
//        MobSDK.init(this, getString(R.string.mob_app_key), getString(R.string.mob_app_secret))

        Payutils.initSupportPayType(this, PayTypeEnum.cashfree)

    }


}