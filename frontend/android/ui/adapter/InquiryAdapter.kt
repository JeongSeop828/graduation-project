package com.project.meongnyangcare.ui.adapter

import com.project.meongnyangcare.model.Inquiry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongnyangcare.R

class InquiryAdapter(
    private val inquiryList: List<Inquiry>,
    private val onItemClick: (Inquiry) -> Unit
) : RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder>() {

    inner class InquiryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.tvId)
        val inquiryTitle: TextView = itemView.findViewById(R.id.tvInquiryTitle)
        val status: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(inquiry: Inquiry, position: Int) {
            textId.text = (position + 1).toString() // 여기 수정!
            inquiryTitle.text = inquiry.title
            status.text = inquiry.isAnswered

            itemView.setOnClickListener { onItemClick(inquiry) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InquiryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inquire, parent, false)
        return InquiryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InquiryViewHolder, position: Int) {
        holder.bind(inquiryList[position], position) // position 넘겨주기
    }

    override fun getItemCount(): Int = inquiryList.size
}
