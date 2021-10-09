package com.example.messanger.ui.adapters.recycler.difutills

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.models.Date
import com.example.messanger.models.DateHeader
import com.example.messanger.models.Item
import com.example.messanger.models.Message

object MessageDiffUtils

 : DiffUtil.ItemCallback<DateHeader<Message>>() {


    override fun areItemsTheSame(
        oldItem: DateHeader<Message>,
        newItem: DateHeader<Message>
    ): Boolean {
        val isSameMessages =
            oldItem is Item && newItem is Item && oldItem.data.to == newItem.data.to
        val isDateIsSame = oldItem is Date && newItem is Date && oldItem.date == newItem.date
        return isSameMessages || isDateIsSame
    }

    override fun areContentsTheSame(
        oldItem: DateHeader<Message>,
        newItem: DateHeader<Message>
    ): Boolean {

        return oldItem is Item && newItem is Item && oldItem.data.to == newItem.data.to &&
                oldItem.data.text == newItem.data.text && oldItem.data.isRead==newItem.data.isRead
    }
}