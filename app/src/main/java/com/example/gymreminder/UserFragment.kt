package com.example.gymreminder

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.databinding.FragmentUserBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Date

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var photoUri: Uri? = null
    private lateinit var profileImage: ImageView
    private var currentState: UserActions = UserActions.CREATE_USER
    private lateinit var viewModel: UserViewModel
    private lateinit var expiryDate: TextInputEditText
    private lateinit var joiningDate: TextInputEditText

    companion object {
        const val TAG = "GymApp"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserBinding.inflate(inflater, container, false)
        addFragmentResultListener()
        configureProfileButton()
        configureViewModel()
        configureCreateButton()
        configureExpiry()
        configureJoining()
        return binding.root
    }

    private fun configureExpiry() {
        expiryDate = binding.expiryDate
    }

    private fun configureJoining() {
        joiningDate = binding.joiningDate
        joiningDate.isEnabled = false
        joiningDate.setText(getTodayDate())
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return "$day/${month+1}/$year"
    }

    private fun configureViewModel() {
        val dao = UserDatabase.getInstance(requireContext()).getUserDao()
        viewModel = ViewModelProvider(this, UserViewModelFactory(dao))[UserViewModel::class.java]
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {

        }

        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {

        }
    }

    private fun configureProfileButton() {
        val image = binding.addPhoto
        image.setOnClickListener {
            navigateToCamera()
        }
        profileImage = image
    }

    private fun configureCreateButton() {
        val userAction = binding.userAction
        userAction.setOnClickListener {
            if(currentState == UserActions.EDIT_USER) {

            } else if(currentState == UserActions.CREATE_USER) {

            }
        }

    }

    private fun addFragmentResultListener() {
        setFragmentResultListener("cameraFragment") { requestKey, bundle ->
            val photoUriString = bundle.getString("photoUri")
            val photoUri = Uri.parse(photoUriString)
            this.photoUri = photoUri
            updateProfileImage()
            Log.d(TAG, "addFragmentResultListener: Found photo uri ${photoUri}")
        }

        setFragmentResultListener("homeFragment") { requestKey, bundle ->
            val currentState = bundle.getString("state")
            currentState?.let {state ->
                this.currentState = UserActions.valueOf(state)
                Log.d(TAG, "addFragmentResultListener:$state ")
            }
        }
    }

    private fun navigateToCamera() {
        findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
    }

    private fun updateProfileImage() {
        profileImage.setImageURI(photoUri)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_YEAR, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            expiryDate.setText("$dayOfMonth/${month + 1}/$year")
        }
        val dateDialog = DatePickerDialog(requireContext(),
            datePicker, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_YEAR)
        )
        dateDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener("homeFragment")
        clearFragmentResultListener("photoUri")
    }
}