package com.example.messanger.ui.adapters.recycler.difutills

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.models.Photo

class PhotoDiffUtil(private val oldList: List<Photo>, private val newList: List<Photo>) :
    DiffUtil.Callback() {


    override fun getOldListSize(): Int = oldList.size


    override fun getNewListSize(): Int = newList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].url == newList[oldItemPosition].url
        && oldList[oldItemPosition].uploadDate == newList[oldItemPosition].uploadDate

}