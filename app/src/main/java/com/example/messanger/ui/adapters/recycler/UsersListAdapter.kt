package com.example.messanger.ui.adapters.recycler


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import com.example.messanger.R
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.User
import com.example.messanger.ui.adapters.recycler.base.BaseAdapter
import com.example.messanger.ui.adapters.recycler.base.BaseViewHolder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.lang.Exception

class UsersListAdapter() : BaseAdapter<User>() {
    private var eventListener: FriendsEventListener? = null

    interface FriendsEventListener {
        fun onMessage(pos: Int, imageButton: ImageButton)
        fun onAdd(pos: Int, view: ToggleButton)
        fun onRemove(pos: Int, view: ToggleButton)
    }


    fun setOnEventListener(eventListener: FriendsEventListener) {
        this.eventListener = eventListener
    }

    fun detachEventListener() {
        eventListener = null
    }


    inner class FriendViewHolder(view: View) : BaseViewHolder<User>(view) {
        private val username: TextView = view.findViewById(R.id.friend_username)
        private val email: TextView = view.findViewById(R.id.friend_email)
        private val image: ImageView = view.findViewById(R.id.friend_item_image)
        private val add: ToggleButton = view.findViewById(R.id.friend_add)
        private val newMessage: ImageButton = view.findViewById(R.id.friend_new_message)


        override fun bind(model: User) {

            username.text = model.username
            email.text = model.email
            if (model.photoUrl.isNotBlank()) {
                Picasso.get().load(model.photoUrl)
                    .centerCrop()
                    .resize(80, 80)
                    .transform(CropCircleTransformation())
                    .into(image)
            }

//            if (model.friends.containsKey(CurrentUser.user.uid)
//            ) {
//                add.isChecked = true
//
//            }
            add.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    eventListener?.onAdd(adapterPosition, add)
                } else {
                    eventListener?.onRemove(adapterPosition, add)
                }
            }
            newMessage.setOnClickListener {
                eventListener?.onMessage(adapterPosition, newMessage)
            }


        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<User> {
        val inflater = LayoutInflater.from(parent.context)
        return FriendViewHolder(inflater.inflate(R.layout.item_friend, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<User>, position: Int) {
        holder.bind(getItems()[position])
    }


}