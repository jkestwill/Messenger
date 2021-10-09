package com.example.messanger.ui.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.messanger.R
import com.example.messanger.models.FriendRequest
import com.example.messanger.other.DateFormat
import com.example.messanger.ui.adapters.recycler.base.BaseAdapter
import com.example.messanger.ui.adapters.recycler.base.BaseViewHolder

class NotificationListAdapter() : BaseAdapter<FriendRequest>() {

    private var eventListener: NotificationEventListener? = null

    interface NotificationEventListener {
        fun onAccept(pos: Int)

        fun onDecline(pos: Int)
    }

    fun setOnEventListener(eventListener: NotificationEventListener) {
        this.eventListener = eventListener
    }

    fun detachEventListener() {
        eventListener = null
    }

    inner class ViewHolder(view: View) : BaseViewHolder<FriendRequest>(view) {

        private val title: TextView = view.findViewById(R.id.notification_title)
        private val time: TextView = view.findViewById(R.id.notification_time)
        private val buttonAccept: ImageButton = view.findViewById(R.id.notification_accept)
        private val buttonDecline: ImageButton = view.findViewById(R.id.notification_decline)

        override fun bind(model: FriendRequest) {

            title.text = model.title
            time.text = DateFormat.formatDay(model.timestamp as Long)


            buttonAccept.setOnClickListener {
                eventListener?.onAccept(adapterPosition)
            }
            buttonDecline.setOnClickListener {
                eventListener?.onDecline(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(inflater)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<FriendRequest>, position: Int) {
        holder.bind(getItems()[position])
    }

}