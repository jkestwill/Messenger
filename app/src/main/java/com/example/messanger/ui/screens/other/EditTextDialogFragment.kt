package com.example.messanger.ui.screens.other

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.EmailDialogBinding

class EditTextDialogFragment : BaseDialogFragment<EmailDialogBinding>(EmailDialogBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPreDialogCreate() {
        setupDialogInfo()
        binding?.ok?.setOnClickListener {
            if (binding?.newEmail?.text!!.isNotBlank()) {
                Log.e(TAG, "onPreDialogCreate:${findNavController().previousBackStackEntry?.destination} ")
                findNavController().previousBackStackEntry
                    ?.savedStateHandle?.set("text",binding?.newEmail?.text.toString())
                findNavController().popBackStack(R.id.profileFragment,false)
            }

        }
        binding?.cancel?.setOnClickListener {
            dismiss()

        }
    }

    private fun setupDialogInfo() {
        arguments?.let {
            binding?.header?.text = it.getString(HEADER_NAME_KEY)
            binding?.header?.inputType = it.getInt(INPUT_TYPE_KEY)
        }
    }


    companion object {
        const val TAG = "EditTextDialogFragment"
        const val HEADER_NAME_KEY = "header"
        const val INPUT_TYPE_KEY = "input_type"

    }

}