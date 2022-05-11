package com.example.messanger.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.databinding.FragmentForgotPasswordBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.example.messanger.viewmodels.auth.ForgotPasswordViewModel
import dagger.Lazy
import javax.inject.Inject

class ForgotPasswordFragment():BaseFragment<FragmentForgotPasswordBinding>(FragmentForgotPasswordBinding::inflate) {

    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel:ForgotPasswordViewModel by viewModels {
        factory.get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        binding?.forgotPasswordEmailSubmit?.setOnClickListener {
            viewModel.forgotPassword(binding?.forgotPasswordEmail?.text.toString())

        }
        viewModel.forgotPasswordLiveData.observe(viewLifecycleOwner){
            when(it){
                is Success->{
                    Toast.makeText(requireContext(),it.result,Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is Error->{
                    // error text view
                }
            }
        }

        binding?.toolbar?.buttonBack?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}