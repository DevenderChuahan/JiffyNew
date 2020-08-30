package `in`.jiffycharge.gopower

import android.content.Context
import android.content.SharedPreferences
var SP_FILE_NAME = "default_name"

fun Context.csp(): SharedPreferences {
    return getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
}

fun Context.setsp(key_name: String?, key_value: Any?) {

    if (key_name == null || key_value == null) {
        return
    }
    try {
        csp().edit().apply {
            when (key_value) {
                is String -> {
                    putString(key_name, key_value)
                }
                is Boolean -> {
                    putBoolean(key_name, key_value)

                }
                is Int -> {
                    putInt(key_name, key_value)
                }


            }


        }.apply()


    } catch (e: Throwable) {
        e.printStackTrace()
    }


}

fun Context.getsp(key_name: String?, default: Any): Any? {
    try {
        var result: Any? = default

        when (default) {
            is String -> {
                result = csp().getString(key_name, default)
            }
            is Boolean -> {
                result = csp().getBoolean(key_name, default)
            }
            is Int -> {
                result = csp().getInt(key_name, default)
            }

        }
        return result

    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return  default
}