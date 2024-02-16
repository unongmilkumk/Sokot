package com.sokot

import com.sun.net.httpserver.HttpExchange
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


abstract class SokotRouter(val router : String) {
    /**
     * Send the HTML String
     * @param exchange where to send
     * @param html what to send
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun sendResponse(exchange : HttpExchange, html : String) {
        exchange.sendResponseHeaders(200, html.length.toLong())
        val os: OutputStream = exchange.responseBody
        val writer = OutputStreamWriter(os, StandardCharsets.UTF_8)
        writer.write(html)
        writer.close()
        os.close()
    }

    /**
     * Send the HTML File
     * @param exchange where to send
     * @param location location of the file what to send
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun sendFileResponse(exchange : HttpExchange, location : String) {
        val html = File(location).readText()
        println(location)
        exchange.sendResponseHeaders(200, html.length.toLong())
        val os: OutputStream = exchange.responseBody
        val writer = OutputStreamWriter(os, StandardCharsets.UTF_8)
        writer.write(html)
        writer.close()
        os.close()
    }

    /**
     * Change the routing of the exchange
     * @param exchange who to change
     * @param targetRoute where to change
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun changRoute(exchange : HttpExchange, targetRoute : String) {
        exchange.responseHeaders.add("Location", targetRoute)
        exchange.sendResponseHeaders(302, -1)
        exchange.close()
    }

    /**
     * Get parameters of the router
     * @param exchange Where to get
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun getParameters(exchange : HttpExchange) : HashMap<String, String> {
        val query = exchange.requestURI.query ?: return hashMapOf()
        val result: HashMap<String, String> = HashMap()
        for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val entry = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (entry.size > 1) {
                result[entry[0]] = entry[1]
            } else {
                result[entry[0]] = ""
            }
        }
        return result
    }

    /**
     * When get "GET" request, run this code
     * @param exchange description of request 
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    open fun getRequest(exchange : HttpExchange) {}
    
    /**
     * When get "POST" request, run this code
     * @param exchange description of request
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    open fun postRequest(exchange : HttpExchange) {}
}