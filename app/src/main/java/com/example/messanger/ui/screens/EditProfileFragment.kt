package com.example.messanger.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.navGraphViewModels
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.viewmodels.EditProfileViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    @Inject
    lateinit var factory: dagger.Lazy<ViewModelProviderFactory>
    private val viewModel: EditProfileViewModel by navGraphViewModels(R.id.nav_graph) {
        factory.get()
    }

    private var content = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            viewModel.sendImage(it)

        } ?: return@registerForActivityResult
    }

    private lateinit var usernameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var confirmEmailEt: EditText
    private lateinit var newPassword: EditText
    private lateinit var passwordEt: EditText
    private lateinit var repeatPasswordEt: EditText
    private lateinit var image: ShapeableImageView
    private lateinit var submitUserNameChanges: Button
    private lateinit var submitLoginChanges: Button
    private lateinit var changeEmailButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.userLiveData.observe(viewLifecycleOwner){
            when(it){
                is Success->{
                    setUserInfo(it.result.username,it.result.email,it.result.photoUrl)
                }
                is Error->{

                }
            }
        }

        submitLoginChanges.setOnClickListener {

            viewModel.updatePassword(newPassword.text.toString(),passwordEt.text.toString(),repeatPasswordEt.text.toString())
        }

        submitUserNameChanges.setOnClickListener {
            viewModel.updateUsername(usernameEt.text.toString())
        }

        changeEmailButton.setOnClickListener {
            viewModel.updateEmail(emailEt.text.toString(),confirmEmailEt.text.toString())
        }



        image.setOnClickListener {
            content.launch("image/*")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.getCurrentUser()
    }

    private fun setUserInfo(username: String, email: String, photoUrl: String) {
        usernameEt.setText(username)
        emailEt.setText(email)
        if (photoUrl.isNotBlank()) {
            Picasso.get().load(photoUrl)
                .resize(100, 100)
                .centerCrop().transform(CropCircleTransformation())
                .into(image)
        }
    }

    private fun initViews() {
        usernameEt = requireView().findViewById(R.id.edit_profile_fragment_username)
        image = requireView().findViewById(R.id.edit_profile_fragment_image)
        emailEt = requireView().findViewById(R.id.edit_profile_fragment_email)
        passwordEt = requireView().findViewById(R.id.edit_profile_old_password)
        repeatPasswordEt = requireView().findViewById(R.id.edit_profile_fragment_repeat_old_password)
        submitUserNameChanges = requireView().findViewById(R.id.edit_profile_fragment_submit_changes_button)
        submitLoginChanges = requireView().findViewById(R.id.edit_profile_fragment_submit_reset_password)
        confirmEmailEt = requireView().findViewById(R.id.submit_email_change)
        changeEmailButton=requireView().findViewById(R.id.edit_profile_fragment_change_email_button)
        newPassword=requireView().findViewById(R.id.edit_profile_new_password)

    }


}