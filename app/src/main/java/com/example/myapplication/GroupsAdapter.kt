package com.example.myapplication

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemGroupBinding
import com.example.myapplication.db.Group
import com.example.myapplication.ui.activity.GroupActivity

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    private var groups = listOf<Group>()

    fun submitList(newGroups: List<Group>) {
        groups = newGroups
        Log.e("GROUPS", groups.toString())
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.groupName.text = group.groupName // Corrected from group.name
            binding.groupDescription.text = "Created by: ${group.createdBy}" // Add appropriate description
            binding.root.setOnClickListener {
                val intent = Intent(it.context, GroupActivity::class.java)
                intent.putExtra("groupId", group.groupId)
                it.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount() = groups.size
}
