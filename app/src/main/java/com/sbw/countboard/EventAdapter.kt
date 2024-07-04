package com.sbw.countboard

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbw.countboard.databinding.ItemEventBinding

/**
@description TODO
@author sbw
@create 2024-06-27 0:36
@version 1.0
 */
class EventAdapter(private val events: MutableList<Event>,
                   private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(event: Event)
    }

    inner class ViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return holder
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.binding.titleTv.text = event.title
        holder.binding.doneTimeTv.text = event.doneTime
        holder.binding.leftTimeTv.text= event.leftTime
        holder.binding.delImg.setOnClickListener { itemClickListener.onItemClick(event) }
    }

    override fun getItemCount(): Int = events.size

}