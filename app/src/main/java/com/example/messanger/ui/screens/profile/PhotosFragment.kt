package com.example.messanger.ui.screens.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.messanger.App
import com.example.messanger.databinding.FragmentPhotosBinding
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.Success
import com.example.messanger.ui.adapters.recycler.ImageListAdapter
import com.example.messanger.ui.adapters.recycler.SpaceItemDecoration
import com.example.messanger.ui.adapters.recycler.difutills.PhotoDiffUtil
import com.example.messanger.ui.screens.BaseFragment
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.example.messanger.viewmodels.profile.PhotosViewModel
import dagger.Lazy
import javax.inject.Inject

class PhotosFragment : BaseFragment<FragmentPhotosBinding>(FragmentPhotosBinding::inflate) {

    @Inject
    lateinit var factory: Lazy<ViewModelProviderFactory>

    private val viewModel: PhotosViewModel by viewModels { factory.get() }

    private val adapter = ImageListAdapter()
    private val TAG = "PhotosFragment"

    companion object {
        const val USER_ID_KEY="userId"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPhotoAdapter()
        // не отображаются остальные фото тк токен старый
            // где-то в коде есть решение этой проблемы
        viewModel.photos.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    Log.e(TAG, "Photos:${it.result} ")
//                    val diffUtils=DiffUtil.calculateDiff(PhotoDiffUtil(adapter.itemList,it.result))
                    adapter.setItems(it.result)
//                    diffUtils.dispatchUpdatesTo(adapter)
                }

                is Loading -> {
                    Log.e(TAG, "onViewCreated: loading")
                }

                is Error -> {
                    Log.e(TAG, "onViewCreated error: ${it.message}")
                }
            }
        }

        binding?.buttonBack?.buttonBack?.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initPhotoAdapter() {
        binding?.imageRv?.adapter = adapter
        binding?.imageRv?.addItemDecoration(SpaceItemDecoration(10))
        binding?.imageRv?.layoutManager =
            StaggeredGridLayoutManager( 2, GridLayoutManager.VERTICAL).also {
                it.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        arguments?.getString(USER_ID_KEY)?.let {
            viewModel.getAllPhotosUrl(USER_ID_KEY)
        }

    }
}