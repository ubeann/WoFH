package com.wofh.ui.edit_statistic

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.repository.UserRepository

class EditStatisticViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserByEmail(email: String): User = mUserRepository.getUserByEmail(email)

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)

    fun updateStatistic(user: User, userGoal: Double,userHeight: Double, userWeight: Double) {
        with(user) {
            goal = userGoal
            height = userHeight
            weight = userWeight
        }
        mUserRepository.update(user)
    }
}