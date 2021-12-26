package com.wofh.ui.login

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
import com.wofh.MainActivity
import com.wofh.databinding.ActivityLoginBinding
import com.wofh.preferences.UserPreferences
import com.wofh.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(application, preferences))[LoginViewModel::class.java]

        binding.loginBtn.setOnClickListener {
            closeKeyboard()

            val isEmailFilled = isInputFilled(binding.email, "Please input your email address")
            val isUserRegistered = if (isEmailFilled) isEmailRegistered(binding.email, "Your email haven't registered yet, please register first") else false
            val isPasswordFilled = isInputFilled(binding.password, "Please input your password")
            val isPasswordMatch = if (isPasswordFilled and isUserRegistered) isPasswordMatch(binding.email.editText?.text.toString(), binding.password, "Password is wrong, please try again") else false

            if (isEmailFilled and isUserRegistered and isPasswordFilled and isPasswordMatch) {
                viewModel.login(email = binding.email.editText?.text.toString())

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.registerBtn.setOnClickListener {
            closeKeyboard()

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isPasswordMatch(email: String, view: TextInputLayout, error: String) : Boolean {
        return if (viewModel.isUserPasswordMatch(email, view.editText?.text.toString())) {
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }

    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}