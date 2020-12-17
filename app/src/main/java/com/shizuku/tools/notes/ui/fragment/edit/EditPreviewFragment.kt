package com.shizuku.tools.notes.ui.fragment.edit

import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.shizuku.tools.notes.R
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class EditPreviewFragment : Fragment() {
    private val viewModel: EditViewModel by activityViewModels()

    private lateinit var webView: WebView

    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_preview, container, false)
        webView = root.findViewById(R.id.web)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.text.observe(viewLifecycleOwner, Observer {
            val html = renderer.render(parser.parse(it))
            webView.loadData(html, "text/html", "utf-8")
        })
    }
}
