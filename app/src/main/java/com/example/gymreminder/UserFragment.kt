package com.example.gymreminder

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.databinding.FragmentUserBinding

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding
    private var photoUri: Uri? = null
    lateinit var profileImage: ImageView
    private var currentState: UserActions = UserActions.CREATE_USER
    private lateinit var viewModel: UserViewModel

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
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener("homeFragment")
        clearFragmentResultListener("photoUri")
    }
}