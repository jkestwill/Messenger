package com.example.messanger.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.ui.adapters.recycler.UsersListAdapter
import com.example.messanger.ui.adapters.recycler.difutills.UserDiffUtil
import com.example.messanger.viewmodels.ProfileViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var factory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: ProfileViewModel by navGraphViewModels(R.id.nav_graph) {
        factory.get()
    }

    private lateinit var logout: ImageButton
    private lateinit var usernameEditText: TextView
    private lateinit var emailField: TextView
    private lateinit var image: ShapeableImageView

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var editProfile: Button


    private val TAG = "ProfileFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        logout.setOnClickListener {
            viewModel.removeDeviceToken()
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_auth)
        }
        viewModel.messageCountLiveDate.observeForever {
            if (it != 0)
                bottomBar.getOrCreateBadge(R.id.messenger).number = it
            else
                bottomBar.removeBadge(R.id.messenger)

        }
        viewModel.notificationCountLiveData.observeForever {
            if (it != 0)
                bottomBar.getOrCreateBadge(R.id.notification).number = it
            else
                bottomBar.removeBadge(R.id.notification)

        }


        viewModel.userLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Success -> {
                    viewModel.notificationCount()
                    setUserInfo(state.result.username, state.result.email, state.result.photoUrl)
                }
                is Error -> {
                    Log.e(TAG, "onViewCreated: ${state.message}")
                }
            }
        }

        editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.getCurrentUser()
        viewModel.messageCount()
    }

    private fun setUserInfo(username: String, email: String, photoUrl: String) {

        usernameEditText.text = username
        emailField.text = email
        if (photoUrl.isNotBlank()) {
            Picasso.get().load(photoUrl)
                .resize(100, 100)
                .centerCrop().transform(CropCircleTransformation())
                .into(image)
        }
    }


    private fun initViews() {
        logout = requireView().findViewById(R.id.button_logout)
        usernameEditText = requireView().findViewById(R.id.profile_username)
        emailField = requireView().findViewById(R.id.profile_email)
        image = requireView().findViewById(R.id.imageView)
        bottomBar = (requireActivity()).findViewById<BottomNavigationView>(R.id.bottom_nav)
        editProfile = requireView().findViewById(R.id.profile_edit_button)

    }


}