package com.wofh.ui.edit_statistic

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
import com.wofh.databinding.ActivityEditStatisticBinding
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.ui.body.BodyViewModel
import com.wofh.ui.body.BodyViewModelFactory
import com.wofh.ui.register.RegisterActivity

class EditStatisticActivity : AppCompatActivity() {
    private var _binding: ActivityEditStatisticBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var user: User
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: EditStatisticViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, EditStatisticViewModelFactory(application, preferences))[EditStatisticViewModel::class.java]

        viewModel.getUserSetting().observe(this, { userData ->
            if (!viewModel.isRegistered(userData.email) and userData.email.isNotEmpty()) {
                val intent = Intent(this@EditStatisticActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }

            user = viewModel.getUserByEmail(userData.email)

            with(binding) {
                inputGoal.editText?.setText(if (user.goal != null) user.goal.toString() else "")
                inputHeight.editText?.setText(if (user.height != null) user.height.toString() else "")
                inputWeight.editText?.setText(if (user.weight != null) user.weight.toString() else "")
            }
        })

        binding.btnSave.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isGoalFilled = isInputFilled(inputGoal, "Please set your goal weight")
                val isHeightFilled = isInputFilled(inputHeight, "Please fill your current height")
                val isWeightFilled = isInputFilled(inputWeight, "Please fill your current weight")

                if (isGoalFilled and isHeightFilled and isWeightFilled) {
                    viewModel.updateStatistic(
                        user,
                        inputGoal.editText?.text.toString().toDouble(),
                        inputHeight.editText?.text.toString().toDouble(),
                        inputWeight.editText?.text.toString().toDouble()
                    )
                    finish()
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

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}