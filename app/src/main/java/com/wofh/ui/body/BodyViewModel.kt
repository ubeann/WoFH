package com.wofh.ui.body

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository

class BodyViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserByEmail(email: String): User = mUserRepository.getUserByEmail(email)

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)

    fun updateBMI(user: User, userHeight: Double, userWeight: Double) {
        with(user) {
            height = userHeight
            weight = userWeight
        }
        mUserRepository.update(user)
    }
}