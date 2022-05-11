package com.example.messanger.ui.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView

import com.example.messanger.R
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.Date
import com.example.messanger.models.DateHeader
import com.example.messanger.models.Item
import com.example.messanger.models.Message
import com.example.messanger.other.DateFormat
import com.example.messanger.ui.adapters.recycler.base.BaseViewHolder
import com.example.messanger.ui.adapters.recycler.difutills.MessageDiffUtils
import java.util.*

class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listDiffer=AsyncListDiffer(this,MessageDiffUtils)

    inner class ItemViewHolder( view: View) : BaseViewHolder<Item<Message>>(view) {
        private var messageText: TextView = view.findViewById(R.id.message_text)
        private var date: TextView = view.findViewById(R.id.message_date)
        private var isReadView:View=view.findViewById(R.id.message_is_read)

        override fun bind(model: Item<Message>) {
            messageText.text = model.data.text
            date.text = DateFormat.format(model.data.timestamp as Long)
            isReadView.isVisible = !model.data.isRead
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder{
        val inflater = LayoutInflater.from(parent.context).inflate(viewType,parent,false)
      return  when (viewType) {
            R.layout.message_item_sent -> {
                ItemViewHolder(inflater)
            }
            R.layout.message_item_received->{
                ItemViewHolder(inflater)
            }
            R.layout.item_date_message_header->{
                DateViewHolder(inflater)
            }

            else->  DateViewHolder(inflater)
        }
    }

    inner class DateViewHolder(view: View) : BaseViewHolder< Date<Message>>(view) {
        private val dateView:TextView=view.findViewById(R.id.symbol)
        override fun bind(model: Date<Message>) {
            dateView.text=model.date
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(holder){
           is DateViewHolder->{

               holder.bind(listDiffer.currentList[position] as Date<Message>)

           }
           is ItemViewHolder->{
               holder.bind(listDiffer.currentList[position] as Item<Message>)
           }
       }
    }

    override fun getItemViewType(position: Int): Int {

        return when (val item: DateHeader<Message> = listDiffer.currentList[position]) {
            is Date -> {
                R.layout.item_date_message_header
            }
            is Item -> {
                if (item.data.fromId == CurrentUser.user.uid) {
                    R.layout.message_item_sent
                } else R.layout.message_item_received
            }
        }

    }

    fun setItems(items:List<DateHeader<Message>>){
        listDiffer.submitList(items)

    }
    fun getItems(): MutableList<DateHeader<Message>> =listDiffer.currentList

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }


}



