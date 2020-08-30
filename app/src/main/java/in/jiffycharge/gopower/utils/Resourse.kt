package `in`.jiffycharge.gopower.utils

data class Resourse<out T>(val status:Status, val data:T?, val exception: String?) {
    enum class Status{LOADING,SUCCESS,ERROR }

    companion object{
        fun <T>success(data:T):Resourse<T>{
            return Resourse(Status.SUCCESS,data,null)
        }
        fun <T>error(exception:String):Resourse<T>
        {
            return Resourse(Status.ERROR,null,exception)
        }
        fun <T>loading():Resourse<T>
        {
            return Resourse(Status.LOADING,null,null)
        }


    }
}