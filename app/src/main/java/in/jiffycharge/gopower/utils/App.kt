package `in`.jiffycharge.gopower.utils

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.di.*
import `in`.jiffycharge.gopower.payment.PayTypeEnum
import `in`.jiffycharge.gopower.payment.Payutils
import `in`.jiffycharge.gopower.repository.MapRepository
import android.app.Application
import com.mob.MobSDK
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(netModule)
            modules(retrofitServiceModule)
            modules(orderViewModel)
            modules(order_repo_module)
            modules(wallet_view_model)
            modules(wallet_repository)
            modules(HomeViewModel)
            modules(Home_repository)
            modules(MapViewModel)
            modules(Map_repository)
            modules(SignUpViewModel)
            modules(SignUpRepo)
            modules(WalletPayRepo)
            modules(walletpaymodel)

        }

        //SMS verification
        MobSDK.init(this, getString(R.string.mob_app_key), getString(R.string.mob_app_secret))

        Payutils.initSupportPayType(this, PayTypeEnum.cashfree)

    }


}