package com.wofh.ui.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wofh.auth.AESEncryption
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class RegisterViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun register(userName: String, userEmail: String, userPassword: String, userCreatedAt: OffsetDateTime) {
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userCreatedAt)
        }
        val user = User(
            name = userName,
            email = userEmail,
            password = AESEncryption.encrypt(userPassword),
            goal = null,
            height = null,
            weight = null,
            createdAt = userCreatedAt
        )
        mUserRepository.insert(user)
    }

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)
}