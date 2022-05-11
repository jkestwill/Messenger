package com.example.messanger.ui.screens

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.databinding.FragmentEditProfileBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.settings.EditProfileViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import javax.inject.Inject


class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {
    @Inject
    lateinit var factory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: EditProfileViewModel by viewModels() {
        factory.get()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
//                    setUserInfo(it.result.username, it.result.email, it.result.photoUrl)
                }
                is Error -> {

                }
                is Loading -> {

                }
            }
        }

        binding?.privateInfo?.setOnClickListener{
            //to privateInfo
            findNavController().navigate(R.id.action_editProfileFragment2_to_privateInfoFragment)
        }

        binding?.profileInfo?.setOnClickListener {
            // to profileInfo
            findNavController().navigate(R.id.action_editProfileFragment2_to_profileInfoFragment)
        }

        binding?.removeAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment2_to_remove_account_email)

        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }




}