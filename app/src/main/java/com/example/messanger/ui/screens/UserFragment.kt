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
import javax.inject.Inject
import dagger.Lazy


class UserFragment : Fragment(R.layout.fragment_users) {
    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val adapter: UsersListAdapter = UsersListAdapter()
    private lateinit var recyclerView: RecyclerView

    private val viewModel: UsersViewModel by navGraphViewModels(R.id.nav_graph) {
        factory.get()
    }
    private lateinit var errorTv: TextView

    private val TAG = "UserFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllUsers()
        initViews()
        iniRecyclerView()
        viewModel.usersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    errorTv.isVisible = false
                    val diffres =
                        DiffUtil.calculateDiff(UserDiffUtil(adapter.getItems(), it.result))
                    adapter.setItems(it.result)
                    diffres.dispatchUpdatesTo(adapter)
                    Log.e(TAG, "onViewCreated: ${it.result}")

                }
                is Error -> {
                    errorTv.isVisible = true
                    errorTv.text = it.message
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.detachEventListener()
    }

    private fun initViews() {
        recyclerView = requireView().findViewById(R.id.users_recycler_view)
        errorTv = requireView().findViewById(R.id.users_error)
    }

    private fun iniRecyclerView() {
        adapter.setOnEventListener(object : UsersListAdapter.FriendsEventListener {
            override fun onMessage(pos: Int, imageButton: ImageButton) {
                val bundle = Bundle()
                bundle.putSerializable("user", adapter.getItemAt(pos).uid)
                findNavController().navigate(
                    R.id.action_usersViewPagerFragment_to_messageFragment,
                    bundle
                )
            }

            override fun onAdd(pos: Int, view: ToggleButton) {
                val user = adapter.getItemAt(pos)
                viewModel.addFriendRequest(user)
                Log.e(TAG, "onAdd:${user.deviceToken} ")
                viewModel.sendFriendRequestNotification(
                    NotificationRequest(
                        user.deviceToken,
                        FriendRequest(
                            resources.getString(R.string.friend_req),
                            "${user.username} ${resources.getString(R.string.wants_add_u)}"
                        )
                    )
                )
                Toast.makeText(requireContext(),resources.getString(R.string.friend_req_sent),Toast.LENGTH_SHORT).show()

            }

            override fun onRemove(pos: Int, view: ToggleButton) {
                val user = adapter.getItemAt(pos)

                viewModel.removeFriend(user.uid)
                viewModel.removeFriendRequest(user.uid)

            }

        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

}