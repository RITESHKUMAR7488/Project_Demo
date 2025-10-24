package com.example.projectdemo.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.model.Appointment
import com.example.projectdemo.databinding.ItemAppointmentDetailBinding
import com.example.projectdemo.databinding.ItemAppointmentScheduleBinding

class AppointmentScheduleAdapter :
    ListAdapter<Appointment, AppointmentScheduleAdapter.AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppointmentViewHolder(private val binding: ItemAppointmentScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: Appointment) {
            binding.apply {
                tvInitials.text = appointment.initials
                tvCustomerName.text = appointment.name
                tvServiceName.text = appointment.service
                tvStatus.text = appointment.status

                // Bind details
                bindDetailItem(detailPhone, R.drawable.ic_phone, "Phone No - ${appointment.phone}")
                bindDetailItem(detailDate, R.drawable.ic_calendar, appointment.date)
                bindDetailItem(detailTime, R.drawable.ic_time, appointment.time)
                bindDetailItem(detailPrice, R.drawable.ic_money, appointment.price)

                // Set click listeners (can be hoisted to fragment with an interface)
                btnStartSession.setOnClickListener {
                    // Handle Start Session click
                }
                btnDetails.setOnClickListener {
                    // Handle Details click
                }
            }
        }

        private fun bindDetailItem(itemBinding: ItemAppointmentDetailBinding, iconRes: Int, text: String) {
            itemBinding.imgIcon.setImageResource(iconRes)
            itemBinding.tvText.text = text
        }
    }

    class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem.name == newItem.name && oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }
    }
}