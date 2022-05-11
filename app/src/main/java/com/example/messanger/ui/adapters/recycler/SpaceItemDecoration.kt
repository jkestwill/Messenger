package com.example.messanger.ui.adapters.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private var space:Int):RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view)==0){
            outRect.top=space
        }
        outRect.apply {
            bottom=space
            right=space
            left=space
        }
    }
}