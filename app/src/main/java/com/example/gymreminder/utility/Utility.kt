package com.example.gymreminder.utility

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.Calendar


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

fun ImageView.setBackgroundTint(id: Int) {
    setColorFilter(
        ContextCompat.getColor(context, id),  // Get color from resource
        PorterDuff.Mode.MULTIPLY  // Apply the tint by multiplying with the original image colors
    )
}
