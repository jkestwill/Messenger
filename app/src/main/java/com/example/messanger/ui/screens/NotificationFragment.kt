package com.example.messanger.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.Error
import com.example.messanger.models.Success
import com.example.messanger.ui.adapters.recycler.NotificationListAdapter
import com.example.messanger.ui.adapters.recycler.difutills.NotificationDiffUtils
import com.example.messanger.viewmodels.NotificationViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import dagger.Lazy
import javax.inject.Inject


class NotificationFragment : Fragment(R.layout.fragment_notification) {
    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: NotificationViewModel by navGraphViewModels(R.id.nav_graph) {
        factory.get()
    }

    private val adapter = NotificationListAdapter()

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationCountView: TextView
    private lateinit var errorTv: TextView

    private val TAG = "NotificationFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initRecyclerView()
        viewModel.notificationLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    notificationCountView.text =
                        "${resources.getString(R.string.items)} ${it.result.size}"
                    if(it.result.isNotEmpty()) {
                        errorTv.isVisible=false
                        val diffres =
                            DiffUtil.calculateDiff(
                                NotificationDiffUtils(
                                    adapter.getItems(),
                                    ArrayList(it.result)
                                )
                            )
                        adapter.setItems(ArrayList(it.result))
                        diffres.dispatchUpdatesTo(adapter)
                    }
                    else {
                        errorTv.isVisible=true
                        errorTv.text=getString(R.string.no_notifications)
                    }
                }
                is Error -> {
                    errorTv.isVisible=true
                    errorTv.text = it.message
                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.receiveNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.detachEventListener()
    }

    private fun initViews() {
        recyclerView = requireView().findViewById(R.id.notification_recycler)
        notificationCountView = requireView().findViewById(R.id.notification_item_count)
        errorTv = requireView().findViewById(R.id.notification_error_view)
    }

    private fun initRecyclerView() {
        adapter.setOnEventListener(object : NotificationListAdapter.NotificationEventListener {
            override fun onAccept(pos: Int) {
                viewModel.acceptFriendRequest(adapter.getItemAt(pos).from)
            }

            override fun onDecline(pos: Int) {
                viewModel.declineFriendRequest(adapter.getItemAt(pos).from)
            }

        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }


}