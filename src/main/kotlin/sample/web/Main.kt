package sample.web

import com.sokot.web.WebSokot
import com.sokot.web.WebSokotAuth
import com.sokot.web.WebSokotRouter
import com.sun.net.httpserver.HttpExchange
import java.io.File

fun main() {
    val sokot = WebSokot(3000)
    val auth = WebSokotAuth("db/user_information.json")
    sokot.applyRouter(TopRouter(auth))
    sokot.applyRouter(LoginRouter(auth))
    sokot.applyRouter(SignupRouter(auth))
    sokot.runServer {
        println("http://localhost:3000")
    }
}

class TopRouter(val auth : WebSokotAuth) : WebSokotRouter("/") {
    override fun getRequest(exchange: HttpExchange) {
        if (auth.getSessionTokenFromRequest(exchange) != null && auth.getUsernameByExchange(exchange) != null) {
            sendResponse(exchange, File("server/index.html").readText().replace("<!--Login Message-->",
                "<p>You are ${auth.getUsernameByExchange(exchange)}"))
        } else {
            sendHtmlResponse(exchange, "server/index.html")
        }
    }
}

class LoginRouter(val auth : WebSokotAuth) : WebSokotRouter("/login") {
    override fun getRequest(exchange: HttpExchange) {
        sendHtmlResponse(exchange, "server/login.html")
    }

    override fun postRequest(exchange: HttpExchange) {
        val formData = exchange.requestBody.bufferedReader().readText()

        val id = formData.substringAfter("id=").substringBefore("&")
        val password = formData.substringAfter("password=").substringBefore("&")
        val sessionToken = auth.authenticateUser(id, password)

        if (sessionToken != null) {
            exchange.responseHeaders.add("Set-Cookie", "sessionToken=$sessionToken")
            changRoute(exchange, "/")
        } else {
            sendHtmlResponse(exchange, "server/login.html")
        }
    }
}

class SignupRouter(val auth : WebSokotAuth) : WebSokotRouter("/signup") {
    override fun getRequest(exchange: HttpExchange) {
        sendHtmlResponse(exchange, "server/signup.html")
    }

    override fun postRequest(exchange: HttpExchange) {
        val formData = exchange.requestBody.bufferedReader().readText()

        val id = formData.substringAfter("id=").substringBefore("&")
        val password = formData.substringAfter("password=").substringBefore("&")

        if (auth.getUserByUsername(id) != null) {
            changRoute(exchange, "/")
            return
        }

        auth.saveUser(id, password)

        if (auth.authenticateUser(id, password) != null) {
            exchange.responseHeaders.add("Set-Cookie", "sessionToken=${auth.authenticateUser(id, password)}")
            changRoute(exchange, "/")
        } else {
            sendHtmlResponse(exchange, "server/signup.html")
        }
    }
}