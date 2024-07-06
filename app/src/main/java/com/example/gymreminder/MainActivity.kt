package com.example.gymreminder

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymreminder.data.TAG
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.databinding.ActivityMainBinding
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.CreateUserImpl
import com.example.gymreminder.usecase.FetchAllUser
import com.example.gymreminder.usecase.FetchAllUserImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =   ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun Todo() {
        /*
        * isActive, Payment Amount in DB
        *
        */
    }
}