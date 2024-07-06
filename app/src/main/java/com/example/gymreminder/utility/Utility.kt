package com.example.gymreminder.utility

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gymreminder.usecase.FilterUsersImpl
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    return "$day-${month+1}-$year"
}

fun convertToStringToMillis(curDate: String): Long {
    val dateFormat = SimpleDateFormat("d-MM-yyyy", Locale.getDefault())
    var timeInMilis: Long = 0
    try {
        timeInMilis = dateFormat.parse(curDate)?.time ?: throw IllegalArgumentException("Invalid format")
    } catch (error: Error) {
        Log.d(FilterUsersImpl.TAG, "convertToStringToMilis: Date format is wrong")
    }
    return timeInMilis
}

fun ImageView.setBackgroundTint(id: Int) {
    setColorFilter(
        ContextCompat.getColor(context, id),  // Get color from resource
        PorterDuff.Mode.MULTIPLY  // Apply the tint by multiplying with the original image colors
    )
}
