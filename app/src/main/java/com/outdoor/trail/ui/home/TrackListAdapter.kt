package com.outdoor.trail.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.outdoor.trail.data.model.TrackListItem
import com.outdoor.trail.databinding.ItemTrackBinding

/**
 * 轨迹列表适配器，用于首页和搜索页的轨迹列表展示
 * 使用DiffUtil高效更新列表
 */
class TrackListAdapter(
    private val onItemClick: (TrackListItem) -> Unit
) : ListAdapter<TrackListItem, TrackListAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrackListItem>() {
            override fun areItemsTheSame(old: TrackListItem, new: TrackListItem) =
                old.trackId == new.trackId

            override fun areContentsTheSame(old: TrackListItem, new: TrackListItem) =
                old == new
        }
    }

    inner class ViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TrackListItem) {
            binding.tvTitle.text = item.title
            binding.tvRegion.text = item.region
            binding.tvDistance.text = formatDistance(item.distance)
            binding.tvDuration.text = formatDuration(item.duration)
            binding.tvCollectCount.text = "${item.collectCount}"
            binding.tvUserName.text = item.userNickname

            // 加载封面图
            if (item.coverImage.isNotEmpty()) {
                Glide.with(binding.root)
                    .load(item.coverImage)
                    .centerCrop()
                    .into(binding.ivCover)
            }

            // 加载用户头像
            if (item.userAvatar.isNotEmpty()) {
                Glide.with(binding.root)
                    .load(item.userAvatar)
                    .circleCrop()
                    .into(binding.ivUserAvatar)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** 格式化距离：米转千米 */
    private fun formatDistance(meters: Double): String {
        return if (meters >= 1000) {
            String.format("%.1f km", meters / 1000)
        } else {
            String.format("%.0f m", meters)
        }
    }

    /** 格式化时长：秒转时:分:秒 */
    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }
}
