package com.shizuku.tools.notes.ui.fragment.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.data.entity.Note
import com.shizuku.tools.notes.ui.adapter.NoteAdapter
import com.shizuku.tools.notes.ui.fragment.edit.EditFragment
import com.shizuku.tools.notes.utils.DayNightUtils

class CollectionFragment : Fragment() {
    private val viewModel by activityViewModels<ListViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var empty: TextView
    private lateinit var adapter: NoteAdapter
    private lateinit var swipe: SwipeRefreshLayout

    private var collectionId = 0L
    private var all = true

    private fun openNote(noteId: Long) {
        val b = EditFragment.newArguments(noteId, false, 0L)
        findNavController().navigate(R.id.action_list_to_edit, b)
    }

    private fun filter(l: List<Note>): List<Note> {
        return if (all) {
            l
        } else {
            val r = mutableListOf<Note>()
            for (i in l) {
                if (i.collectionId == collectionId) {
                    r.add(i)
                }
            }
            r
        }
    }

    object NoteComparator : Comparator<Note> {
        override fun compare(o1: Note, o2: Note): Int {
            return if (o1.pin && !o2.pin) {
                -1
            } else if (!o1.pin && o2.pin) {
                1
            } else {
                (o2.time - o1.time).toInt()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_collection, container, false)
        recycler = root.findViewById(R.id.recycler)
        recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = NoteAdapter()
        recycler.adapter = adapter
        adapter.addOnClickListener {
            openNote(it.uid)
        }
        empty = root.findViewById(R.id.empty)
        swipe = root.findViewById(R.id.swipe)
        val isDark = DayNightUtils.getDark(context ?: throw Exception("no context"))
        if (isDark) {
            swipe.setProgressBackgroundColorSchemeColor(
                resources.getColor(
                    R.color.gray_2,
                    resources.newTheme()
                )
            )
            swipe.setColorSchemeColors(resources.getColor(R.color.gray_a, resources.newTheme()))
        } else {
            swipe.setProgressBackgroundColorSchemeColor(
                resources.getColor(
                    R.color.white,
                    resources.newTheme()
                )
            )
            swipe.setColorSchemeColors(resources.getColor(R.color.teal_1, resources.newTheme()))
        }
        swipe.setOnRefreshListener {
            onRefresh()
            swipe.isRefreshing = false
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.notes.observe(viewLifecycleOwner, {
            val x = filter(it)
            if (x.isEmpty()) {
                empty.visibility = View.VISIBLE
                recycler.visibility = View.INVISIBLE
            } else {
                adapter.submit(x.sortedWith(NoteComparator))
                adapter.notifyDataSetChanged()
                empty.visibility = View.INVISIBLE
                recycler.visibility = View.VISIBLE
            }
        })
        arguments?.let {
            collectionId = it.getLong(ARG_CID)
            all = it.getBoolean(ARG_ALL)
        }
    }

    private fun onRefresh() {
        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    companion object {
        const val ARG_CID = "cid"
        const val ARG_ALL = "all"

        @JvmStatic
        fun newArguments(cid: Long, all: Boolean) = bundleOf(ARG_CID to cid, ARG_ALL to all)

        @JvmStatic
        fun newInstance(cid: Long, all: Boolean) =
            CollectionFragment().apply {
                arguments = newArguments(cid, all)
            }
    }
}
