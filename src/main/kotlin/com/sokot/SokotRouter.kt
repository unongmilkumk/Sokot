package com.sokot

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

open class SokotRouter(val router : String) {
    fun sendResponse(exchange : HttpExchange, html : String) {
        exchange.sendResponseHeaders(200, html.length.toLong())
        val os: OutputStream = exchange.responseBody
        val writer = OutputStreamWriter(os, StandardCharsets.UTF_8)
        writer.write(html)
        writer.close()
        os.close()
    }
    fun changRoute(exchange: HttpExchange, targetRoute: String) {
        exchange.responseHeaders.add("Location", targetRoute)
        exchange.sendResponseHeaders(302, -1)
        exchange.close()
    }
    open fun getRequest(exchange : HttpExchange) {

    }
    open fun postRequest(exchange : HttpExchange) {

    }
}