package `in`.jiffycharge.gopower.utils

import android.content.Context
import android.content.SharedPreferences

class Pref(val context: Context) {
    val myPref = context.getSharedPreferences("Login_pref", Context.MODE_PRIVATE)

    fun save_google_credentials(username: String, email: String, photo: String) {
        val myPref = context.getSharedPreferences("Login_pref", Context.MODE_PRIVATE)

        val editor = myPref.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("photo", photo)
        editor.apply()

    }

    fun get_google_credentilas(): Map<String, String> {

        val map = LinkedHashMap<String, String>()
        map.put("username", myPref.getString("username", null)!!)
        map.put("email", myPref.getString("email", null)!!)
        map.put("photo", myPref.getString("photo", null)!!)
        return map

    }

    fun clear_google_login_pref() {

        val editor = myPref.edit()
        editor.clear()
        editor.apply()

    }

    fun save_facebook_credentials(username: String, email: String, photo: String) {
        val myPref = context.getSharedPreferences("Login_pref", Context.MODE_PRIVATE)

        val editor = myPref.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("photo", photo)
        editor.apply()

    }

    fun get_facebook_credentilas(): Map<String, String> {

        val map = LinkedHashMap<String, String>()
        map.put("username", myPref.getString("username", null)!!)
        map.put("email", myPref.getString("email", null)!!)
        map.put("photo", myPref.getString("photo", null)!!)
        return map

    }

    fun clear_facebook_login_pref() {

        val editor = myPref.edit()
        editor.clear()
        editor.apply()

    }


}


