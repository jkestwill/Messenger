package com.example.messanger.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.messanger.R
import com.example.messanger.ui.adapters.viewpager.UsersAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class UsersViewPagerFragment : Fragment(R.layout.fragment_users_view_pager) {

private lateinit var viewpager:ViewPager2
private lateinit var tabsLayout: TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager=requireView().findViewById(R.id.user_view_pager)
        tabsLayout=requireView().findViewById(R.id.tab_layout)
        viewpager.adapter=UsersAdapter(this)
        TabLayoutMediator(tabsLayout,viewpager){tab, position->
            when(position){
                0->tab.text="Users"
                1->tab.text="Friends"
            }
        }.attach()
    }



}