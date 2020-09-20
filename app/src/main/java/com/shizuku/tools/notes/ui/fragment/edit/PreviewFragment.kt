package com.shizuku.tools.notes.ui.fragment.edit

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.shizuku.tools.notes.R
import com.shizuku.tools.notes.utils.MarkdownUtils

class PreviewFragment : Fragment() {
    private val viewModel: EditViewModel by activityViewModels()
    private lateinit var web: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_preview, container, false)
        web = root.findViewById(R.id.web)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.text.observe(viewLifecycleOwner, Observer {
            val s = MarkdownUtils.toMarkdown(it)
            web.loadData(s, "text/html", "UTF-8")
            viewModel.save()
        })
    }
}
