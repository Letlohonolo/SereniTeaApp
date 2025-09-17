package com.fake.sereniteaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class JournalAdapter(
    private var items: List<JournalEntity>,
    private val onClick: (JournalEntity) -> Unit
) : RecyclerView.Adapter<JournalAdapter.Holder>() {

    fun submitList(newList: List<JournalEntity>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.cardTitle)
        val tvPreview: TextView = v.findViewById(R.id.txtPreview)
        val tvTime: TextView = v.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_journal_entry, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val entry = items[position]
        holder.tvTitle.text = entry.title
        holder.tvPreview.text = entry.content
        holder.tvTime.text = android.text.format.DateFormat.format("dd/MM", entry.createdAt)
        holder.itemView.setOnClickListener { onClick(entry) }
    }

    override fun getItemCount(): Int = items.size
}