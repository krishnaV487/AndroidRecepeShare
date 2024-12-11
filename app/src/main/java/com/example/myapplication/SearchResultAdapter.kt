package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemMemberBinding

class SearchResultAdapter(
    private val onItemClick: (String, String) -> Unit // userId/groupId and name
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var items = listOf<Pair<String, String>>() // List of ID and Name

    fun submitList(newItems: List<Pair<String, String>>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>) {
            binding.memberName.text = item.second // Name
            binding.memberId.text = "ID: ${item.first}" // ID

            binding.root.setOnClickListener {
                onItemClick(item.first, item.second)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
