package com.wofh.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.wofh.App
import com.wofh.R
import com.wofh.databinding.ActivityMainBinding
import com.wofh.preferences.UserPreferences
import com.wofh.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application, preferences))[MainViewModel::class.java]

        viewModel.getUserSetting().observe(this, { user ->
            if (!viewModel.isRegistered(user.email) and user.email.isNotEmpty()) {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationBar.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}