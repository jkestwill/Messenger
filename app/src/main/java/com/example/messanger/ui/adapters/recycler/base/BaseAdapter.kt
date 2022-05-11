package com.example.messanger.ui.adapters.recycler.base

import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<T>:RecyclerView.Adapter<BaseViewHolder< T>>() {
      val itemList=ArrayList<T>()


   open fun setItems(items:List<T>){
        itemList.clear()
        itemList.addAll(items)
       notifyDataSetChanged()
    }


    open fun addItem(item: T) {
        itemList.add(item)
        notifyItemChanged(itemList.size-1)
    }

    fun removeItemAt(position: Int){
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateItemAt(position: Int,item:T){
        itemList[position]=item
        notifyItemChanged(position)
    }

    fun getItems(): ArrayList<T> {
        return itemList
    }

    fun getItemAt(position: Int): T {
        return itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}