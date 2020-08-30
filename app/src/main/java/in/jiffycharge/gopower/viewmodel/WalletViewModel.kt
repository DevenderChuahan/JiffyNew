package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.model.User_balance_model
import `in`.jiffycharge.gopower.repository.WalletRepository
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class WalletViewModel(val walletRepository: WalletRepository):ViewModel() {

    var wallet_balance=ObservableField<String>()
    var deposit_amount=ObservableField<String>()

 val balance_data=  walletRepository.balance_data.observeForever(Observer<User_balance_model> {

     wallet_balance.set(it.item.availableBalance.toString())
     deposit_amount.set("500")

 })










}