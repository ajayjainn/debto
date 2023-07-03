package com.example.debto

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.debto.databinding.ExpenseItemBinding

class SettlementAdapter(
    private val values: List<Pair<String,Float>>,private val onItemClicked: (String,String) -> Unit
) : ListAdapter<String, SettlementAdapter.ViewHolder>(DiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(
            ExpenseItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        Log.d("f",viewholder.contentView.text.toString())
        viewholder.itemView.setOnClickListener {
            onItemClicked(viewholder.idView.text.toString(),viewholder.contentView.text.toString())
        }
        return viewholder
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.first
        if(item.second>0){
            holder.contentView.setTextColor(Color.parseColor("#5fb49c"))
            holder.idView.setTextColor(Color.parseColor("#5fb49c"))
        }else if(item.second<0){
            holder.contentView.setTextColor(Color.parseColor("#CF6679"))
            holder.idView.setTextColor(Color.parseColor("#CF6679"))
        }
        holder.contentView.text = "₹ " + item.second.toString()
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}