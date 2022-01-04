package com.wofh.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wofh.App
import com.wofh.databinding.FragmentStatisticBinding
import com.wofh.entity.User
import com.wofh.preferences.UserPreferences
import com.wofh.ui.edit_statistic.EditStatisticActivity
import com.wofh.ui.main.MainViewModel
import com.wofh.ui.main.MainViewModelFactory
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        preferences = UserPreferences.getInstance(requireActivity().dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserSetting().observe(viewLifecycleOwner, { dataUser ->
            if (dataUser.email.isNotEmpty()) {
                viewModel.getUserByEmailLive(dataUser.email).observe(viewLifecycleOwner, { user ->
                    with(binding) {
                        progressBar.progress =
                            if ((user.weight != null) and (user.goal != null))
                                (100.0 - abs(user.weight?.minus(user.goal!!)!!).div(
                                    if (user.weight!! > user.goal!!)
                                        user.weight!!
                                    else
                                        user.goal!!)*100).roundToInt()
                            else
                                100
                        progressIndicator.text =
                            if ((user.weight != null) and (user.goal != null))
                                String.format("%s%%", (100.0 - abs(user.weight?.minus(user.goal!!)!!).div(
                                    if (user.weight!! > user.goal!!)
                                        user.weight!!
                                    else
                                        user.goal!!)*100).toString())
                            else
                                "0%"

                        with(cardStatistic) {
                            weight.text = if (user.weight != null) user.weight.toString() else "0"
                            goal.text = if (user.goal != null) user.goal.toString() else "0"
                        }

                        if ((user.height != null) and (user.weight != null)) {
                            showStatus(user.height, user.weight)
                        }
                    }
                })
            }
        })

        binding.btnEdit.setOnClickListener {
            val intent = Intent(requireActivity(), EditStatisticActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showStatus(height: Double?, weight: Double?) {
        val index = weight?.div((height?.div(100)?.pow(2)!!))
        index?.let {
            with(binding) {
                when {
                    index < 18.5  -> {
                        statusCard.setCardBackgroundColor(Color.parseColor("#A2C6DD"))
                        status.text = String.format("BMI : Underweight")
                    }
                    index <= 24.9 -> {
                        statusCard.setCardBackgroundColor(Color.parseColor("#D1D9A5"))
                        status.text = String.format("BMI : Normal")
                    }
                    index <= 29.9 -> {
                        statusCard.setCardBackgroundColor(Color.parseColor("#E6BB98"))
                        status.text = String.format("BMI : Overweight")
                    }
                    index <= 34.9 -> {
                        statusCard.setCardBackgroundColor(Color.parseColor("#E0A49F"))
                        status.text = String.format("BMI : Obese")
                    }
                    else -> {
                        statusCard.setCardBackgroundColor(Color.parseColor("#DBA4C9"))
                        status.text = String.format("BMI : Extremely Obese")
                    }
                }
                statusCard.visibility = View.VISIBLE
            }
        }
    }
}