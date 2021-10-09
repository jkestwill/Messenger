package com.example.messanger.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.LoginViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModelProviderFactory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: LoginViewModel by viewModels { viewModelProviderFactory.get() }

    private lateinit var loginButton: Button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var createNewAcc: TextView


    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        loginButton.setOnClickListener {
            viewModel.login(emailField.text.toString(), passwordField.text.toString())

        }

        createNewAcc.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).appComponent.inject(this)

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    if(it.result!=null){

                        findNavController().navigate(R.id.action_loginFragment_to_profile)
                    }

                }
                is Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }


        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initViews() {
        loginButton = requireView().findViewById(R.id.login_button)
        emailField = requireView().findViewById(R.id.login_email)
        passwordField = requireView().findViewById(R.id.login_password)
        createNewAcc = requireView().findViewById(R.id.create_new_account)

    }

}