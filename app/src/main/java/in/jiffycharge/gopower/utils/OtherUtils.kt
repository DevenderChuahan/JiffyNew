package `in`.jiffycharge.gopower.utils

fun tryError(
    handlerError: ((Throwable) -> Unit)? = null,
    finally: (() -> Unit)? = null,
    code: () -> Unit
) {
    try {
        code.invoke()
    } catch (e: Throwable) {
        e.printStackTrace()
        handlerError?.invoke(e)
    } finally {
        finally?.invoke()
    }
}


fun getCountTimeByLong(finishTime: Long?, isNeedHour: Boolean): String {
    if (finishTime == null) {
        return "00:00"
    }
    var totalTime = (finishTime / 1000).toInt()//秒
    var hour = 0
    var minute = 0
    var second = 0

    if (3600 <= totalTime) {
        hour = totalTime / 3600
        totalTime -= 3600 * hour
    }
    if (60 <= totalTime) {
        minute = totalTime / 60
        totalTime -= 60 * minute
    }
    if (0 <= totalTime) {
        second = totalTime
    }
    val sb = StringBuilder()
    if (isNeedHour) {
        if (hour < 10) {
            sb.append("0").append(hour).append(":")
        } else {
            sb.append(hour).append(":")
        }
    }
    if (minute < 10) {
        sb.append("0").append(minute).append(":")
    } else {
        sb.append(minute).append(":")
    }
    if (second < 10) {
        sb.append("0").append(second)
    } else {
        sb.append(second)
    }
    return sb.toString()
}
