package com.example.debto

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.debto.databinding.DetailFragmentItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailFragmentAdapter(
    private val values: ArrayList<Triple<String,Float,Long>>
) : ListAdapter<String, DetailFragmentAdapter.ViewHolder>(DiffCallback())  {

    private val dateFormat = SimpleDateFormat("d MMMM, h:mma", Locale.getDefault())

    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DetailFragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.amount.text = "₹ " +  item.second.toString()
        holder.time.text = dateFormat.format(item.third)
        holder.comment.text = item.first
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: DetailFragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val comment: TextView = binding.comment
        val amount: TextView = binding.amount
        val time: TextView = binding.time

    }

}