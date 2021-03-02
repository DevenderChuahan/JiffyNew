package `in`.jiffycharge.gopower.di

import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.getsp
import `in`.jiffycharge.gopower.repository.*
import `in`.jiffycharge.gopower.utils.Constants.Companion.base_url
import `in`.jiffycharge.gopower.viewmodel.*
import android.app.Application
import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val netModule = module {

    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())

    }

    fun provideOntercepter(context:Context):Interceptor {

        return AuthInterceptor(context)
    }

    fun provideHttpClient(cache: Cache, intercepter:Interceptor): OkHttpClient {
        val okhttpclientBuilder = OkHttpClient.Builder().cache(cache).addInterceptor(intercepter)
            okhttpclientBuilder.retryOnConnectionFailure(true)
        okhttpclientBuilder.connectTimeout(30,TimeUnit.SECONDS)
        okhttpclientBuilder.readTimeout(30,TimeUnit.SECONDS)
        return okhttpclientBuilder.build()


    }


    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }

    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.jiffycharge.in/api/")
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single { provideCache(androidApplication()) }
    single { provideOntercepter(androidContext()) }
    single { provideHttpClient(get(),get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }


}
val order_repo_module= module {
    factory { OrderRepository(get()) }


}
val wallet_repository= module {

    factory { WalletRepository(get()) }
}

val wallet_view_model= module {
    factory { WalletViewModel(get()) }

}


val retrofitServiceModule= module {
    fun provideApiService(retrofit: Retrofit):ApiInterface
    {
        return retrofit.create(ApiInterface::class.java)
    }
    single { provideApiService(get()) }
}

val orderViewModel= module {
    viewModel { Orders_view_model(get()) }

}

val HomeViewModel= module {
    viewModel { HomeActivityViewModel(get()) }

}
val Home_repository= module {

    factory { HomeRepository(get()) }


}
val MapViewModel= module {
    viewModel { MapFragmentViewModel(get()) }

}
val Map_repository= module {

    factory { MapRepository(get()) }
}

val SignUpViewModel= module {
    viewModel { SignUpActivityViewModel(get()) }
}

val SignUpRepo= module {
    factory { SignUpRepository(get()) }
}
val WalletPayRepo= module {
    factory { WalletPayRepository(get()) }
}
val walletpaymodel= module {
    viewModel { WalletPayViewModel(get()) }

}



class AuthInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        context.getsp("token","").let {
//            val credentials = Credentials.basic("ODYtMTg2ODIzMTAwMTU=", "MTIzNDU2")

//            requestBuilder.addHeader("Authorization", "$credentials ")
            requestBuilder.addHeader("Authorization", "Bearer $it")
                .addHeader("Connection","close")

                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json;versions=1")

        }

        return chain.proceed(requestBuilder.build())
    }
}
