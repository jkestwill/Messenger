package com.example.messanger.ui.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.messanger.R
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.MessagePreview
import com.example.messanger.other.DateFormat
import com.example.messanger.ui.adapters.recycler.base.BaseAdapter
import com.example.messanger.ui.adapters.recycler.base.BaseViewHolder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class MessengerAdapter() : BaseAdapter<MessagePreview>() {
    private var itemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onClick(pos: Int)
    }

    fun setOnEventListener(eventListener: OnItemClickListener) {
        this.itemClickListener = eventListener
    }

    fun detachEventListener() {
        itemClickListener = null
    }

    inner class ViewHolder(private var view: View) : BaseViewHolder<MessagePreview>(view) {

        private val photo: ImageView = view.findViewById(R.id.messenger_preview_photo)
        private val lastMessage: TextView = view.findViewById(R.id.messenger_preview_last_message)
        private val name: TextView = view.findViewById(R.id.messenger_preview_username)
        private val time: TextView = view.findViewById(R.id.messenger_preview_timestamp)
        private val isRead:View=view.findViewById(R.id.messenger_preview_is_read)

        override fun bind(model: MessagePreview) {
            lastMessage.text = model.lastMessage
            name.text = model.username
            time.text = DateFormat.format(model.timestamp as Long)
            if(model.idFrom!=CurrentUser.user.uid){
                isRead.isVisible=!model.isRead
            }

            if (model.imageUrl.isNotEmpty()) {
                Picasso.get().load(model.imageUrl)
                    .placeholder(R.color.image_background)
                    .centerCrop()
                    .resize(80, 80)
                    .transform(CropCircleTransformation())
                    .into(photo)
            }
                view.setOnClickListener {
                    itemClickListener?.onClick(adapterPosition)
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_preview_item, parent, false)
        return ViewHolder(inflater)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<MessagePreview>, position: Int) {
        holder.bind(getItems()[position])
    }


}