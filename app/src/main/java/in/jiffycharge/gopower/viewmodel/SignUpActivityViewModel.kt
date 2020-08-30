package `in`.jiffycharge.gopower.viewmodel

import `in`.jiffycharge.gopower.repository.SignUpRepository
import androidx.lifecycle.ViewModel

class SignUpActivityViewModel(val signUpRepository: SignUpRepository):ViewModel() {

    val otp_data=signUpRepository.otp_data
    val response_message=signUpRepository.response_message








}