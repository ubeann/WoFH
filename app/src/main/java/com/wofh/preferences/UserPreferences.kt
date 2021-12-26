package com.wofh.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.wofh.App
import com.wofh.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val userKey = booleanPreferencesKey(KEY)
    private val nameKey = stringPreferencesKey(NAME_KEY)
    private val emailKey = stringPreferencesKey(EMAIL_KEY)
    private val phoneKey = stringPreferencesKey(PHONE_KEY)
    private val createdKey = stringPreferencesKey(CREATED_KEY)

    fun getUserIsLogin(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[userKey] ?: false
        }
    }

    suspend fun forgetUserLogin(isForget: Boolean) {
        dataStore.edit { preferences ->
            preferences[userKey] = isForget
        }
    }

    fun getUserSetting(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                name = preferences[nameKey] ?: "Belum mengisi data",
                email = preferences[emailKey] ?: "Belum mengisi data",
                phone = preferences[phoneKey] ?: "Belum mengisi data",
                createdAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(preferences[createdKey], OffsetDateTime::from)
            )
        }
    }

    suspend fun saveUserSetting(userName: String, userEmail: String, userPhone: String, userCreatedAt: OffsetDateTime) {
        dataStore.edit { preferences ->
            preferences[userKey] = true
            preferences[nameKey] = userName
            preferences[emailKey] = userEmail
            preferences[phoneKey] = userPhone
            preferences[createdKey] = userCreatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }

    companion object {
        private const val KEY = "${App.DATA_STORE_KEY}_already_login"
        private const val NAME_KEY = "${App.DATA_STORE_KEY}_name"
        private const val EMAIL_KEY = "${App.DATA_STORE_KEY}_email"
        private const val PHONE_KEY = "${App.DATA_STORE_KEY}_phone"
        private const val CREATED_KEY = "${App.DATA_STORE_KEY}_created"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}