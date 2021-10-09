package com.example.messanger.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.RegisterViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import javax.inject.Inject


class RegisterFragment : Fragment(R.layout.fragment_register) {
    @Inject
    lateinit var viewModelProviderFactory:ViewModelProviderFactory
    private val viewModel: RegisterViewModel by viewModels { viewModelProviderFactory }


    private lateinit var username: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var repeatPasswordField: EditText
    private lateinit var registerButton: Button

    private  val TAG = "RegisterFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        registerButton.setOnClickListener{
            viewModel.register(
                username.text.toString(),
                emailField.text.toString(),
                passwordField.text.toString(),
                repeatPasswordField.text.toString()
            )

        }

        viewModel.userLiveData.observe(viewLifecycleOwner){
            if(it!=null){
                when(it){
                    is Success->{
                        findNavController().navigate(R.id.action_registerFragment_to_profile)
                    }
                    is Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    private fun initViews(){
        username=requireView().findViewById(R.id.register_username)
        emailField=requireView().findViewById(R.id.register_email)
        passwordField=requireView().findViewById(R.id.register_password)
        registerButton=requireView().findViewById(R.id.register_button)
        repeatPasswordField=requireView().findViewById(R.id.register_repeat_password)
    }


}