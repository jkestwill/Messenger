package com.example.messanger.ui.screens.other

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.messanger.ui.screens.Inflate

abstract class BaseDialogFragment<B : ViewBinding>(private val inflate: Inflate<B>) :
    DialogFragment() {
    private var _binding: B? = null
    val binding get() = _binding


    abstract fun onPreDialogCreate()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = inflate(layoutInflater, null, false)
        //единственное место где работает binding
        onPreDialogCreate()
        return AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create().also {
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}