package com.example.messanger.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.models.MessagePreview
import com.example.messanger.ui.adapters.recycler.MessageListAdapter
import com.example.messanger.viewmodels.MessengerViewModel
import com.example.messanger.ui.adapters.recycler.MessengerAdapter
import com.example.messanger.ui.adapters.recycler.difutills.MessagePreviewDiffUtils
import com.example.messanger.ui.adapters.recycler.difutills.NotificationDiffUtils
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.Lazy
import javax.inject.Inject


class MessengerFragment : Fragment(R.layout.fragment_messenger) {

    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: MessengerViewModel by navGraphViewModels(R.id.nav_graph) {
        factory.get()
    }

    private val TAG = "MessengerFragment"

    private val adapter = MessengerAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var countTv: TextView
    private lateinit var bottomBar:BottomNavigationView

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniViews()
        initRecyclerView()
        viewModel.getMessagePreview()

        viewModel.messagePreviewLiveData.observe(viewLifecycleOwner) {
            Log.e(TAG, "onViewCreated: $it")
            val diffres = DiffUtil.calculateDiff(MessagePreviewDiffUtils(adapter.getItems(), it))
            adapter.setItems(it)
            diffres.dispatchUpdatesTo(adapter)
            countTv.text="Chats: ${adapter.itemCount}"

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

    private fun iniViews() {
        recyclerView = requireView().findViewById(R.id.messenger_recycler_view)
        countTv=requireView().findViewById(R.id.messenger_count)
        bottomBar=(requireActivity()).findViewById<BottomNavigationView>(R.id.bottom_nav)
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
        adapter.setOnEventListener(object : MessengerAdapter.OnItemClickListener {
            override fun onClick(pos: Int) {
                val bundle = Bundle()
                bundle.putString("user", adapter.getItems()[pos].userId)
                Log.e(TAG, "onClick: ${adapter.getItems()[pos].userId}")
                findNavController().navigate(
                    R.id.action_messengerFragment_to_messageFragment,
                    bundle
                )
            }

        })
    }


}