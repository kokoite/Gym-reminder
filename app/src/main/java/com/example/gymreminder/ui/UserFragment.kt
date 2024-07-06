package com.example.gymreminder.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.ContentLoadingProgressBar
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
import com.example.gymreminder.utility.hideKeyboard
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
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
    private lateinit var errorText: TextView
    private lateinit var loader: ContentLoadingProgressBar
    private lateinit var paymentAmountView: TextInputEditText
    private var isDeleteFlow = false

    private var currentUri: Uri? = null
    private var updatedUri: Uri? = null


    companion object {
        const val TAG = "GymApp"
    }

    // MARK :- Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        setupProfile()
        setupViewModel()
        addFragmentResultListener()
        configureCurrentState()
        handleBackPressed()
        setupLoader()
        setupErrorView()
        return binding.root
    }

    private fun setupErrorView() {
        errorText = binding.errorText
    }

    private fun setupLoader() {
        loader = binding.loader
    }
    
    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner ,object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                updatedUri?.let {
                    requireActivity().contentResolver.delete(it, null)
                }
                findNavController().popBackStack()
            }
        })
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
        paymentAmountView = binding.paymentAmount
    }

    private fun setupViewModel() {
        val dao = UserDatabase.getInstance(requireContext()).getUserDao()
        viewModel = ViewModelProvider(this, UserViewModelFactory(dao))[UserViewModel::class.java]
    }

    private fun addFragmentResultListener() {
        setFragmentResultListener("cameraFragment") { requestKey, bundle ->
            val photoUriString = bundle.getString("photoUri")
            if(photoUriString?.isNotEmpty() == true) {
                val photoUri = Uri.parse(photoUriString)
                updatedUri = photoUri
                updateProfileImage()
            }
        }

        setFragmentResultListener("homeFragment") { requestKey, bundle ->
            val currentState = bundle.getString("state") ?: ""
            val userId = bundle.getString("userId")
            this.userId = userId
            if(currentState.isNotEmpty() &&currentState != this.currentState.name) {
                this.currentState = UserActions.valueOf(currentState)
                configureCurrentState()
            }
        }
    }

    private fun configureCurrentState() {
        isDeleteFlow = false
        when (currentState) {
            UserActions.CREATE_USER -> configureCreateUserState()
            UserActions.EDIT_USER -> configureUpdateUserState()
            UserActions.VIEW_USER -> configureViewUserState()
        }
    }

    private fun configureCreateUserState() {
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
        }
        profileActionButton.setOnClickListener {
            val user = createUserFromField()
            viewModel.createMultipleUser(user)
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
        paymentAmountView.isEnabled = true
        deleteButton.visibility = View.GONE
        separatorView.visibility = View.GONE
        profileActionButton.text = "Create User"
    }

    private fun configureViewUserState() {
        var id =0
        userId?.let {
            id = it.toInt()
        }
        viewModel.fetchUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
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
        paymentAmountView.isEnabled = false
        deleteButton.visibility = View.GONE
        viewModel.fetchUserDetail(id)
        profileActionButton.text = "Update User"
    }

    private fun updateUIForViewState(user: User) {
        if(user.photo.isNotEmpty()) {
            try {
                currentUri = Uri.parse(user.photo)
                profileImage.setImageURI(currentUri)
            } catch (error: Exception) {
                Log.d(TAG, "Unable to parse Uri")
            }
        }
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
        paymentAmountView.setText(user.amount.toString())
        expiryDate.setText(user.expiryDate)
    }

    private fun configureUpdateUserState() {
            var id = 0
            userId?.let {
                id = it.toInt()
            }
        viewModel.updateUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
        }

        viewModel.deleteUserLiveData.observe(viewLifecycleOwner) {
            handleUIState(it)
        }

        profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_createUserFragment_to_cameraFragment)
        }

        deleteButton.setOnClickListener {
            val user = createUserFromField()
            isDeleteFlow = true
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
        paymentAmountView.isEnabled = true
        separatorView.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
    }


    private fun updateProfileImage() {
        profileImage.setImageURI(updatedUri)
    }

    private fun createUserFromField(): User {
        val id = userId?.toLong() ?: 0
        val amount = paymentAmountView.text.toString().toInt() ?: 0
        val paymentStatus = paymentStatusView.text.toString().lowercase() == "yes"
        return User(id, profileNameView.text.toString(), profilePhoneView.text.toString(), profileWeightView.text.toString().toInt(), joiningDate.text.toString(), expiryDate.text.toString(), updatedUri?.toString() ?: currentUri?.toString() ?: "", profileAddressView.text.toString(), profileInjuryView.text.toString(), paymentStatus, profileGenderView.text.toString(), amount)
    }

    private fun<T> handleUIState(uiState: UIState<T>) {
        when(uiState) {
            is UIState.Success<*> -> handleSuccess(uiState.result)
            is UIState.Loading -> handleLoading()
            is UIState.Error -> handleError(uiState.message)
        }
    }

    private fun<T> handleSuccess(result: T) {
        loader.hide()
        if(isDeleteFlow) {
            updatedUri?.let {
                requireContext().contentResolver.delete(it, null)
            }
            currentUri?.let {
                requireContext().contentResolver.delete(it, null)
            }
            Toast.makeText(requireContext(),"Deleted successfully", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        when (currentState) {
            UserActions.CREATE_USER -> handleCreateUserResult()
            UserActions.EDIT_USER -> handleUpdateUserResult()
            UserActions.VIEW_USER -> handleViewUserResult(result as User)
        }
    }

    private fun handleCreateUserResult() {
        findNavController().popBackStack()
    }

    private fun handleUpdateUserResult() {
        Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
        updatedUri?.let {
            currentUri?.let {
                requireContext().contentResolver.delete(it, null)
            }
        }
        currentState = UserActions.VIEW_USER
        configureCurrentState()
    }

    private fun handleViewUserResult(user: User) {
        updateUIForViewState(user)
    }

    private fun handleLoading() {
        loader.show()
    }

    private fun handleError(error: String) {
        loader.hide()
        errorText.text = error
        errorText.visibility = VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentUri = null
        updatedUri = null
    }
}