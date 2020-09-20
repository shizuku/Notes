package com.shizuku.tools.notes.ui.fragment.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.ui.activity.EditActivity
import com.shizuku.tools.notes.ui.view.StackEditText

class EditFragment : Fragment() {
    private val viewModel: EditViewModel by activityViewModels()
    private lateinit var edit: StackEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        edit = root.findViewById(R.id.edit)
        (activity as EditActivity).setEdit(edit)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        edit.text = Editable.Factory.getInstance().newEditable(viewModel.text.value)
        edit.fetchTo(viewModel.text)
    }
}
