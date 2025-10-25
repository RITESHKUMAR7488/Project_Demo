package com.example.projectdemo.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.model.Profile
import com.example.projectdemo.data.model.ProfileListItem
import com.example.projectdemo.data.model.ProfileMenuItem
import com.example.projectdemo.databinding.ItemProfileHeaderBinding
import com.example.projectdemo.databinding.ItemProfileMenuBinding
import com.example.projectdemo.databinding.ItemProfileSignoutBinding

// Define view types for our three different layouts
private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1
private const val VIEW_TYPE_SIGNOUT = 2

class ProfileAdapter :
    ListAdapter<ProfileListItem, RecyclerView.ViewHolder>(ProfileDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is ProfileListItem.Header -> VIEW_TYPE_HEADER
            is ProfileListItem.Item -> {
                if (item.menuItem.icon == "sign_out") {
                    VIEW_TYPE_SIGNOUT
                } else {
                    VIEW_TYPE_ITEM
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemProfileHeaderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_ITEM -> ItemViewHolder(
                ItemProfileMenuBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_SIGNOUT -> SignOutViewHolder(
                ItemProfileSignoutBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ProfileListItem.Header -> (holder as HeaderViewHolder).bind(item.profile)
            is ProfileListItem.Item -> {
                when (holder) {
                    is ItemViewHolder -> holder.bind(item.menuItem)
                    is SignOutViewHolder -> holder.bind(item.menuItem)
                }
            }
        }
    }

    // --- ViewHolders ---

    inner class HeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: Profile) {
            binding.tvInitials.text = profile.initials
            binding.tvCustomerName.text = profile.name
            binding.tvPhone.text = profile.phone
        }
    }

    inner class ItemViewHolder(private val binding: ItemProfileMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: ProfileMenuItem) {
            binding.tvTitle.text = menuItem.title
            binding.imgChevron.visibility = View.VISIBLE

            // Set icon based on the string from JSON
            val iconRes = when (menuItem.icon) {
                "about" -> R.drawable.ic_about // <-- ADD THIS LINE
                "gallery" -> R.drawable.ic_gallery
                "quick_set" -> R.drawable.ic_quick_set
                "languages" -> R.drawable.ic_languages
                "specification" -> R.drawable.ic_specification
                else -> R.drawable.ic_gallery // Default
            }
            binding.imgIcon.setImageResource(iconRes)

            binding.root.setOnClickListener {
                // Handle item click
                Toast.makeText(it.context, "${menuItem.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class SignOutViewHolder(private val binding: ItemProfileSignoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: ProfileMenuItem) {
            binding.root.setOnClickListener {
                // Handle sign out click
                Toast.makeText(it.context, "${menuItem.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- DiffUtil ---

    class ProfileDiffCallback : DiffUtil.ItemCallback<ProfileListItem>() {
        override fun areItemsTheSame(oldItem: ProfileListItem, newItem: ProfileListItem): Boolean {
            return (oldItem is ProfileListItem.Header && newItem is ProfileListItem.Header && oldItem.profile.name == newItem.profile.name) ||
                    (oldItem is ProfileListItem.Item && newItem is ProfileListItem.Item && oldItem.menuItem.title == newItem.menuItem.title)
        }

        override fun areContentsTheSame(oldItem: ProfileListItem, newItem: ProfileListItem): Boolean {
            return oldItem == newItem
        }
    }
}