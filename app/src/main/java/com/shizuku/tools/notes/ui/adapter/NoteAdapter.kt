package com.shizuku.tools.notes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.data.entity.Note
import java.text.DateFormat
import java.util.*

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val list = mutableListOf<Note>()

    fun submit(l: List<Note>) {
        list.clear()
        list.addAll(l)
    }

    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var note: Note
        private val summary: TextView = v.findViewById(R.id.summary)
        private val time: TextView = v.findViewById(R.id.summary)
        fun bind(item: Note) {
            note = item
            summary.text = item.content
            val t = Calendar.getInstance().apply {
                timeInMillis = item.time
            }.time
            time.text = DateFormat.getDateTimeInstance().format(t)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_note, parent, false)
        val holder = ViewHolder(v)
        holder.v.setOnClickListener {
            onCLickListeners.forEach {
                it.invoke(holder.note)
            }
        }
        holder.v.setOnLongClickListener {
            true
        }
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    private val onCLickListeners = mutableSetOf<(Note) -> Unit>()
    fun addOnClickListener(listener: (Note) -> Unit) {
        onCLickListeners.add(listener)
    }
}
