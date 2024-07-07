package com.example.gymreminder.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gymreminder.R
import com.example.gymreminder.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog(private val buttons: Array<BottomSheetButton>): BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetDialogBinding
    private lateinit var expiry1: Button
    private lateinit var expiry2: Button
    private lateinit var activeButton: Button
    private lateinit var inactiveButton: Button
    private lateinit var activeButExpiry: Button
    private lateinit var clearButton: Button

    // TODO :- can be changed to dynamic behaviour
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogBinding.inflate(layoutInflater, container, false)
        setupButtons()
        return binding.root
    }


    private fun setupButtons() {
        expiry1 = binding.expiry7
        expiry2 = binding.expiry15
        activeButton = binding.activeButton
        inactiveButton = binding.inactiveButton
        activeButExpiry = binding.activeWithExpiry
        clearButton = binding.clearFilter
        expiry1.text = buttons[0].title
        expiry1.setOnClickListener {
            buttons[0].execute()
        }

        expiry2.text = buttons[1].title
        expiry2.setOnClickListener {
            buttons[1].execute()
        }

        activeButton.text = buttons[2].title
        activeButton.setOnClickListener {
            buttons[2].execute()
        }

        inactiveButton.text = buttons[3].title
        inactiveButton.setOnClickListener {
            buttons[3].execute()
        }

        activeButExpiry.text = buttons[4].title
        activeButExpiry.setOnClickListener {
            buttons[4].execute()
        }

        clearButton.text = buttons[5].title
        clearButton.setOnClickListener {
            clearButton.setBackgroundColor(requireContext().getColor(R.color.blue))
            buttons[5].execute()
        }
    }

    interface BottomSheetButton {
        val title: String
        fun execute()
    }
}