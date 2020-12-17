package com.shizuku.tools.notes.ui.fragment.edit

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.ui.fragment.list.ListViewModel

class EditFragment : Fragment() {
    private val viewModel: EditViewModel by activityViewModels()
    private val listViewModel: ListViewModel by activityViewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: Adapter

    private fun moveCollection() {
        listViewModel.collections.value?.let { cs ->
            val cid = viewModel.note.collectionId
            var l = arrayOf(resources.getText(R.string.collection_all).toString())
            l = l.plus(cs.map { it.name }.toTypedArray())
            val cur = if (cid == 0L) {
                0
            } else {
                cs.indexOfFirst {
                    it.uid == cid
                } + 1
            }
            MoveDialogFragment(l, cur) {
                //Log.d("move to", "$it")
                if (it == 0) {
                    viewModel.moveCollection(0) {
                        listViewModel.refresh()
                    }
                } else {
                    viewModel.moveCollection(cs[it - 1].uid) {
                        listViewModel.refresh()
                    }
                }
            }.show(childFragmentManager, "move collection")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        toolbar = root.findViewById(R.id.toolbar)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_back)
        viewPager2 = root.findViewById(R.id.pager)
        viewPager2.offscreenPageLimit = 10
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                fetchMenuTo(position)
            }
        })
        activity?.let {
            (it as AppCompatActivity).setSupportActionBar(toolbar)
            adapter = Adapter(it)
            viewPager2.adapter = adapter
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val noteId = arguments?.getLong(ARG_NID) ?: 0L
        val isNew = arguments?.getBoolean(ARG_NEW) ?: true
        val cid = arguments?.getLong(ARG_CID) ?: 0L
        Log.d("edit create", "$noteId, $isNew, $cid")
        viewModel.load(noteId, isNew, cid) {
            fetchPin(viewModel.note.pin)
        }
    }

    private var pinIt: MenuItem? = null
    private var unpinIt: MenuItem? = null
    private fun changePin(p: Boolean) {
        viewModel.changePin(p) {
            fetchPin(it)
        }
    }

    private fun fetchPin(p: Boolean) {
        if (p) {
            pinIt?.isVisible = false
            unpinIt?.isVisible = true
        } else {
            pinIt?.isVisible = true
            unpinIt?.isVisible = false
        }
    }

    private var input: MenuItem? = null
    private var preview: MenuItem? = null
    private fun scrollTo(idx: Int) {
        if (idx == 0) {
            viewPager2.setCurrentItem(0, true)
        } else {
            viewPager2.setCurrentItem(1, true)
        }
    }

    private fun fetchMenuTo(idx: Int) {
        if (idx == 0) {
            input?.isVisible = false
            preview?.isVisible = true
        } else {
            input?.isVisible = true
            preview?.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        input = menu.findItem(R.id.menu_edit_input)
        preview = menu.findItem(R.id.menu_edit_preview)
        pinIt = menu.findItem(R.id.menu_edit_pin)
        unpinIt = menu.findItem(R.id.menu_edit_unpin)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragment?.findNavController()?.popBackStack()
                true
            }
            R.id.menu_edit_share -> {
                true
            }
            R.id.menu_edit_input -> {
                scrollTo(0)
                true
            }
            R.id.menu_edit_preview -> {
                scrollTo(1)
                true
            }
            R.id.menu_edit_move -> {
                moveCollection()
                true
            }
            R.id.menu_edit_pin -> {
                changePin(true)
                true
            }
            R.id.menu_edit_unpin -> {
                changePin(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class Adapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                EditInputFragment()
            } else {
                EditPreviewFragment()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.close()
    }

    class MoveDialogFragment(
        private val l: Array<String>,
        private val cur: Int,
        private val cb: (pos: Int) -> Unit
    ) :
        DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                var newPos = cur
                val builder = AlertDialog.Builder(it)
                    .setTitle(R.string.dialog_move_title)
                    .setSingleChoiceItems(l, cur) { _, which ->
                        newPos = which
                        Log.d("change choose", "$newPos")
                    }.setPositiveButton(R.string.dialog_ok) { _, _ ->
                        cb.invoke(newPos)
                    }.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                return builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    companion object {
        const val ARG_NID = "noteId"
        const val ARG_NEW = "isNew"
        const val ARG_CID = "collectionId"

        @JvmStatic
        fun newArguments(noteId: Long, isNew: Boolean, cid: Long) =
            bundleOf(ARG_NID to noteId, ARG_NEW to isNew, ARG_CID to cid)

        @JvmStatic
        fun newInstance(noteId: Long, isNew: Boolean, cid: Long) =
            EditFragment().apply {
                Log.d("new inst", "$noteId,$isNew, $cid")
                arguments = newArguments(noteId, isNew, cid)
            }
    }
}
