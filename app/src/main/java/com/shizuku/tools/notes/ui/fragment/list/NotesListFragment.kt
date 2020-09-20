package com.shizuku.tools.notes.ui.fragment.list

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.ui.activity.EditActivity
import com.shizuku.tools.notes.ui.adapter.NoteAdapter
import com.shizuku.tools.notes.ui.fragment.edit.EditFragment

class NotesListFragment : Fragment() {
    private val viewModel: NotesListViewModel by activityViewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var empty: TextView
    private lateinit var adapter: NoteAdapter
    private lateinit var swipe: SwipeRefreshLayout
    private fun navTo(id: Long) {
        val i = Intent("com.shizuku.tools.notes.EDIT")
        i.putExtra("noteId", id)
        context?.startActivity(i)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notes_list, container, false)
        recycler = root.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = NoteAdapter()
        recycler.adapter = adapter
        adapter.addOnClickListener {
            navTo(it.id)
        }
        empty = root.findViewById(R.id.empty)
        swipe = root.findViewById(R.id.swipe)
        swipe.setColorSchemeColors(resources.getColor(R.color.colorPrimary, resources.newTheme()))
        swipe.setOnRefreshListener {
            onRefresh()
            swipe.isRefreshing = false
        }
        root.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            navTo(0)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.notes.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                empty.visibility = View.VISIBLE
            } else {
                adapter.submit(it)
                adapter.notifyDataSetChanged()
                empty.visibility = View.INVISIBLE
            }
        })
        viewModel.load()
    }

    fun onRefresh() {
        viewModel.load()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }
}
