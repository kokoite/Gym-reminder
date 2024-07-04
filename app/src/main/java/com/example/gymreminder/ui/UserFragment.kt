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
import com.example.gymreminder.utility.getTodayDate
import com.google.android.material.textfield.TextInputEditText
import java.lang.IllegalArgumentException
import java.util.Calendar


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var photoUri: Uri? = null
    private var userId: String? = null
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
    private lateinit var deleteButton: Button
    private lateinit var separatorView: View


    companion object {
        const val TAG = "GymApp"
    }

    // MARK :- Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate UserFragment $currentState")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        setupProfile()
        setupViewModel()
        addFragmentResultListener()
        return binding.root
    }

    // Initializing all profile related views
    private fun setupProfile() {
        profileImage = binding.addPhoto
        profileNameView = binding.userName
        profileAddressView = binding.userAddress
        profileInjuryView = binding.userInjuries
        profilePhoneView = binding.userPhone
        profileWeightView = binding.userWeight
        profileGenderView = binding.userGender
        paymentStatusView = binding.paymentStatus
        expiryDate = binding.expiryDate
        joiningDate = binding.joiningDate
        joiningDate.isEnabled = false
        joiningDate.setText(getTodayDate())
        profileActionButton = binding.createupdateUser
        deleteButton = binding.deleteUser
        separatorView = binding.userActionSeparatorView
    }

    private fun setupViewModel() {
        val dao = UserDatabase.getInstance(requireContext()).getUserDao()
        viewModel = ViewModelProvider(this, UserViewModelFactory(dao))[UserViewModel::class.java]
    }

    private fun addFragmentResultListener() {
        setFragmentResultListener("cameraFragment") { requestKey, bundle ->
            val photoUriString = bundle.getString("photoUri")
            if(photoUriString?.isNotEmpty() == true) {
                photoUri?.let {
                    requireContext().contentResolver.delete(it, null)
                }
            }
            val photoUri = Uri.parse(photoUriString)
            this.photoUri = photoUri
            updateProfileImage()
        }

        setFragmentResultListener("homeFragment") { requestKey, bundle ->
            val currentState = bundle.getString("state")
            currentState?.let {state ->
                this.currentState = UserActions.valueOf(state)
                Log.d(TAG, "addFragmentResultListener:$state ")
            }
            val userId = bundle.getString("userId")
            this.userId = userId
            configureCurrentState()
        }
    }

    private fun configureCurrentState() {
        when (currentState) {
            UserActions.CREATE_USER -> configureCreateUserState()
            UserActions.EDIT_USER -> configureUpdateUserState()
            UserActions.VIEW_USER -> configureViewUserState()
        }
    }

    private fun configureCreateUserState() {
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        profileActionButton.setOnClickListener {
            val user = createUserFromField()
            viewModel.createUser(user)
        }

        profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
        }

        profileNameView.isEnabled = true
        profileGenderView.isEnabled = true
        profileAddressView.isEnabled = true
        profileInjuryView.isEnabled = true
        profilePhoneView.isEnabled = true
        profileWeightView.isEnabled = true
        paymentStatusView.isEnabled = true
        expiryDate.isEnabled = true
        deleteButton.visibility = View.GONE
        separatorView.visibility = View.GONE
        profileActionButton.text = "Create User"
    }

    private fun configureViewUserState() {
        var id =0
        userId?.let {
            id = it.toInt()
        }
        Log.d(TAG, "configureViewUserState $id")
        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "configureViewUserState: $it")
            when(it) {
                is UIState.Loading -> handleLoading()
                is UIState.Success -> updateUIForViewState(it.result)
                is UIState.Error -> handleError()
            }
        }

        profileActionButton.setOnClickListener {
            currentState = UserActions.EDIT_USER
            configureUpdateUserState()
        }

        profileImage.setOnClickListener {
            val bundle = Bundle().apply {
                putString("photoUri", photoUri?.toString())
            }
            setFragmentResult("userFragment", bundle)
            findNavController().navigate(R.id.action_createUserFragment_to_previewImageFragment)
        }

        profileNameView.isEnabled = false
        profileGenderView.isEnabled = false
        profileAddressView.isEnabled = false
        profileInjuryView.isEnabled = false
        profilePhoneView.isEnabled = false
        profileWeightView.isEnabled = false
        paymentStatusView.isEnabled = false
        expiryDate.isEnabled = false
        deleteButton.visibility = View.GONE
        viewModel.fetchUserDetail(id)
        profileActionButton.text = "Update User"
    }

    private fun updateUIForViewState(user: User) {
        photoUri = Uri.parse(user.photo)
        profileImage.setImageURI(Uri.parse(user.photo))
        profileNameView.setText(user.name)
        profileGenderView.setText(user.gender.uppercase())
        profileInjuryView.setText(user.existingProblems)
        profileAddressView.setText(user.address)
        profileWeightView.setText(user.weight.toString())
        profilePhoneView.setText(user.phoneNumber)
        val paymentStatus = if(user.paymentDone) {
            "yes"
        } else {
            "no"
        }
        paymentStatusView.setText(paymentStatus)
        expiryDate.setText(user.expiryDate)
    }

    private fun configureUpdateUserState() {
            var id = 0
            userId?.let {
                id = it.toInt()
            }
        viewModel.updateUserLiveData.observe(viewLifecycleOwner) {
            // TODO :- show toast message
            Log.d(TAG, "configureUpdateUserState: $it")
            findNavController().popBackStack()
        }

        viewModel.deleteUserLiveData.observe(viewLifecycleOwner) {
            // TODO :- show toast message
            Log.d(TAG, "configureUpdateUserState: $it")
            // delete the image present at uri
            findNavController().popBackStack()
        }

        profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
        }

        deleteButton.setOnClickListener {
            val user = createUserFromField()
            viewModel.deleteUser(user)
        }

        profileActionButton.setOnClickListener {
            val user = createUserFromField()
            viewModel.updateUserDetail(user)
        }

        profileNameView.isEnabled = true
        profileGenderView.isEnabled = true
        profileAddressView.isEnabled = true
        profileInjuryView.isEnabled = true
        profilePhoneView.isEnabled = true
        profileWeightView.isEnabled = true
        paymentStatusView.isEnabled = true
        expiryDate.isEnabled = true
        separatorView.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
    }


    private fun updateProfileImage() {
        profileImage.setImageURI(photoUri)
    }

    private fun createUserFromField(): User {
        val id = userId?.toLong() ?: 0
        val paymentStatus = paymentStatusView.text.toString().lowercase() == "yes"
        return User(id, profileNameView.text.toString(), profilePhoneView.text.toString(), profileWeightView.text.toString().toInt(), joiningDate.text.toString(), expiryDate.text.toString(), photoUri?.toString() ?: "", profileAddressView.text.toString(), profileInjuryView.text.toString(), paymentStatus, profileGenderView.text.toString())
    }

    private fun handleLoading() {

    }

    private fun handleError() {

    }
}