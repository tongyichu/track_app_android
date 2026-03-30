package com.example.outdoortrack.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outdoortrack.data.model.TrackListItem
import com.example.outdoortrack.databinding.ItemTrackBinding

/**
 * 首页/搜索轨迹列表适配器。
 */
class TrackListAdapter(
    private val onItemClick: (TrackListItem) -> Unit
) : ListAdapter<TrackListItem, TrackListAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<TrackListItem>() {
        override fun areItemsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean =
            oldItem == newItem
    }

    inner class VH(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TrackListItem) {
            binding.tvName.text = item.name
            binding.tvMeta.text = "${item.distanceMeters ?: 0.0} m / ${item.durationSeconds ?: 0L} s"
            Glide.with(binding.ivThumb)
                .load(item.thumbnailUrl)
                .into(binding.ivThumb)
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
