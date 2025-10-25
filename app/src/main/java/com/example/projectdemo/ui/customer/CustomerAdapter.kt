package com.example.projectdemo.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.data.model.Customer
import com.example.projectdemo.data.model.CustomerListItem
import com.example.projectdemo.databinding.ItemCustomerDetailsBinding
import com.example.projectdemo.databinding.ItemCustomerHeaderBinding
import com.example.projectdemo.databinding.ItemCustomerInfoChipBinding

// Define view types for our two different layouts
private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_CUSTOMER = 1

class CustomerAdapter :
    ListAdapter<CustomerListItem, RecyclerView.ViewHolder>(CustomerDiffCallback()) {

    /**
     * This function tells the adapter which layout to use for a given position.
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CustomerListItem.HeaderItem -> VIEW_TYPE_HEADER
            is CustomerListItem.CustomerItem -> VIEW_TYPE_CUSTOMER
        }
    }

    /**
     * This creates the correct ViewHolder based on the viewType.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemCustomerHeaderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_CUSTOMER -> CustomerViewHolder(
                ItemCustomerDetailsBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    /**
     * This binds the data to the correct ViewHolder.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CustomerListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item.title)
            is CustomerListItem.CustomerItem -> (holder as CustomerViewHolder).bind(item.customer)
        }
    }


    /** ViewHolder for the "Today" / "Yesterday" header */
    inner class HeaderViewHolder(private val binding: ItemCustomerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvHeaderTitle.text = title
        }
    }

    /** ViewHolder for the customer card */
    inner class CustomerViewHolder(private val binding: ItemCustomerDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: Customer) {
            binding.apply {
                tvInitials.text = customer.initials
                tvCustomerName.text = customer.name
                tvPhone.text = customer.phone
                tvPrice.text = customer.price

                // *** CORRECTED CHIP LOGIC ***
                infoChipGroup.removeAllViews() // Clear old chips from reuse

                customer.tags.take(3).forEachIndexed { index, tag ->
                    // Inflate the chip layout
                    val chipBinding = ItemCustomerInfoChipBinding.inflate(
                        LayoutInflater.from(binding.root.context),
                        infoChipGroup,
                        false // Do not attach to root yet
                    )
                    chipBinding.tvInfoChip.text = tag.label

                    // Add margin to all chips except the first one
                    if (index > 0) {
                        // We create layout params to set the margin
                        val params = chipBinding.root.layoutParams as ViewGroup.MarginLayoutParams
                        params.marginStart = 8 // 8dp
                        chipBinding.root.layoutParams = params
                    }

                    // Add the new chip to the LinearLayout
                    infoChipGroup.addView(chipBinding.root)
                }
            }
        }
    }

    // --- DiffUtil ---

    class CustomerDiffCallback : DiffUtil.ItemCallback<CustomerListItem>() {
        override fun areItemsTheSame(oldItem: CustomerListItem, newItem: CustomerListItem): Boolean {
            return (oldItem is CustomerListItem.HeaderItem && newItem is CustomerListItem.HeaderItem && oldItem.title == newItem.title) ||
                    (oldItem is CustomerListItem.CustomerItem && newItem is CustomerListItem.CustomerItem && oldItem.customer.id == newItem.customer.id)
        }

        override fun areContentsTheSame(oldItem: CustomerListItem, newItem: CustomerListItem): Boolean {
            return oldItem == newItem
        }
    }
}