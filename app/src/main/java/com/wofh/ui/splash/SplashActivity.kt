package com.wofh.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.wofh.App
import com.wofh.databinding.ActivitySplashBinding
import com.wofh.preferences.UserPreferences
import com.wofh.ui.main.MainActivity
import com.wofh.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!
    private val loadingTime: Long = 3000 //Delay of 3 seconds
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private var isUserAlreadyLogin: Boolean = false
    private var isUserRegistered: Boolean = false
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, SplashViewModelFactory(application, userPreferences))[SplashViewModel::class.java]

        viewModel.getUserLogin().observe(this, {
            isUserAlreadyLogin = it
            if (isUserAlreadyLogin) {
                viewModel.getUserSetting().observe(this, { user ->
                    isUserRegistered = viewModel.isRegistered(user.email) and user.email.isNotEmpty()
                })
            }
        })

        val splashThread: Thread = object : Thread() {
            override fun run() {
                try {
                    super.run()
                    sleep(loadingTime)
                } catch (e: Exception) {
                } finally {
                    intentTo()
                }
            }
        }
        splashThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun intentTo() {
        val intent = Intent(this@SplashActivity,
            if (isUserAlreadyLogin and isUserRegistered)
                MainActivity::class.java
            else
                RegisterActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}