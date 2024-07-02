package com.example.gymreminder

import androidx.recyclerview.widget.DiffUtil
import com.example.gymreminder.data.UserSummary

class CustomDiffUtilCallback(private val list1: List<UserSummary>, private val list2: List<UserSummary>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return list1.size
    }

    override fun getNewListSize(): Int {
        return list2.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return list1[oldItemPosition].userId == list2[oldItemPosition].userId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return list1[oldItemPosition].equals(list2[newItemPosition])
    }
}