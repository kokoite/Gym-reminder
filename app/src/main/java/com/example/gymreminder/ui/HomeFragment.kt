package com.example.gymreminder.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymreminder.HomeViewModel
import com.example.gymreminder.HomeViewModelFactory
import com.example.gymreminder.R
import com.example.gymreminder.UserActions
import com.example.gymreminder.data.TAG
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.databinding.FragmentHomeBinding
import com.example.gymreminder.utility.hideKeyboard
import com.example.gymreminder.usecase.CreateUserImpl
import com.example.gymreminder.usecase.FetchAllUserImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var  userListAdapter: UserListAdapter
    private lateinit var userListRecyclerView: RecyclerView
    private lateinit var createButton: FloatingActionButton
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        val createUser = CreateUserImpl(dao)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(fetchAllUser, createUser))[HomeViewModel::class.java]
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
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                handleQuerySubmit(query?:"")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    handleQueryTextChange(it)
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

    private fun handleQuerySubmit(text: String) {

    }

    private fun handleQueryTextChange(text: String) {

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



