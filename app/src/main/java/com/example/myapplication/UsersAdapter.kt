package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemUserBinding

class UsersAdapter(private val onUserClick: (String, String) -> Unit) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var users = listOf<Pair<String, String>>() // Pair(userId, userName)

    fun submitList(newUsers: List<Pair<String, String>>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Pair<String, String>) {
            binding.userName.text = user.second
            binding.addUserButton.setOnClickListener {
                onUserClick(user.first, user.second)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size
}
