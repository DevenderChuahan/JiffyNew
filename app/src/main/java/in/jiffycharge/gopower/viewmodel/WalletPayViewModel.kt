package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.repository.WalletPayRepository
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.view.wallet.WallerPayBean
import `in`.jiffycharge.gopower.view.wallet.WallerPayMark
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletPayViewModel(val walletPayRepo:WalletPayRepository):ViewModel() {

    var paydata=MutableLiveData<Resourse<MutableList<WallerPayBean>>>()

    fun getSupportPay()
    {

        paydata.postValue(Resourse.success( mutableListOf(
            WallerPayBean(
                R.drawable.wallet_copy, "My wallet", null, WallerPayMark.BALANCE, false
            ),
            WallerPayBean(R.drawable.cashfree, WallerPayMark.DEFAULT.markStr, null, WallerPayMark.DEFAULT, true)
        )))


    }



}