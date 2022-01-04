package com.wofh.ui.body

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.wofh.App
import com.wofh.R
import com.wofh.databinding.ActivityBodyBinding
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.ui.main.MainViewModel
import com.wofh.ui.main.MainViewModelFactory
import com.wofh.ui.register.RegisterActivity
import kotlin.math.pow

class BodyActivity : AppCompatActivity() {
    private var _binding: ActivityBodyBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var user: User
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: BodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBodyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, BodyViewModelFactory(application, preferences))[BodyViewModel::class.java]

        viewModel.getUserSetting().observe(this, { userData ->
            if (!viewModel.isRegistered(userData.email) and userData.email.isNotEmpty()) {
                val intent = Intent(this@BodyActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
            user = viewModel.getUserByEmail(userData.email)
            if ((user.height != null) and (user.weight != null)) {
                with(binding) {
                    inputHeight.editText?.setText(user.height.toString())
                    inputWeight.editText?.setText(user.weight.toString())
                    showBMI(user.height, user.weight)
                }
            }
        })

        binding.btnCalculate.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isHeightFilled = isInputFilled(inputHeight, "Please fill your height")
                val isWeightFilled = isInputFilled(inputWeight, "Please fill your weight")

                if (isHeightFilled and isWeightFilled) {
                    showBMI(
                        inputHeight.editText?.text.toString().toDouble(),
                        inputWeight.editText?.text.toString().toDouble()
                    )
                    viewModel.updateBMI(
                        user,
                        inputHeight.editText?.text.toString().toDouble(),
                        inputWeight.editText?.text.toString().toDouble()
                    )
                }
            }
        }

        binding.btnBack.setOnClickListener {
            closeKeyboard()
            onBackPressed()
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

    private fun showBMI(height: Double?, weight: Double?) {
        val index = weight?.div((height?.div(100)?.pow(2)!!))
        index?.let {
            with(binding.cardBmi) {
                bmi.text = resources.getString(R.string.bmi, index)
                when {
                    index < 18.5  -> {
                        card.setCardBackgroundColor(Color.parseColor("#A2C6DD"))
                        status.text = String.format("Underweight")
                    }
                    index <= 24.9 -> {
                        card.setCardBackgroundColor(Color.parseColor("#D1D9A5"))
                        status.text = String.format("Normal")
                    }
                    index <= 29.9 -> {
                        card.setCardBackgroundColor(Color.parseColor("#E6BB98"))
                        status.text = String.format("Overweight")
                    }
                    index <= 34.9 -> {
                        card.setCardBackgroundColor(Color.parseColor("#E0A49F"))
                        status.text = String.format("Obese")
                    }
                    else -> {
                        card.setCardBackgroundColor(Color.parseColor("#DBA4C9"))
                        status.text = String.format("Extremely Obese")
                    }
                }
                card.visibility = View.VISIBLE
            }
        }
    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}