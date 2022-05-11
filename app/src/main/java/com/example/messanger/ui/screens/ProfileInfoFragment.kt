package com.example.messanger.ui.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.databinding.FragmentProfileInfoBinding
import com.example.messanger.models.Gender
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.viewmodels.settings.ProfileInfoViewModel
import javax.inject.Inject

class ProfileInfoFragment :
    BaseFragment<FragmentProfileInfoBinding>(FragmentProfileInfoBinding::inflate) {
    @Inject
    lateinit var factory: ViewModelProviderFactory

    private val viewModel by viewModels<ProfileInfoViewModel> {
        factory
    }

    private val TAG = "ProfileInfoFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    showUserInfo(
                        it.result.username,
                        it.result.birthday,
                        it.result.gender
                    )

                    Log.e(TAG, "onViewCreated: ${it.result}")
                }
                is Error -> {
                    Log.e(TAG, "onViewCreated: ${it.message}")
                }

                is Loading -> {

                }
            }

        }

        binding?.toolbarBack?.buttonBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.birthday?.setOnClickListener {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { datePicker, year, month, day ->
                    binding?.birthday?.text = "$day/${month + 1}/$year"
                    Log.e(TAG, "onViewCreated: pizdec")
                }
            }.show()
        }

        binding?.submitChanges?.setOnClickListener {
            val gender = when (binding?.gender?.checkedRadioButtonId) {
                binding?.female?.id -> Gender.MALE
                binding?.male?.id -> Gender.FEMALE
                else -> Gender.NONE
            }
            saveUserInfo(
                binding?.username?.text.toString(),
                binding?.birthday?.text.toString(),
                gender
            )
        }


    }

    private fun showUserInfo(username: String, birthday: String, gender: Gender) {
        binding?.username?.setText(username)
        binding?.birthday?.text =birthday
        if (gender!= Gender.NONE) {
            binding?.male?.isChecked = gender == Gender.MALE
            binding?.female?.isChecked = gender == Gender.FEMALE
        }

    }

    private fun saveUserInfo(username: String, birthday: String, gender: Gender) {
        viewModel.updateUsername(username)
        viewModel.updateBirthday(birthday)
        viewModel.updateGender(gender)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.getCurrentUser()
    }
}