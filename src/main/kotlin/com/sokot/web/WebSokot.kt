package com.sokot.web

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class WebSokot(port : Int) {
    private val server = HttpServer.create(InetSocketAddress(port), 0)!!
    init {
        server.executor = null
    }

    /**
     * Apply Router of the server
     * @param router WebSokot Router to apply
     * @see WebSokotRouter
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun applyRouter(router : WebSokotRouter) {
        server.createContext(router.router) { exchange ->
            if (exchange.requestMethod == "POST") {
                router.postRequest(exchange)
                router.anyRequest(exchange, exchange.requestMethod)
            }
            if (exchange.requestMethod == "GET") {
                router.getRequest(exchange)
                router.anyRequest(exchange, exchange.requestMethod)
            }
        }
    }

    /**
     * Run server with the code
     * @param startFunction code to start after server start
     *
     * @author Unongmilk
     * @since 1.0.0
     */
    fun runServer(startFunction : (() -> Unit)? = null) {
        server.start()
        startFunction?.invoke()
    }
}