package com.shizuku.tools.notes.ui.fragment.list

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.data.entity.Collection
import com.shizuku.tools.notes.ui.fragment.edit.EditFragment
import com.shizuku.tools.notes.utils.DayNightUtils
import java.lang.Exception

class ListFragment : Fragment() {
    private val viewModel: ListViewModel by activityViewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: Adapter

    private val cs = mutableListOf<Collection>()
    private var curPos: Int = 0
    private var cid: Long = 0L
    private fun fetchCid(pos: Int) {
        curPos = pos
        cid = if (pos == 0) {
            0
        } else {
            cs[pos - 1].uid
        }
        Log.d("fetchCid", "$cid")
        deleteMenuItem?.isEnabled = pos != 0
    }

    private fun newNote() {
        Log.d("new note", "in: $cid")
        val b = EditFragment.newArguments(0, true, cid)
        findNavController().navigate(R.id.action_list_to_edit, b)
    }

    private fun newCollection() {
        val dl = NewCollectionDialogFragment {
            viewModel.newCollection(it)
        }
        dl.show(childFragmentManager, "New collection")
    }

    private fun deleteCollection() {
        if (curPos != 0) {
            val name = cs[curPos - 1].name
            DeleteCollectionDialogFragment(name) {
                viewModel.deleteCollection(cid)
            }.show(childFragmentManager, "Delete collection")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        toolbar = root.findViewById(R.id.toolbar)
        viewPager2 = root.findViewById(R.id.pager)
        viewPager2.offscreenPageLimit = 100
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                fetchCid(position)
            }
        })
        root.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            newNote()
        }
        activity?.let {
            (it as AppCompatActivity).setSupportActionBar(toolbar)
            adapter = Adapter(it)
            viewPager2.adapter = adapter
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.collections.observe(viewLifecycleOwner, {
            cs.clear()
            cs.addAll(it)
            adapter.notifyDataSetChanged()

        })
    }

    private fun onRefresh() {
        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.tabLayout)
        if (DayNightUtils.getDark(context ?: throw Exception("no context"))) {
            tabLayout.setSelectedTabIndicatorColor(
                resources.getColor(
                    R.color.gray_7,
                    resources.newTheme()
                )
            )
        } else {
            tabLayout.setSelectedTabIndicatorColor(
                resources.getColor(
                    R.color.blue,
                    resources.newTheme()
                )
            )
        }
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if (position == 0) {
                tab.text = resources.getText(R.string.collection_all)
            } else {
                tab.text = cs[position - 1].name
            }
        }.attach()
    }

    private var deleteMenuItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_list, menu)
        deleteMenuItem = menu.findItem(R.id.menu_list_delete_collection)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_list_new_collection -> {
                newCollection()
                true
            }
            R.id.menu_list_new_note -> {
                newNote()
                true
            }
            R.id.menu_list_delete_collection -> {
                deleteCollection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class Adapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = cs.size + 1
        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                CollectionFragment.newInstance(0, true)
            } else {
                val i = cs[position - 1]
                CollectionFragment.newInstance(i.uid, false)
            }
        }
    }

    class NewCollectionDialogFragment(private val cb: (title: String) -> Unit) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                val inflater = requireActivity().layoutInflater
                val view = inflater.inflate(R.layout.dialog_new_collection, null)
                val edit = view.findViewById<EditText>(R.id.edit_text)
                builder.setTitle(R.string.dialog_title_new_collection)
                    .setView(view)
                    .setPositiveButton(R.string.dialog_ok) { _, _ ->
                        val s = edit.text.toString()
                        cb.invoke(s)
                    }
                    .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    class DeleteCollectionDialogFragment(private val name: String, private val cb: () -> Unit) :
        DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.dialog_delete_collection_title)
                    .setMessage(
                        resources.getString(R.string.dialog_delete_collection_message).format(name)
                    )
                    .setPositiveButton(R.string.dialog_ok) { _, _ ->
                        cb.invoke()
                    }
                    .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}
