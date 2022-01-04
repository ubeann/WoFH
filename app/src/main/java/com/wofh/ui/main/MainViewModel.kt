package com.wofh.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.wofh.auth.AESEncryption
import com.wofh.entity.User
import com.wofh.helper.Event
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserByEmail(email: String): User = mUserRepository.getUserByEmail(email)

    fun getUserByEmailLive(email: String): LiveData<User> = mUserRepository.getUserByEmailLive(email)

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)

    fun isEmailValid(email: String): Boolean = !mUserRepository.isEmailRegistered(email)

    fun updateProfile(user: User, userName: String, userEmail: String) {
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, user.createdAt)
        }
        with(user) {
            name = userName
            email = userEmail
        }
        mUserRepository.update(user)
        _notificationText.value = Event("Success update profile $userName on database")
    }

    fun isPasswordMatch(userEmail: String, oldPassword: String): Boolean {
        val user = mUserRepository.getUserByEmail(userEmail)
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun updatePassword(user: User, newPassword: String) {
        user.password = AESEncryption.encrypt(newPassword)
        mUserRepository.update(user)
        _notificationText.value = Event("Success change password of ${user.name} on database")
    }

    fun forgetUserLogin(isForget: Boolean) {
        viewModelScope.launch {
            preferences.forgetUserLogin(!isForget)
        }
    }
}