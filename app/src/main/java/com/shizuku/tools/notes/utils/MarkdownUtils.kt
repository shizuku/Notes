package com.shizuku.tools.notes.utils

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownUtils {
    companion object {
        @JvmStatic
        fun toMarkdown(src: String): String {
            val parser = Parser.builder().build()
            val document = parser.parse(src)
            val renderer = HtmlRenderer.builder().build()
            return renderer.render(document)
        }
    }
}
