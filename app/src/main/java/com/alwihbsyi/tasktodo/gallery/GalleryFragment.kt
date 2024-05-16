package com.alwihbsyi.tasktodo.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.ui.GalleryAdapter
import com.alwihbsyi.core.utils.getMimeType
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.FragmentGalleryBinding
import com.alwihbsyi.tasktodo.media.MediaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.HttpURLConnection
import java.net.URL

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<GalleryViewModel>()
    private val galleryAdapter by lazy { GalleryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            btnAdd.setOnClickListener {
                startActivity(Intent(requireContext(), AddGalleryActivity::class.java))
            }
        }
    }

    private fun observer() {
        viewModel.getGalleries().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    toast(it.message ?: "")
                }

                is Resource.Loading -> {}
                is Resource.Success -> {
                    it.data?.let { galleries ->
                        setUpGallery(galleries)
                    }
                }
            }
        }
    }

    private fun setUpGallery(galleries: List<Gallery>) {
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = galleryAdapter
        }

        galleryAdapter.differ.submitList(galleries)
        galleryAdapter.onClick = { gallery ->
            lifecycleScope.launch {
                val mimeType = getMimeType(gallery.file!!)

                when {
                    mimeType.startsWith("video") -> playVideo(gallery.file!!)
                    else -> openFile(gallery, mimeType)
                }
            }
        }
    }

    private fun playVideo(file: String) {
        startActivity(Intent(requireContext(), MediaActivity::class.java).apply {
            putExtra(MediaActivity.VID_URL, file)
        })
    }

    private fun openFile(gallery: Gallery, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(gallery.file), mimeType)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        observer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}