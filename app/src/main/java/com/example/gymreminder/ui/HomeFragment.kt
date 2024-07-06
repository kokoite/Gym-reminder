package com.example.gymreminder.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymreminder.HomeViewModel
import com.example.gymreminder.HomeViewModelFactory
import com.example.gymreminder.MyApplication
import com.example.gymreminder.NotificationService
import com.example.gymreminder.R
import com.example.gymreminder.UserActions
import com.example.gymreminder.data.TAG
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.databinding.FragmentHomeBinding
import com.example.gymreminder.utility.hideKeyboard
import com.example.gymreminder.usecase.CreateUserImpl
import com.example.gymreminder.usecase.EditUser
import com.example.gymreminder.usecase.FetchAllUserImpl
import com.example.gymreminder.usecase.FilterUsers
import com.example.gymreminder.usecase.FilterUsersImpl
import com.example.gymreminder.utility.setBackgroundTint
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchView
import kotlinx.coroutines.delay
import kotlin.math.abs


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var  userListAdapter: UserListAdapter
    private lateinit var userListRecyclerView: RecyclerView
    private lateinit var createButton: FloatingActionButton
    private lateinit var viewModel: HomeViewModel
    private lateinit var dialog: BottomSheetDialog
    private lateinit var closeButton: ImageView
    private lateinit var searchButton: ImageView
    private lateinit var filterButton: ImageView
    private var navigatingFromSettings = false


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
        if(result) {
            Log.d(TAG, "Permssion granted for post notification ")
        } else {
            // show Denied permission dialog
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        configureViewModel()
        configureSearchView()
        configureRecyclerView()
        configureCreateUserButton()
        fetchUsers()
        requestPermission()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        if(!checkPermission()) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                val shouldShow = shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                if(shouldShow) {
                    showRationaleDialog()
                }
        } else {
            Log.d(TAG, "Post notification permission already granted")
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    private fun showRationaleDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Notification permission needed")
            .setMessage("Notification permission is needed in order to send notification")
            .setPositiveButton("Okay") { dialog, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                dialog.cancel()
            }
            .setNegativeButton("Cancel") {_,_ ->
                findNavController().popBackStack()
            }.create()
            .show()
    }

    private fun showPermissionDeniedDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Permission denied")
            .setMessage("Notification permission denied. You can enable it in app settings")
            .setPositiveButton("Settings") {dialog, _ ->
                val intent  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }

                startActivity(intent)
                navigatingFromSettings = true
                dialog.cancel()
            }.setNegativeButton("Cancel") {_,_ ->
                findNavController().popBackStack()
            }.create().show()
    }

    private fun configureViewModel() {
        val app = requireActivity().application as MyApplication
        viewModel = ViewModelProvider(this, HomeViewModelFactory(app.fetchUsers, app.filterUser))[HomeViewModel::class.java]
        viewModel.listOfUser.observe(viewLifecycleOwner) {
            userListAdapter.submitList(it)
        }
    }

    private fun configureRecyclerView() {
        userListRecyclerView = binding.usersList
        userListRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val userListImpl = UserListActionImpl()
        userListAdapter = UserListAdapter(userListImpl)
        userListRecyclerView.adapter = userListAdapter
    }

    private fun fetchUsers() {
        viewModel.fetchAllUser()
    }

    private fun configureSearchView() {
        searchView = binding.searchField
        searchView.isFocusedByDefault = false
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                viewModel.filterUser(UserFilter.NameFilter(query ?: ""))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.filterUser(UserFilter.NameFilter(it))
                }
                return true
            }
        })
    }

    private fun configureCreateUserButton() {
        createButton = binding.createButton
        createButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("state", UserActions.CREATE_USER.toString())
            }
            setFragmentResult("homeFragment", bundle)
            findNavController().navigate(R.id.action_homeFragment2_to_createUserFragment)
        }
    }

    private fun showBottomSheet() {
        val buttonActions = getButtonAction()
        dialog = BottomSheetDialog(buttonActions)
        dialog.show(childFragmentManager, "gymApp")
    }

    private fun getButtonAction() : Array<BottomSheetDialog.BottomSheetButton> {

        val button1 = createBottomSheetButton(7)
        val button2 = createBottomSheetButton(15)
        val button3 = createBottomSheetButton(30)
        val button4 = object: BottomSheetDialog.BottomSheetButton {
            override val title: String
                get() = "Clear all filters"

            override fun execute() {
                viewModel.clearAllFilter()
                dialog.dismiss()
            }
        }

        return listOf(button1, button2, button3, button4).toTypedArray()
    }

    private fun createBottomSheetButton(days: Int) = object : BottomSheetDialog.BottomSheetButton {
        override val title: String
            get() = "Expiry in $days days"

        override fun execute() {
            viewModel.filterUser(UserFilter.ExpireFilter(days))
            dialog.dismiss()
        }
    }

    interface UserListAction {
        fun onItemClicked(userId: Long)
    }

    inner class UserListActionImpl: UserListAction {
        override fun onItemClicked(userId: Long) {
            val bundle = Bundle().apply {
                putString("state", UserActions.VIEW_USER.toString())
                putString("userId", userId.toString())
            }
            setFragmentResult("homeFragment", bundle)
            Log.d(TAG, "onItemClicked: UserListActionImpl")
            findNavController().navigate(R.id.action_homeFragment2_to_createUserFragment)
        }
    }
}
