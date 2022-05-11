package com.example.messanger.ui.adapters.recycler

import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.messanger.databinding.ImageSlideLayoutBinding
import com.example.messanger.models.Photo
import com.example.messanger.ui.adapters.recycler.base.BaseAdapter
import com.example.messanger.ui.adapters.recycler.base.BaseViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class ImageListAdapter() : BaseAdapter<Photo>() {

    class ImageViewHolder(private val binding: ImageSlideLayoutBinding) :
        BaseViewHolder<Photo>(binding.root) {
        override fun bind(model: Photo) {
//            binding.image.maxHeight=model.height

            Picasso.get()
                .load(model.url)
                .noFade()
                .noPlaceholder()
                .resize(model.width,model.height)
                .into(binding.image)

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Photo> {
        val binding =
            ImageSlideLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Photo>, position: Int) {
        holder.bind(itemList[position])
    }



}