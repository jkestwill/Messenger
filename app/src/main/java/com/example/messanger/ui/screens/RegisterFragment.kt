package com.example.messanger.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.databinding.FragmentRegisterBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.auth.RegisterViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    @Inject
    lateinit var viewModelProviderFactory:ViewModelProviderFactory
    private val viewModel: RegisterViewModel by viewModels { viewModelProviderFactory }

    private  val TAG = "RegisterFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let {bind->
            binding?.registerButton?.setOnClickListener{
                viewModel.register(
                    bind.registerUsername.text.toString(),
                    bind.registerEmail.text.toString(),
                    bind.registerPassword.text.toString(),
                    bind.registerRepeatPassword.text.toString()
                )
            }
        }

        binding?.navigateBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.userLiveData.observe(viewLifecycleOwner){
            if(it!=null){
                when(it){
                    is Success->{
                        saveUserToSharedPreference(it.result)
                        findNavController().navigate(R.id.action_registerFragment_to_profile)
                        isLoading(false)
                    }
                    is Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        isLoading(false)
                    }
                    is Loading->{
                        isLoading(true)
                    }
                }

            }
        }


    }

    private fun saveUserToSharedPreference(firebaseUser: FirebaseUser){
        val sharedPreferences= requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply{
            putString("userId",firebaseUser.uid)
            putString("userEmail",firebaseUser.email)
        }.apply()
    }

    private fun isLoading(isLoading:Boolean){
        binding?.loading?.isVisible=isLoading
        binding?.registerButton?.isVisible=!isLoading
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }



}