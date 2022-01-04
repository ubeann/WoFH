package com.wofh.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wofh.entity.User
import com.wofh.helper.Event
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)
}