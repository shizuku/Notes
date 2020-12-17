package com.shizuku.tools.notes.ui.fragment.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.shizuku.tools.notes.R

class EditInputFragment : Fragment() {
    private val viewModel: EditViewModel by activityViewModels()

    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_input, container, false)
        editText = root.findViewById(R.id.edit_text)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.editText.observe(viewLifecycleOwner, Observer {
            editText.setText(it.s)
            editText.setSelection(it.slStart, it.slEnd)
        })
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                viewModel.beforeModify(s.toString(), start, count, after)

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                viewModel.onModify(s.toString(), start, before, count)

            override fun afterTextChanged(s: Editable?) = viewModel.afterModify(s.toString())
        })
    }
}
