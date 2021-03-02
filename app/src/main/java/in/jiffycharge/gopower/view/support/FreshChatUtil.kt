package `in`.jiffycharge.gopower.view.support

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.ItemXX
import `in`.jiffycharge.gopower.utils.App.Companion.app
import `in`.jiffycharge.gopower.utils.tryError
import `in`.jiffycharge.gopower.view.home.HomeActivity
import androidx.annotation.StringRes
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig

object FreshChatUtil {
    private lateinit var freshchat: Freshchat

init {
    val freshConfig = FreshchatConfig(
//        app.getString(R.string.freshchat_activity_title_article_detail)
        app.resources.getString(R.string.fresh_chat_app_id),
        app.resources.getString(R.string.fresh_chat_app_key)
    )
    freshConfig.isCameraCaptureEnabled = true
    freshConfig.isGallerySelectionEnabled = true
    freshchat = Freshchat.getInstance(app)
    freshchat.init(freshConfig)
}

    fun setUser(bo: ItemXX?) {
        //No need for customer system to report errors
        tryError {
            if (bo!=null) {
                val freshUser = freshchat.user
                freshUser.firstName = bo.nickname
//          freshUser.lastName = nickname
                freshUser.email = bo.email
//          freshUser.setPhone("+91", "9790987495")
//Call setUser so that the user information is synced with Freshchat's servers
//Call setUser so that the user information is synced with Freshchat's servers
//Call setUser so that the user information is synced with Freshchat's servers
                freshchat.user = freshUser
            } else {
                Freshchat.resetUser(app)
            }
        }
    }



    fun start(activity: HomeActivity) {
        activity.getPermission(mutableListOf(android.Manifest.permission.CAMERA)) {
            if (it) {
                Freshchat.showConversations(activity)
            }
        }
    }


}