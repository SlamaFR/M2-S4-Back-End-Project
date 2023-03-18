package com.kamelia.ugeoverflow.core

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service

@Service
class MdToHtmlService {

    private val parser = Parser.builder().build()

    fun convert(md: String): String {
        val document = parser.parse(md)
        val renderer = HtmlRenderer.builder().build()
        return renderer.render(document)
    }

}