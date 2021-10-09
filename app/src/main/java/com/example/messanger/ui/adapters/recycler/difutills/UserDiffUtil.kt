package com.example.messanger.ui.adapters.recycler.difutills

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.models.User


class UserDiffUtil(private val oldList: List<User>,
                   private val newList:List<User>):DiffUtil.Callback() {

    override fun getOldListSize(): Int {
    return oldList.size
    }

    override fun getNewListSize(): Int {
       return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldList[oldItemPosition].uid==newList[newItemPosition].uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {


        return oldList[oldItemPosition].email==newList[newItemPosition].email &&
                oldList[oldItemPosition].username==newList[newItemPosition].username &&
                oldList[oldItemPosition].photoUrl==newList[newItemPosition].photoUrl &&
                oldList[oldItemPosition].friends==newList[newItemPosition].friends

    }

}