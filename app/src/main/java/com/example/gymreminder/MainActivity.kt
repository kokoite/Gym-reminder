package com.example.gymreminder

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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


    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =   ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun Todos() {
        /*
        * Change Joining Date and Expiry Date to long
        * Create UIState sealed class
        * Implement filter useCase
        * Implement bottom sheet for choosing filters
        * Implement Diff util with recycler view
        * Create new Activity for new Screen
        * Create new Activity for detail Screen
        * Change icon for createButton
        * Improve adapter logic
        * Pagination in room
        *
        * _____________________________________________
        * Improve loader / Progress Bar
        *
        * */
    }

    private fun completed() {
        /*
        * Change Joining Date and Expiry Date to long
        * Create UIState sealed class
        * Implemented filter usecase
        * Pagination in room
        * Fragment for new screen
        * Fragment for detail screen
        * Improvde adapter logic
        * Change icon for createButton
        * */
    }

    private fun TodoSaturday() {
        /*
        * Implement photo clicking and storing mechanism
        * Hide keyboard and understand focus
        * added minor comment
        */
    }

    private fun TodoSunday() {
        /*
        * Use Fragment User as Edit User and Detail User
        * Handle permission cases
        * */
    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (currentFocus != null) {
//            Log.d(TAG, "dispatchTouchEvent: $currentFocus")
//            hideKeyboard()
//        }
//        return super.dispatchTouchEvent(ev)
//    }
}