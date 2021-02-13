package com.example.indydemo.indy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.indydemo.database.Credential
import com.example.indydemo.databinding.ListItemCredentialBinding


class WalletAdapter(private val clickListener: CredentialListener):
    ListAdapter<Credential, WalletAdapter.ViewHolder>(CredentialDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemCredentialBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Credential, clickListener: CredentialListener) {
            binding.credential = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCredentialBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CredentialDiffCallback : DiffUtil.ItemCallback<Credential>() {
    override fun areItemsTheSame(oldItem: Credential, newItem: Credential): Boolean {
        return oldItem.credentialId == newItem.credentialId
    }

    override fun areContentsTheSame(oldItem: Credential, newItem: Credential): Boolean {
        return oldItem == newItem
    }
}

class CredentialListener(val clickListener: (credentialId: Int) -> Unit) {
    fun onClick(credential: Credential) = credential.credentialId?.let { clickListener(it) }
}
