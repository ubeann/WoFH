package com.wofh.ui.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.wofh.App
import com.wofh.ui.main.MainActivity
import com.wofh.databinding.ActivityRegisterBinding
import com.wofh.preferences.UserPreferences
import com.wofh.ui.login.LoginActivity
import java.time.OffsetDateTime

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(application, preferences))[RegisterViewModel::class.java]

        binding.registerBtn.setOnClickListener {
            closeKeyboard()

            val isNameFilled = isInputFilled(binding.name, "Please input your full name")
            val isEmailFilled = isInputFilled(binding.email, "Please input your email address")
            val isUserRegistered = if (isEmailFilled) isEmailRegistered(binding.email, "Your email already registered, please use other") else false
            val isPasswordFilled = isInputFilled(binding.password, "Please input your password")

            if (isNameFilled and isEmailFilled and isPasswordFilled and !isUserRegistered) {
                viewModel.register(
                    userName = binding.name.editText?.text.toString(),
                    userEmail = binding.email.editText?.text.toString().lowercase(),
                    userPassword = binding.password.editText?.text.toString(),
                    userCreatedAt = OffsetDateTime.now()
                )

                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.loginBtn.setOnClickListener {
            closeKeyboard()

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isInputFilled(view: TextInputLayout, error: String) : Boolean {
        view.editText?.clearFocus()
        return if (TextUtils.isEmpty(view.editText?.text.toString().trim())) {
            view.isErrorEnabled = true
            view.error = error
            false
        } else {
            view.isErrorEnabled = false
            true
        }
    }

    private fun isEmailRegistered(view: TextInputLayout, error: String) : Boolean {
        return if (viewModel.isRegistered(view.editText?.text.toString().lowercase().trim())) {
            view.isErrorEnabled = true
            view.error = error
            true
        } else {
            view.isErrorEnabled = false
            false
        }
    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}