package org.porcumipsum.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.porcumipsum.MainActivity
import org.porcumipsum.R
import org.porcumipsum.fragments.CreateQrFragment
import org.porcumipsum.models.ListViewModel
import org.porcumipsum.utils.PorkUtils

class FavouritesAdapter(private val listVM: ListViewModel)
    : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
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
            PorkUtils.copyToClipboard(it.context, text)

            Toast.makeText(
                it.context,
                it.context.getString(R.string.clipboard_copy),
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.deleteBtn.setOnClickListener {
            listVM.removeElement(it.context, position)
        }

        holder.textView.setOnClickListener {
            openCreateQrDialog(it.context, text)
        }
    }

    override fun getItemCount() = dataSet.size

    private fun openCreateQrDialog(context: Context, text: String) {
        val activity = context as? MainActivity
        activity?.let {
            CreateQrFragment.newInstance(text)
                .show(context.supportFragmentManager, "CreateQrFragmentTag")
        }
    }

    fun setData(newData: List<String>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}