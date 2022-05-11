package com.example.messanger.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.databinding.FragmentLoginBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.auth.LoginViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var viewModelProviderFactory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: LoginViewModel by viewModels { viewModelProviderFactory.get() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.loginButton?.setOnClickListener {
            viewModel.login(
                binding?.loginEmail?.text.toString(),
                binding?.loginPassword?.text.toString()
            )
        }

        binding?.createNewAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding?.forgotPassword?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).appComponent.inject(this)

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            // если пользователь уже зарегистрирован то его переводит на главный фрагмент
            when (it) {
                is Success -> {
                    isLoading(false)
                    if (it.result != null) {
                        findNavController().navigate(R.id.action_loginFragment_to_profile)

                    }
                }
                is Error -> {
                    isLoading(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Loading -> {
                    isLoading(true)
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    private fun isLoading(isLoading: Boolean) {
        binding?.loading?.isVisible = isLoading
        binding?.loginButton?.isVisible = !isLoading
    }


}