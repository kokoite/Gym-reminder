package com.example.gymreminder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gymreminder.data.UserSummary
import com.example.gymreminder.databinding.UserSummaryBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.log

class UserListAdapter(private val userListImpl: HomeFragment.UserListAction): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    // TODO :- Implement Diff Util with recycler view
    companion object {
        private const val TAG = "GymApp"
    }
    private var _userList: List<UserSummary> = listOf()
    val userList: List<UserSummary> get() = _userList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(userList[position])
    }

    fun submitList(list: List<UserSummary>) {
        _userList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: UserSummaryBinding): RecyclerView.ViewHolder(binding.root) {

        var userImage: ImageView = binding.userImage
        private var userName: TextView = binding.name
        private var joiningDate: TextView = binding.joiningDate
        private var expiryDate: TextView = binding.expiryDate
        private var phoneNumber: TextView = binding.phoneNumber

        init {

            binding.root.setOnClickListener {
                Log.d(TAG, ": item clicked $adapterPosition ")
                userListImpl.onItemClicked(adapterPosition)
            }
        }

        fun bind(userSummary: UserSummary) {
            userName.text = userSummary.name
            phoneNumber.text = userSummary.phone
            expiryDate.text = convertLongToDate(userSummary.expiryDate)
            joiningDate.text = convertLongToDate(userSummary.joiningDate)
        }

        private fun convertLongToDate(time: Long): String {
            val instant = Instant.ofEpochMilli(time)
            val localDateTime = LocalDateTime.ofInstant(instant,ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
            return localDateTime.format(formatter).toString()
        }
    }
}