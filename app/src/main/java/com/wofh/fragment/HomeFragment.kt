package com.wofh.fragment

import android.content.Context
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
import com.google.android.material.chip.Chip
import com.wofh.App
import com.wofh.databinding.FragmentHomeBinding
import com.wofh.preferences.UserPreferences
import com.wofh.ui.main.MainViewModel
import com.wofh.ui.main.MainViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filterAll.setOnClickListener {
            setActiveChip(binding.filterAll)
        }

        binding.filterBulk.setOnClickListener {
            setActiveChip(binding.filterBulk)
        }

        binding.filterCut.setOnClickListener {
            setActiveChip(binding.filterCut)
        }

        binding.filterDiet.setOnClickListener {
            setActiveChip(binding.filterDiet)
        }

        binding.filterRecomp.setOnClickListener {
            setActiveChip(binding.filterRecomp)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setActiveChip(chip: Chip) {
        with(binding) {
            filterAll.isChecked = filterAll == chip
            filterBulk.isChecked = filterBulk == chip
            filterCut.isChecked = filterCut == chip
            filterDiet.isChecked = filterDiet == chip
            filterRecomp.isChecked = filterRecomp == chip
        }
    }
}