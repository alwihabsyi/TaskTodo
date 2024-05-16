package com.alwihbsyi.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alwihbsyi.core.databinding.ItemGalleryBinding
import com.alwihbsyi.core.domain.gallery.model.Gallery

class GalleryAdapter: Adapter<GalleryAdapter.GalleryViewHolder>() {
    inner class GalleryViewHolder(private val binding: ItemGalleryBinding): ViewHolder(binding.root) {
        fun bind(gallery: Gallery) {
            binding.apply {
                tvTitle.text = gallery.name

                itemView.setOnClickListener {
                    onClick?.invoke(gallery)
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Gallery>() {
        override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder(
            ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = if (differ.currentList.size > 3) 3 else differ.currentList.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val gallery = differ.currentList[position]
        holder.bind(gallery)
    }

    var onClick: ((Gallery) -> Unit)? = null
}