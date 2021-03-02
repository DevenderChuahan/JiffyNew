package `in`.jiffycharge.gopower.utils

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.getsp
import `in`.jiffycharge.gopower.network.ApiInterface
import `in`.jiffycharge.gopower.setsp
import `in`.jiffycharge.gopower.view.home.HomeActivity
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.jetbrains.anko.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Constants {

    companion object
    {
        private lateinit var apiService: ApiInterface

        const val base_url="https://api.jiffycharge.in/api/"
//        const val base_url="https://apistaging.jiffycharge.in/api/"
        const val token_Url="https://oauth.jiffycharge.in/oauth/token"
//        const val token_Url="https://oauthstaging.jiffycharge.in/oauth/token"
        const val agreement_url="http://api.jiffycharge.in/share/use_car_rules"




        const val INTENT_ZXING_CONFIG="zxingConfig"
        const val app_client_Id="3000001"
        const val app_Client_Secret="92aa5479e9b3fc62c4f7faff7d415310"
        const val username="86-18682310015"
        const val password="123456"
        const val TIME_INTERVAL=2000
        lateinit var okHttpClient: OkHttpClient
        lateinit var okHttpClientBuilder: OkHttpClient.Builder


        fun getApiService(context:Context):ApiInterface
        {

//
//            val gson=GsonBuilder().setLenient().create()
            val  retrofit= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//               .addConverterFactory(GsonConverterFactory.create())
//            return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(base_url).client(okhttpClient(context)).build()
//                .baseUrl(base_url).client(okhttpClient(context)).build()
            apiService = retrofit.create(ApiInterface::class.java)

            return apiService

        }

        fun create_app_level_token(context: Context)
        {

            async {

                try {


                    val request = OAuthClientRequest
                        .tokenLocation(Constants.token_Url)
                        .setClientId(Constants.app_client_Id)
                        .setClientSecret(Constants.app_Client_Secret)
                        .setGrantType(GrantType.CLIENT_CREDENTIALS)
                        .buildBodyMessage()
                    val client = OAuthClient(OAuthOkHttpClient(okHttpClient))
                    val accessTokenResponse = client.accessToken(request)
                    val app_token = accessTokenResponse?.accessToken ?: ""
                    context.setsp("token",app_token)


                    client.shutdown()


                } catch (e: Exception) {
                    e.printStackTrace()


                }
            }

        }

        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                // Create a trust manager that does not validate certificate chains

                val x509TrustManager: X509TrustManager =
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                val trustManagers =
                    arrayOf<TrustManager>(x509TrustManager)
                // Install the all-trusting trust manager
                val sslContext: SSLContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustManagers, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory: SSLSocketFactory? = sslContext.socketFactory
                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, x509TrustManager)
                builder.hostnameVerifier { hostname, session -> true }
                return builder
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }

        fun initOkHttp(application: Context, client: ((OkHttpClient.Builder) -> Unit)? = null) {
            okHttpClientBuilder = getUnsafeOkHttpClient()
            okHttpClientBuilder.connectTimeout(3, TimeUnit.MINUTES)
            okHttpClientBuilder.readTimeout(3, TimeUnit.MINUTES)
            okHttpClientBuilder.writeTimeout(3, TimeUnit.MINUTES)
            client?.invoke(okHttpClientBuilder)
//            okHttpClientBuilder.addInterceptor(jnkjnnn)
            okHttpClient = okHttpClientBuilder.build()

        }
        private fun okhttpClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .retryOnConnectionFailure(true)
//                .addInterceptor("oauth2-password")
                .build()
        }
        @SuppressLint("WrongConstant")
        fun buildNotification(context: Context)
        {
            val intent=Intent(context,HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT)


            val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificatonChannelId="notificatonChannelId"

            var  notificationBuilder:NotificationCompat.Builder?=null

//            val soundUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE.toString() + "://" + context.packageName + "/" + R.raw.mysound)
            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)


            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                val notificationChannel:NotificationChannel=NotificationChannel(notificatonChannelId,"Jiffy Notification",NotificationManager.IMPORTANCE_MAX)

                // Configure the notification channel.
                notificationChannel.description = "Channel description"
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                notificationChannel.enableVibration(true)
                val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                notificationChannel.setSound(alarmSound,audioAttributes)
                notificationManager.createNotificationChannel(notificationChannel)
                notificationBuilder=NotificationCompat.Builder(context,notificatonChannelId)


            }else
            {
                  notificationBuilder=NotificationCompat.Builder(context)

            }

                notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.home_icon)
                    .setTicker("Jiffy")
                    .setContentTitle("Jiffy notification")
                    .setContentText("Please get Jiffy PowerBank to keep phone always charged.")
                    .setContentInfo("Info")
//                    .setSound( alarmSound)
//                    .setDefaults(Notification.DEFAULT_VIBRATE)
//                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)







            notificationManager.notify(1234,notificationBuilder.build())


        }




    }
}



class AuthInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        context.getsp("token","").let {
//            val credentials = Credentials.basic("ODYtMTg2ODIzMTAwMTU=", "MTIzNDU2")

//            requestBuilder.addHeader("Authorization", "$credentials ")
            requestBuilder.addHeader("Authorization", "Bearer $it")
//            requestBuilder.addHeader("Authorization", "$it")

                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json;versions=1")

        }

        return chain.proceed(requestBuilder.build())
    }
}
