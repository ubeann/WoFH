package com.wofh.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.wofh.auth.AESEncryption
import com.wofh.entity.Action
import com.wofh.entity.User
import com.wofh.entity.Workout
import com.wofh.entity.relation.UserAction
import com.wofh.helper.Event
import com.wofh.preferences.UserPreferences
import com.wofh.repository.ActionRepository
import com.wofh.repository.UserRepository
import com.wofh.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mWorkoutRepository: WorkoutRepository = WorkoutRepository(application)
    private val mActionRepository: ActionRepository = ActionRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    private val _userAction = MutableLiveData<Event<List<UserAction>>>()
    private val calendar: Calendar = Calendar.getInstance()
    private val today: OffsetDateTime = OffsetDateTime.of(calendar.get(Calendar.YEAR) - 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) - 0, 0, 0,0,0, ZoneOffset.UTC)
    val notificationText: LiveData<Event<String>> = _notificationText
    val userAction: LiveData<Event<List<UserAction>>> = _userAction

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

    fun setupAction(id: Int) {
        for (workout in mWorkoutRepository.getAllWorkout()) {
            mActionRepository.insert(
                Action(
                    userId = id,
                    workoutId = workout.id,
                    workoutType = workout.type,
                    dateTime = OffsetDateTime.now(),
                    isCleared = false
                )
            )
        }
    }

    fun getUserAction(userId: Int) : List<UserAction> = mActionRepository.getUserAction(userId, today)

    fun setUserActionAll(userId: Int) {
        _userAction.value = Event(mActionRepository.getUserAction(userId, today))
    }

    fun setUserActionByFilter(userId: Int, filter: String) {
        _userAction.value = Event(mActionRepository.getUserActionByFilter(userId, filter, today))
    }

    fun clearedAction(userAction: UserAction) {
        userAction.detailAction.isCleared = true
        mActionRepository.update(userAction.detailAction)
        _notificationText.value = Event("${userAction.workout.name} has been cleared")
    }

    fun unClearedAction(userAction: UserAction) {
        userAction.detailAction.isCleared = false
        mActionRepository.update(userAction.detailAction)
        _notificationText.value = Event("${userAction.workout.name} canceled to be clear")
    }
}