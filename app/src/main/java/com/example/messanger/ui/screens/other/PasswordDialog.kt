package com.example.messanger.ui.screens.other

import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.PasswordDialogBinding

class PasswordDialog : BaseDialogFragment<PasswordDialogBinding>(PasswordDialogBinding::inflate) {



    override fun onPreDialogCreate() {
        binding?.ok?.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle?.getLiveData<String>("password")?.value =
                binding?.password?.text.toString()
            dismiss()
            findNavController().popBackStack(R.id.privateInfoFragment,false)
        }
    }


}