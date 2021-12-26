package com.wofh.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wofh.databinding.ActivitySplashBinding
import com.wofh.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!
    private val loadingTime: Long = 3000 //Delay of 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}