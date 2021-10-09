package com.example.messanger.ui.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.messanger.R
import com.example.messanger.ui.screens.FriendsFragment
import com.example.messanger.ui.screens.UserFragment

class UsersAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {
    private val PAGE_NUM=2
    override fun getItemCount(): Int {
        return PAGE_NUM
    }

    override fun createFragment(position: Int): Fragment {
      return when(position){
            0->{
                UserFragment()
            }
          else ->FriendsFragment()
      }
    }
}