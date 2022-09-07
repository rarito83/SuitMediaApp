package com.example.suitmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suitmediaapp.R
import com.example.suitmediaapp.databinding.ItemEventBinding
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.screen.event.EventView

class EventAdapter: RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val dataUser = ArrayList<User>()
    private lateinit var listener: onItemClickCallback

    fun setData(list: List<User>) {
        Log.d("Adapter Event", "Data Users $list")
        if (dataUser.isNullOrEmpty()) return
        with(this.dataUser) {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: EventView) {
        this.listener = onItemClickCallback
    }

    interface onItemClickCallback {
        fun onItemClicked(data: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(dataUser[position])
    }

    override fun getItemCount() = dataUser.size

    inner class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemEventBinding.bind(itemView)

        fun bind(data: User) {
            with(binding) {
                tvTitleEvent.text = data.first_name
                tvDescriptionEvent.text = data.last_name
                tvDate.text = "22 Feb 2022"
                tvTime.text = "11.15 PM"
                imgPhotoEvent.apply {
                    data.avatar
                }
            }
        }
    }
}