package com.example.messanger.ui.adapters.recycler.difutills

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.models.MessagePreview

class MessagePreviewDiffUtils(private val oldList: List<MessagePreview>, private val newList:List<MessagePreview>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return  oldList[oldItemPosition].id==newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldList[oldItemPosition].lastMessage==newList[newItemPosition].lastMessage
    }
}