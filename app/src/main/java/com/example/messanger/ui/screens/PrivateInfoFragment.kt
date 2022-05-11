package com.example.messanger.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.databinding.FragmentPrivateLayoutBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.settings.PrivateInfoViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import dagger.Lazy
import javax.inject.Inject

class PrivateInfoFragment :
    BaseFragment<FragmentPrivateLayoutBinding>(FragmentPrivateLayoutBinding::inflate) {
    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: PrivateInfoViewModel by viewModels {
        factory.get()
    }

    private val TAG = "PrivateInfoFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.include?.buttonBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.submitEmailChange?.setOnClickListener {
            binding?.let {
                viewModel.updateEmail(it.email.text.toString(), it.emailPassword.text.toString())
            }
        }

        binding?.changePassword?.setOnClickListener {
            binding?.let {
                viewModel.updatePassword(
                    it.newPassword.text.toString(),
                    it.oldPassword.text.toString(),
                    it.repeatNewPassword.text.toString()
                )
            }

        }
        viewModel.passwordLiveData.observe(viewLifecycleOwner){
            Log.e(TAG, "onViewCreatedPassword: ${it}", )
            when(it){
                is Success ->{
                    Log.e(TAG, "${it.result}", )
                }
                is Error->{
                    Log.e(TAG, " ${it.message}", )
                }

                is Loading->{

                }
            }
        }

        viewModel.emailLiveData.observe(viewLifecycleOwner) {
            Log.e(TAG, "onViewCreated: ${it}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }


}