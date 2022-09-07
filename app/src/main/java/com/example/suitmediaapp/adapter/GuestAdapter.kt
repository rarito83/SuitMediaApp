package com.example.suitmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.databinding.ItemGuestBinding
import com.example.suitmediaapp.screen.guest.GuestView

class GuestAdapter: RecyclerView.Adapter<GuestAdapter.GuestViewHolder>() {

    private val dataUser = ArrayList<User>()
    private lateinit var listener: onItemClickCallback

    fun setData(list: List<User>) {
        if (list.isNullOrEmpty()) return
        this.dataUser.clear()
        this.dataUser.addAll(list)
        notifyDataSetChanged()
        Log.d("adapter", "data user list $dataUser")
    }

    fun setOnItemClickCallback(onItemClickCallback: GuestView) {
        this.listener = onItemClickCallback
    }

    interface onItemClickCallback {
        fun onItemClicked(dataUser: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val bind = ItemGuestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuestViewHolder(bind)
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        holder.bind(dataUser[position])
    }

    override fun getItemCount(): Int = dataUser.size

    inner class GuestViewHolder(private val binding: ItemGuestBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataUser: User) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(dataUser.avatar)
                    .into(imgUser)

                tvName.text = "${dataUser.first_name} ${dataUser.last_name}"
            }
        }
    }
}