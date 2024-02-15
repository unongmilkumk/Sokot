package com.sokot

import com.sun.net.httpserver.HttpExchange

fun main() {
    val sokot = Sokot(3000)
    sokot.applyRouter(TestSokotRouter())
    sokot.runServer()
}

class TestSokotRouter : SokotRouter("/") {
    override fun getRequest(exchange: HttpExchange) {
        sendResponse(exchange, "<p>Hello World</p>")
    }
}