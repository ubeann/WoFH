package com.wofh.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wofh.App
import com.wofh.adapter.WorkoutAdapter
import com.wofh.databinding.FragmentHomeBinding
import com.wofh.entity.User
import com.wofh.entity.relation.UserAction
import com.wofh.preferences.UserPreferences
import com.wofh.ui.main.MainViewModel
import com.wofh.ui.main.MainViewModelFactory
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var user: User
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel
    private var whichFilter: String = ""

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

        with(binding.listActivity) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.getUserSetting().observe(viewLifecycleOwner, { dataUser ->
            if (dataUser.email.isNotEmpty()) {
                user =  viewModel.getUserByEmail(dataUser.email)

                if (viewModel.getUserAction(user.id).size != 12) {
                    viewModel.setupAction(user.id)
                }

                viewModel.setUserActionAll(user.id)
                whichFilter = "all"
            }
        })

        binding.filterAll.setOnClickListener {
            setActiveChip(binding.filterAll)
            whichFilter = "all"
            viewModel.setUserActionAll(user.id)
        }

        binding.filterBulk.setOnClickListener {
            setActiveChip(binding.filterBulk)
            whichFilter = "bulk"
            viewModel.setUserActionByFilter(user.id, "bulk")
        }

        binding.filterCut.setOnClickListener {
            setActiveChip(binding.filterCut)
            whichFilter = "cut"
            viewModel.setUserActionByFilter(user.id, "cut")
        }

        binding.filterDiet.setOnClickListener {
            setActiveChip(binding.filterDiet)
            whichFilter = "diet"
            viewModel.setUserActionByFilter(user.id, "diet")
        }

        binding.filterRecomp.setOnClickListener {
            setActiveChip(binding.filterRecomp)
            whichFilter = "recomp"
            viewModel.setUserActionByFilter(user.id, "recomp")
        }

        viewModel.userAction.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { listAction ->
                showActivity(listAction)
            }
        })

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

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setActiveChip(chip: Chip) {
        with(binding) {
            for (view in arrayOf(filterAll, filterBulk, filterCut, filterDiet, filterRecomp)) {
                view.isChecked = view == chip
            }
        }
    }

    private fun showActivity(lisAction: List<UserAction>) {
        if (lisAction.isNotEmpty()) {
            val adapter = WorkoutAdapter(lisAction)
            binding.listActivity.visibility = View.VISIBLE
            binding.listActivity.adapter = adapter
            adapter.setOnCardClickCallback(
                object : WorkoutAdapter.OnCardClickCallback {
                    override fun onCardClicked(data: UserAction) {
                        setOnClickActivity(data)
                    }
                }
            )
        } else {
            binding.listActivity.visibility = View.INVISIBLE
        }
    }

    private fun setOnClickActivity(userAction: UserAction) {
        if (userAction.detailAction.isCleared) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmation uncleared ${userAction.workout.name}")
                .setMessage("${userAction.workout.name} already cleared. Are you still want to make it uncleared?")
                .setNegativeButton("Close") { _, _ ->
                }
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.unClearedAction(userAction)
                    refreshListActivity(whichFilter)
                }
                .show()
        } else {
            viewModel.clearedAction(userAction)
            refreshListActivity(whichFilter)
        }
    }

    private fun refreshListActivity(filter: String) {
        if (filter == "all") {
            viewModel.setUserActionAll(user.id)
        } else {
            viewModel.setUserActionByFilter(user.id, filter)
        }
    }
}