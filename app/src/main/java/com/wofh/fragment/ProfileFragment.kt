package com.wofh.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.wofh.App
import com.wofh.R
import com.wofh.databinding.FragmentProfileBinding
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.ui.login.LoginActivity
import com.wofh.ui.main.MainViewModel
import com.wofh.ui.main.MainViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var user: User
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        preferences = UserPreferences.getInstance(requireActivity().dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserSetting().observe(viewLifecycleOwner, { dataUser ->
            if (dataUser.email.isNotEmpty()) {
                user =  viewModel.getUserByEmail(dataUser.email)
            }

            with(binding) {
                inputName.editText?.setText(dataUser.name)
                inputEmail.editText?.setText(dataUser.email)
            }
        })

        binding.btnSave.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isNameFilled = isInputFilled(inputName, "Please fill your full name")
                val isEmailFilled = isInputFilled(inputEmail, "Please fill your email address")
                val isEmailValid = if (isEmailFilled and (inputEmail.editText?.text.toString() != user.email)) isEmailValid(inputEmail) else true

                if (isNameFilled and isEmailFilled and isEmailValid) {
                    viewModel.updateProfile(
                        user,
                        inputName.editText?.text.toString(),
                        inputEmail.editText?.text.toString().trim().lowercase(),
                    )
                }
            }
        }

        binding.btnPassword.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isOldPasswordFilled = isInputFilled(inputOldPassword, "Please fill your old password")
                val isOldPasswordValid = if (isOldPasswordFilled) isPasswordValid(inputOldPassword) else false
                val isNewPasswordFilled = isInputFilled(inputNewPassword, "Please fill your new password")

                if (isOldPasswordFilled and isOldPasswordValid and isNewPasswordFilled) {
                    viewModel.updatePassword(
                        user,
                        inputNewPassword.editText?.text.toString().trim()
                    )
                    inputOldPassword.editText?.setText("")
                    inputNewPassword.editText?.setText("")
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            closeKeyboard()
            viewModel.forgetUserLogin(true)
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            finishAffinity(requireActivity())
            startActivity(intent)
        }

        viewModel.notificationText.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { text ->
                showToast(text)
            }
        })
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

    private fun isEmailValid(view: TextInputLayout, error: String = "Email already registered, please use other email") : Boolean {
        view.editText?.clearFocus()
        return if (viewModel.isEmailValid(view.editText?.text.toString().trim())){
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isPasswordValid(view: TextInputLayout, error: String = "Password is not match in our records, please try again") : Boolean {
        view.editText?.clearFocus()
        return if (viewModel.isPasswordMatch(user.email, view.editText?.text.toString().trim())){
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun closeKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}