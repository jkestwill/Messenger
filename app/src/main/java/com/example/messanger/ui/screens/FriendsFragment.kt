package com.example.messanger.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.*
import com.example.messanger.ui.adapters.recycler.UsersListAdapter
import com.example.messanger.ui.adapters.recycler.difutills.UserDiffUtil
import com.example.messanger.viewmodels.UsersViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import dagger.Lazy
import javax.inject.Inject


class FriendsFragment : Fragment(R.layout.fragment_friends) {
    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: UsersViewModel by navGraphViewModels(R.id.nav_graph){
        factory.get()
    }
    private lateinit var recyclerView: RecyclerView
    private var adapter= UsersListAdapter()

    private lateinit var errorTv: TextView
    private val TAG = "FriendsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initRecyclerView()
        viewModel.friendsLiveData.observe(viewLifecycleOwner){
            when(it){
                is Success->{
                    errorTv.isVisible = it.result.isEmpty()
                    val diffres =
                        DiffUtil.calculateDiff(UserDiffUtil(adapter.getItems(), it.result))
                    adapter.setItems(it.result)
                    diffres.dispatchUpdatesTo(adapter)
                    Log.e(TAG, "onViewCreated: ",)
                }
                is Error->{
                    Log.e(TAG, "onViewCreated: ${it.message}")
                    errorTv.isVisible=true
                    errorTv.text=it.message
                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel.getFriends()
    }

    private fun initViews(){
        recyclerView=requireView().findViewById(R.id.friends_recycler_view)
        errorTv=requireView().findViewById(R.id.friends_no_friends)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.detachEventListener()
    }

    private fun initRecyclerView(){
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter.setOnEventListener(object : UsersListAdapter.FriendsEventListener{

            override fun onMessage(pos: Int,imageButton: ImageButton) {
                val bundle=Bundle()
                bundle.putSerializable("user",adapter.getItemAt(pos).uid)
                findNavController().navigate(R.id.action_usersViewPagerFragment_to_messageFragment,bundle)
            }

            override fun onRemove(pos: Int, view: ToggleButton) {
                val user = adapter.getItemAt(pos)

                viewModel.removeFriend(user.uid)
                viewModel.removeFriendRequest(user.uid)
            }

            override fun onAdd(pos: Int, view: ToggleButton) {
                val user = adapter.getItemAt(pos)
                viewModel.addFriendRequest(user)
                Log.e(TAG, "onAdd:${user.deviceToken} ")
                viewModel.sendFriendRequestNotification(
                    NotificationRequest(
                        user.deviceToken,
                        FriendRequest(
                            "Friend request",
                            "${user.username} wants add you as a friend"
                        )
                    )
                )
                Toast.makeText(requireContext(), "Sending friend request", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


}