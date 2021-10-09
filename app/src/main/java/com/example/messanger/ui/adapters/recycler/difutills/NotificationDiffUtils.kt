package com.example.messanger.ui.adapters.recycler.difutills


import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.models.FriendRequest

class NotificationDiffUtils(
    private val oldList: List<FriendRequest>,
    private val newList: List<FriendRequest>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
      return  newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition].title==newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title==newList[newItemPosition].title
    }
}