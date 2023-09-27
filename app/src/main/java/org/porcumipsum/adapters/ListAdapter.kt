package org.porcumipsum.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.porcumipsum.R

class ListAdapter(
    private val onTextClick: (String) -> Unit,
    private val onCopyClick: (String) -> Unit,
    private  val onDeleteClick: (Int) -> Unit
)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var dataSet = emptyList<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_display)
        val copyBtn: ImageButton = view.findViewById(R.id.copy_button)
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = dataSet[position]
        holder.textView.text = text

        holder.copyBtn.setOnClickListener {
            onCopyClick(text)
        }

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(position)
        }

        holder.textView.setOnClickListener {
            onTextClick(text)
        }
    }

    override fun getItemCount() = dataSet.size

    fun setData(newData: List<String>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}