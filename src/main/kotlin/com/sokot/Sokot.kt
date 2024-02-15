package com.sokot

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class Sokot(port : Int) {
    private val server = HttpServer.create(InetSocketAddress(port), 0)!!
    init {
        server.executor = null
    }
    fun applyRouter(router : SokotRouter) {
        server.createContext(router.router) { exchange ->
            if (exchange.requestMethod == "POST") {
                router.postRequest(exchange)
            }
            if (exchange.requestMethod == "GET") {
                router.getRequest(exchange)
            }
        }
    }
    fun runServer() {
        server.start()
    }
}