package `in`.jiffycharge.gopower.utils

data class LoadingState private  constructor(val status:Status,val msg:String?=null){
    companion object
    {

        val LOADED=LoadingState(Status.SUCCESS)
        val LOADING=LoadingState(Status.RUNNUNG)
        fun error(msg: String?)=LoadingState(Status.FAILED,msg)
    }
    enum class Status
    {

        RUNNUNG,
        SUCCESS,
        FAILED
    }
}
