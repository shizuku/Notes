package com.shizuku.tools.notes.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.ui.fragment.edit.EditFragment
import com.shizuku.tools.notes.ui.fragment.edit.EditViewModel
import com.shizuku.tools.notes.ui.fragment.edit.PreviewFragment
import com.shizuku.tools.notes.ui.view.StackEditText

class EditActivity : AppCompatActivity() {
    private val viewModel: EditViewModel by viewModels()

    private var edit: StackEditText? = null
    private lateinit var pager: ViewPager2
    private lateinit var adapter: ScreenSlidePagerAdapter

    private var noteId: Long = 0L

    fun setEdit(e: StackEditText) {
        edit = e
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.let {
            noteId = it.getLongExtra("noteId", 0)
        }
        viewModel.load(noteId)
        pager = findViewById(R.id.pager)
        val list = listOf(EditFragment(), PreviewFragment())
        adapter = ScreenSlidePagerAdapter(this, list)
        pager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_undo -> {
                edit?.undo()
                true
            }
            R.id.action_edit_redo -> {
                edit?.redo()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity, var list: List<Fragment>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = list.size
        override fun createFragment(position: Int): Fragment = list[position]
    }
}
