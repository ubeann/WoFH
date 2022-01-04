package com.wofh.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository

class SplashViewModel  (application: Application, private val user: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getUserLogin(): LiveData<Boolean> = user.getUserIsLogin().asLiveData()

    fun getUserSetting(): LiveData<User> = user.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)
}