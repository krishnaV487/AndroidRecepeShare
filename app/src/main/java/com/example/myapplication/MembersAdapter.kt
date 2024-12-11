package com.example.myapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemMemberBinding
import com.example.myapplication.ui.activity.Member

class MembersAdapter : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    private var members = listOf<Member>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }

    inner class MemberViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(member: Member) {
            binding.memberName.text = member.name
            binding.memberId.text = "ID: ${member.userId}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size
}
