package com.example.messanger.ui.screens.profile

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.databinding.FragmentProfileBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.ui.screens.BaseFragment
import com.example.messanger.ui.screens.other.EditTextDialogFragment
import com.example.messanger.viewmodels.profile.ProfileViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject


class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var factory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: ProfileViewModel by viewModels() {
        factory.get()
    }

    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            viewModel.uploadImage(it)

        }
    }

    private val TAG = "ProfileFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logout()
        observeUserChanges()
        observePhotoLoad()
        changeStatusListener()

        binding?.imageView?.setOnClickListener {
            contract.launch("image/*")
        }


        binding?.profileEditButton?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        //заменить

        observeStatusEditResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.getCurrentUser()

    }


    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()

    }

    //#################################################


    private fun changeStatusListener(){
        binding?.changeStatus?.setOnClickListener {
            val bundle = bundleOf(
                EditTextDialogFragment.HEADER_NAME_KEY to "Status",
                EditTextDialogFragment.INPUT_TYPE_KEY to InputType.TYPE_CLASS_TEXT
            )
            findNavController().navigate(R.id.action_profileFragment_to_status_dialog, bundle)
        }

    }


    private fun logout() {
        binding?.buttonLogout?.setOnClickListener {
            viewModel.removeDeviceToken()
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_auth)
        }

    }

    private fun observeUserChanges() {
        viewModel.userLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Success -> {
                    setUserInfo(
                        state.result.username,
                        state.result.email,
                        state.result.photoUrl,
                        state.result.status
                    )
                    binding?.changeStatus?.isEnabled = true

                    binding?.photosCountContainer?.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_profileFragment_to_photosFragment,
                            bundleOf(PhotosFragment.USER_ID_KEY to state.result.uid)
                        )
                    }
                }
                is Error -> {
                    Log.e(TAG, "onViewCreated: ${state.message}")
                }
                is Loading -> {
                    binding?.changeStatus?.isEnabled = false
                }
            }
        }
    }
    // нужно чтобы 1 раз отправляло
    private fun observePhotoLoad() {
        viewModel.imageLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                  viewModel.updateProfilePhoto(it.result.url)
                }
                is Error -> {
                    Log.e(TAG, "updatePhoto: error")
                }
                is Loading -> {
                    binding?.imageView?.resetLoader()
                }
            }
        }
    }

    private fun setUserInfo(username: String, email: String, photoUrl: String, status: String) {
        binding?.profileUsername?.text = username
        binding?.profileEmail?.text = email
        binding?.profileStatus?.text = status
        var photo = Picasso.get().load(R.drawable.shaper_rectangle_main_color)
        if (photoUrl.isNotBlank()) {
            photo = Picasso.get().load(photoUrl)
        }
        photo
            .resize(100, 100)
            .centerCrop()
            .transform(CropCircleTransformation())
            .into(binding?.imageView)
    }

    private fun observeStatusEditResult() {
        //при обычном экране работает
        // при перевернутом - нет
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("text")
            ?.observe(viewLifecycleOwner) {
                viewModel.updateStatus(it)
                Log.e(TAG, "onViewCreated: hui")
            }
        // при обычном не работает
        // при перевернутом работает
        findNavController().previousBackStackEntry?.savedStateHandle?.getLiveData<String>("text")
            ?.observe(viewLifecycleOwner) {
                Log.e(TAG, "onViewCreated: $it")
            }
    }


}