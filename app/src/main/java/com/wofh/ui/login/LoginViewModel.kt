package com.wofh.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wofh.auth.AESEncryption
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)

    fun isUserPasswordMatch(email: String, oldPassword: String): Boolean {
        val user = mUserRepository.getUserByEmail(email)
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun login(email: String) {
        val user = mUserRepository.getUserByEmail(email)
        viewModelScope.launch {
            preferences.saveUserSetting(
                userName = user.name,
                userEmail = user.email,
                userCreatedAt = user.createdAt
            )
        }
    }
}