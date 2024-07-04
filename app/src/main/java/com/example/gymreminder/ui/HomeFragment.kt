package com.example.gymreminder.ui

import android.content.Context
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
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
    private lateinit var searchView: EditText
    private lateinit var  userListAdapter: UserListAdapter
    private lateinit var userListRecyclerView: RecyclerView
    private lateinit var createButton: FloatingActionButton
    private lateinit var viewModel: HomeViewModel
    private lateinit var dialog: BottomSheetDialog
    private lateinit var closeButton: ImageView
    private lateinit var searchButton: ImageView
    private lateinit var filterButton: ImageView

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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    

    private fun configureViewModel() {
        val dao = UserDatabase.getInstance(activity as Context).getUserDao()
        val fetchAllUser = FetchAllUserImpl(dao)
        val filterUser = FilterUsersImpl(dao)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(fetchAllUser, filterUser))[HomeViewModel::class.java]
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
        searchView = binding.searchView
        closeButton = binding.closeIcon
        filterButton = binding.filterIcon
        searchButton = binding.searchIcon
        searchView.addTextChangedListener { text ->
            if(text?.isNotEmpty() == true) {
                closeButton.visibility = View.VISIBLE
                closeButton.setBackgroundTint(R.color.black)
                searchButton.setBackgroundTint(R.color.black)
                filterButton.setBackgroundTint(R.color.black)
                if(text.toString().last() == '\n') {
                    viewModel.filterUser(UserFilter.NameFilter(text.toString()))
                    val txt = text.toString().filter {
                        it != '\n'
                    }
                    searchView.setText(txt)
                    searchView.clearFocus()
                } else {
                    viewModel.filterUser(UserFilter.NameFilter(text.toString()))
                }
            } else {
                closeButton.visibility = View.INVISIBLE
            }

            closeButton.setOnClickListener {
                searchView.setText("")
                it.visibility = View.GONE
                searchButton.setBackgroundTint(R.color.lightGray)
                filterButton.setBackgroundTint(R.color.lightGray)
                closeButton.setBackgroundTint(R.color.lightGray)
            }
        }


        filterButton.setOnClickListener {
            showBottomSheet()
        }

        searchButton.setOnClickListener {
            val text = searchView.text.toString()
            if(text.length > 1) {
                viewModel.filterUser(UserFilter.NameFilter(text))
            }
        }
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
