package com.shizuku.tools.notes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.utils.TimeUtils
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
        private val time: TextView = v.findViewById(R.id.time)
        private val pin: ImageView = v.findViewById(R.id.pin)
        fun bind(item: Note) {
            note = item
            summary.text = item.content
            val t = Calendar.getInstance().apply {
                timeInMillis = item.time
            }.time
            time.text = TimeUtils.display(item.time)
            if (item.pin) {
                pin.visibility = View.VISIBLE
            } else {
                pin.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_note, parent, false)
        val holder = ViewHolder(v)
        holder.v.setOnClickListener {
            onClickListeners.forEach {
                it.invoke(holder.note)
            }
        }
        holder.v.setOnLongClickListener {
            var r = true
            onLongClickListeners.forEach {
                r = it.invoke(holder.note) && r
            }
            r
        }
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    private val onClickListeners = mutableSetOf<(Note) -> Unit>()
    fun addOnClickListener(listener: (Note) -> Unit) {
        onClickListeners.add(listener)
    }

    private val onLongClickListeners = mutableSetOf<(Note) -> Boolean>()
    fun addOnLongClickListener(listener: (Note) -> Boolean) {
        onLongClickListeners.add(listener)
    }
}
