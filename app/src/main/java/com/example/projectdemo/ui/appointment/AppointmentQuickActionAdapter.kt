package com.example.projectdemo.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.model.AppointmentAction
import com.example.projectdemo.databinding.ItemAppointmentDetailBinding
import com.example.projectdemo.databinding.ItemAppointmentQuickActionBinding

class AppointmentQuickActionAdapter :
    ListAdapter<AppointmentAction, AppointmentQuickActionAdapter.ActionViewHolder>(ActionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemAppointmentQuickActionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActionViewHolder(private val binding: ItemAppointmentQuickActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(action: AppointmentAction) {
            binding.apply {
                tvInitials.text = action.initials
                tvCustomerName.text = action.name
                tvServiceName.text = action.service

                // *** DYNAMIC STATUS HANDLING ***
                tvStatus.text = action.status
                val context = binding.root.context

                when (action.status) {
                    "Upcoming" -> {
                        tvStatusContainer.setBackgroundResource(R.drawable.shape_stat_background_upcoming)
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_upcoming))
                    }
                    "Pending" -> {
                        tvStatusContainer.setBackgroundResource(R.drawable.shape_stat_background_pending)
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending))
                    }
                    "Completed" -> {
                        tvStatusContainer.setBackgroundResource(R.drawable.shape_stat_background_completed)
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_completed))
                    }
                    else -> {
                        tvStatusContainer.setBackgroundResource(R.drawable.shape_stat_background_cancel)
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_cancel))
                    }
                }

                // Bind details
                bindDetailItem(detailPhone, R.drawable.ic_phone, "Phone No - ${action.phone}")
                bindDetailItem(detailDate, R.drawable.ic_calendar, action.date)
                bindDetailItem(detailTime, R.drawable.ic_time, action.time)
                bindDetailItem(detailPrice, R.drawable.ic_money, action.price)
            }
        }

        private fun bindDetailItem(itemBinding: ItemAppointmentDetailBinding, iconRes: Int, text: String) {
            itemBinding.imgIcon.setImageResource(iconRes)
            itemBinding.tvText.text = text
        }
    }

    class ActionDiffCallback : DiffUtil.ItemCallback<AppointmentAction>() {
        override fun areItemsTheSame(oldItem: AppointmentAction, newItem: AppointmentAction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppointmentAction, newItem: AppointmentAction): Boolean {
            return oldItem == newItem
        }
    }
}