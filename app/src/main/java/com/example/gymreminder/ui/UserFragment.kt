package com.example.gymreminder.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.gymreminder.R
import com.example.gymreminder.UIState
import com.example.gymreminder.UserActions
import com.example.gymreminder.UserViewModel
import com.example.gymreminder.UserViewModelFactory
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.databinding.FragmentUserBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate UserFragment $currentState")
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
        Log.d(TAG, "onCreateView: UserFragment $currentState")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: UserFragment $currentState")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: UserFragment $currentState")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: UserFragment $currentState ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: UserFragment $currentState")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: UserFragment $currentState")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener("homeFragment")
        clearFragmentResultListener("photoUri")
        Log.d(TAG, "onDestroyView: UserFragment $currentState")
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
            handleUserAction()
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
    }


    private fun configureProfileButton() {
        profileImage = binding.addPhoto
        handleProfileCreateAction()
    }

    
    private fun handleProfileCreateAction() {
        profileImage.setOnClickListener {
            // Deleting existing photo
            photoUri?.let {
                requireContext().contentResolver.delete(it,null)
            }
            photoUri = null
            findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
        }
    }
    
    private fun handleProfileViewAction(photoUri: String) {
        profileImage.setOnClickListener {
            val bundle = Bundle().apply {
                putString("photoUri", photoUri )
            }
            Log.d(TAG, "handleViewUserState: $photoUri")
            setFragmentResult("userFragment", bundle)
            findNavController().navigate(R.id.action_createUserFragment_to_previewImageFragment)
        }
    }
    
    private fun handleProfileUpdateAction() {
        profileImage.setOnClickListener {
            photoUri?.let {
                requireContext().contentResolver.delete(it,null)
            }
            photoUri = null
            findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
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
            val userId = bundle.getString("userId")
            if(this.currentState == UserActions.VIEW_USER) {
                val id = userId?.toInt() ?: 0
                handleViewUserState(id)
            }
        }
    }



    private fun updateUiForViewState(user: User) {
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

        handleProfileViewAction(user.photo)
    }

    private fun updateUiForCreateState() {

    }

    private fun updateUiForUpdateState() {

    }


    private fun handleUIState(uiState: UIState<User>) {
        when (uiState) {
            is UIState.Loading ->
                Log.d(TAG, "handleUIState: loading")
            is UIState.Success<User> -> updateUiForViewState(uiState.result)
            is UIState.Error -> Log.d(TAG, "handleUIState: Some error occured")
        }
    }

    private fun navigateToCamera() {

    }

    private fun updateProfileImage() {
        profileImage.setImageURI(photoUri)
    }

    // Mark :- User Action
    private fun handleUserAction() {
        when (currentState) {
            UserActions.CREATE_USER -> handleCreateUserAction()
            UserActions.EDIT_USER -> handleUpdateUserAction()
            else -> {
                Log.d(TAG, "handleUserAction: View User")
            }
        }
    }

    private fun handleCreateUserAction() {
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
        }
        val paymentStatus = paymentStatusView.text.toString().lowercase() == "yes"
        val user = User(0, profileNameView.text.toString(), profilePhoneView.text.toString(), profileWeightView.text.toString().toInt(), joiningDate.text.toString(), expiryDate.text.toString(), photoUri?.toString() ?: "", profileAddressView.text.toString(), profileInjuryView.text.toString(), paymentStatus, profileGenderView.text.toString())
        Log.d(TAG, "handleCreateUser: $user")
        viewModel.createMultipleUser(user)
    }

    private fun handleUpdateUserAction() {
        viewModel.updateUserLiveData.observe(viewLifecycleOwner) {

        }
    }

    private fun handleViewUserState(userId: Int) {
        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
        }
        profileNameView.isEnabled = false
        profileWeightView.isEnabled = false
        profilePhoneView.isEnabled = false
        profileAddressView.isEnabled = false
        profileInjuryView.isEnabled = false
        profileGenderView.isEnabled = false
        expiryDate.isEnabled = false
        paymentStatusView.isEnabled = false
        profileActionButton.text = "Update Profile"
        viewModel.fetchUserDetail(userId)
    }
}