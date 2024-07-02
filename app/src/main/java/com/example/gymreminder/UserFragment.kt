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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.toLowerCase
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


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var photoUri: Uri? = null
    private lateinit var profileImage: ImageView
    private var currentState: UserActions = UserActions.CREATE_USER
    private lateinit var viewModel: UserViewModel
    private lateinit var expiryDate: TextInputEditText
    private lateinit var joiningDate: TextInputEditText
    private lateinit var profileActionButton: Button
    private lateinit var profileNameView: TextInputEditText
    private lateinit var profileWeightView: TextInputEditText
    private lateinit var profileGenderView: TextInputEditText
    private lateinit var paymentStatusView: TextInputEditText
    private lateinit var profileInjuryView: TextInputEditText
    private lateinit var profileAddressView: TextInputEditText
    private lateinit var profilePhoneView: TextInputEditText


    companion object {
        const val TAG = "GymApp"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        configureProfile()
        configureExpiry()
        configureJoining()
        configureViewModel()
        configureProfileActionButton()
        addFragmentResultListener()
        return binding.root
    }

    private fun configureProfile() {
        profileNameView = binding.userName
        profileAddressView = binding.userAddress
        profileInjuryView = binding.userInjuries
        profilePhoneView = binding.userPhone
        profileWeightView = binding.userWeight
        profileGenderView = binding.userGender
        configureProfileButton()
        paymentStatusView = binding.paymentStatus
    }



    private fun configureProfileActionButton() {
        profileActionButton = binding.userAction
        profileActionButton.setOnClickListener {
            if(currentState == UserActions.CREATE_USER) {
                handleCreateUser()
            }
        }
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
        return "$day-${month+1}-$year"
    }

    private fun configureViewModel() {
        val dao = UserDatabase.getInstance(requireContext()).getUserDao()
        viewModel = ViewModelProvider(this, UserViewModelFactory(dao))[UserViewModel::class.java]
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "configureViewModel: createdUser ${it}")
        }

        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {

        }
    }

    private fun handleCreateUser() {
        // TODO :- Use something like switch
        if(currentState == UserActions.CREATE_USER) {
            // TODO :- Validate in UseCases for now assuming valid data
            val paymentStatus = paymentStatusView.text.toString().lowercase() == "yes"
            val user = User(0, profileNameView.text.toString(), profilePhoneView.text.toString(), profileWeightView.text.toString().toInt(), joiningDate.text.toString(), expiryDate.text.toString(), photoUri?.toString() ?: "", profileAddressView.text.toString(), profileInjuryView.text.toString(), paymentStatus, profileGenderView.text.toString())
            Log.d(TAG, "handleCreateUser: $user")
            viewModel.createUser(user)
        }
    }


    private fun configureProfileButton() {
        val image = binding.addPhoto
        image.setOnClickListener {
            // Deleting existing photo
            photoUri?.let {
                requireContext().contentResolver.delete(it,null)
            }
            photoUri = null
            navigateToCamera()
        }
        profileImage = image
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
            val userId = bundle.getString("userId")
            handleCurrentState(userId)

        }
    }

    private fun handleCurrentState(userId: String?) {
        if(currentState == UserActions.CREATE_USER) {
            Log.d(TAG, "handleCurrentState: Create user called")

        } else if(currentState == UserActions.VIEW_USER) {
            Log.d(TAG, "handleCurrentState: called")
            userId?.let {
                viewModel.fetchUserDetail(userId.toInt())
            }
            handleViewUser()
            Log.d(TAG, "handleCurrentState: View user called")

        } else {
            Log.d(TAG, "handleCurrentState: Update user called")
        }
    }

    private fun handleViewUser() {
        profileImage.setOnClickListener {
            // TODO :- Open zoomed view
        }

        profileNameView.isEnabled = false
        profileWeightView.isEnabled = false
        profilePhoneView.isEnabled = false
        profileAddressView.isEnabled = false
        profileInjuryView.isEnabled = false
        profileGenderView.isEnabled = false
        profileActionButton.text = "Update Profile"
        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
            // get the state of user and update all the fields respectively
        }
    }

    private fun handleUIState(uiState: UIState<User>) {
        when (uiState) {
            is  UIState.Loading ->
                Log.d(TAG, "handleUIState: loading")
            is  UIState.Success<User> -> updateProfile(uiState.result)
            is UIState.Error -> Log.d(TAG, "handleUIState: Some error occured")
        }
    }

    private fun updateProfile(user: User) {
        Log.d(TAG, "handleUIState: user found ${user}")
        profileActionButton.text = "Update User"
        profileNameView.setText(user.name)
        profileGenderView.setText(user.gender)
        profileInjuryView.setText(user.existingProblems)
        profileAddressView.setText(user.address)
        profileWeightView.setText(user.weight.toString())
        profilePhoneView.setText(user.phoneNumber)
        profileImage.setImageURI(Uri.parse(user.photo))
        profileActionButton.setOnClickListener {
            Log.d(TAG, "updateProfile: profile photo clicked ")
        }
    }

    private fun navigateToCamera() {
        findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
    }

    private fun updateProfileImage() {
        profileImage.setImageURI(photoUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener("homeFragment")
        clearFragmentResultListener("photoUri")
    }
}