package com.example.messanger.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.*
import com.example.messanger.services.notification.NotificationApi
import com.example.messanger.viewmodels.MessengerViewModel
import com.example.messanger.ui.adapters.recycler.MessageListAdapter
import com.example.messanger.ui.adapters.recycler.difutills.MessageDiffUtils
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import dagger.Lazy
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject


class MessageFragment : Fragment(R.layout.fragment_message) {

    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: MessengerViewModel by viewModels(){
        factory.get()
    }
    private val TAG = "MessageFragment"
    @Inject
    lateinit var notificationApi:NotificationApi

    private var currentUser: User=User()

    private val adapter= MessageListAdapter()

    private lateinit var toolbarUsername: TextView
    private lateinit var toolbarUserPhoto:ShapeableImageView
    private lateinit var sendButton: Button
    private lateinit var editMessage:EditText
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLiveData.observe(viewLifecycleOwner){
            updateUser(it)
            currentUser=it
            viewModel.getMessages(it)
            Log.e(TAG, "onViewCreated: ${it.username}")


        }

        initViews()
        initRecyclerView()

        sendButton.setOnClickListener{
            if(editMessage.text.isNotEmpty()){
                viewModel.sendMessage(currentUser, editMessage.text.toString())
                viewModel.sendMessageNotification(NotificationRequest(currentUser.deviceToken,
                MessageNotification(CurrentUser.user.username,editMessage.text.toString(),CurrentUser.user.uid)))
                editMessage.text.clear()
            }
        }

        viewModel.lastMessageLiveDate.observe(viewLifecycleOwner){

            adapter.setItems(it)

            recyclerView.scrollToPosition(adapter.itemCount-1)

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        currentUser.uid= arguments?.get("user") as String
        viewModel.observeUserChanges( currentUser.uid)

    }


    private fun initViews(){
        sendButton=requireView().findViewById(R.id.message_send_button)
        editMessage=requireView().findViewById(R.id.message_input)
        recyclerView=requireView().findViewById(R.id.message_recycler_view)
        toolbarUserPhoto=requireView().findViewById(R.id.message_toolbar_user_image)
        toolbarUsername=requireView().findViewById(R.id.message_toolbar_username)
    }

    private fun initRecyclerView(){
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        recyclerView.itemAnimator?.changeDuration = 0;

    }

    private fun updateUser(user: User){
        if(user.photoUrl.isNotEmpty()){
            Picasso.get()
                .load(user.photoUrl)
                .centerCrop()
                .resize(60,60)
                .transform(CropCircleTransformation())
                .into(toolbarUserPhoto)
        }
        toolbarUsername.text=user.username
    }


}