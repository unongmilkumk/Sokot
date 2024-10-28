# Proj. KSSL
### Kotlin Simple Server Library

This project is composed with `Sokot`, the library class. It contains a socket-based game server (`GameSokot`) and a web server framework (`WebSokot`) written in Kotlin. It includes HTTP routing, authentication, and static file serving, making it ideal for multiplayer games or interactive web applications.

## Why use this?

### GameSokot (com.sokot.game)
- **Socket-based multiplayer server**: Allows multiple clients to connect, sending messages to one another.
- **Client handling**: Each client connection is handled in a separate thread.
- **Message broadcasting**: Messages received from a client are broadcast to all other connected clients.

### WebSokot (com.sokot.web)
- **HTTP Server**: Uses `HttpServer` to serve HTTP requests.
- **Routing support**: Custom routing via `WebSokotRouter` for handling GET and POST requests.
- **Static file serving**: Supports serving static files like HTML, CSS, and JavaScript.
- **Authentication**: Simple user authentication with password hashing and session management via cookies.

## How to Install

1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd sokot-game-web
   ```

2. Configure the server port and authentication database file location as required.

## Usage

### GameSokot

The `GameSokot` class allows clients to connect and send messages. Each message is broadcast to all other clients.

```kotlin
import com.sokot.game.GameSokot

fun main() {
    val server = GameSokot(port = 12345)
    server.start()
}
```

### WebSokot

The `WebSokot` framework allows for serving web requests with specific routes and handling GET/POST requests.

```kotlin
import com.sokot.web.WebSokot
import com.sokot.web.WebSokotRouter

fun main() {
    val webServer = WebSokot(port = 8080)
    
    val myRouter = object : WebSokotRouter("/example") {
        override fun getRequest(exchange: HttpExchange) {
            sendResponse(exchange, "<h1>Welcome to Sokot!</h1>")
        }
    }

    webServer.applyRouter(myRouter)
    webServer.runServer {
        println("Server is running on port 8080")
    }
}
```

### WebSokot Authentication

The `WebSokotAuth` class provides basic user management with salted and hashed password storage and session-based authentication.

```kotlin
import com.sokot.web.WebSokotAuth

val auth = WebSokotAuth("path/to/database.json")
auth.saveUser("username", "password")
val sessionToken = auth.authenticateUser("username", "password")
```

## Classes Overview

### GameSokot

- **start()**: Starts the server, accepting and handling client connections.
- **handleClient()**: Manages each client connection, reading messages and broadcasting them.
- **broadcast()**: Sends messages from one client to all other connected clients.

### WebSokot

- **applyRouter()**: Applies a router to handle specific routes and methods.
- **runServer()**: Starts the server with an optional startup function.

### WebSokotRouter

- **sendResponse()**: Sends an HTML string to the client.
- **sendHtmlResponse()**: Sends an HTML file to the client.
- **sendStaticFileResponse()**: Serves static files from a specified root directory.
- **getParameters()**: Parses URL query parameters.

### WebSokotAuth

- **saveUser()**: Registers a new user with hashed and salted password storage.
- **authenticateUser()**: Authenticates a user and returns a session token if successful.
- **getUsernameByToken()**: Retrieves a username by session token.
- **getSessionTokenFromRequest()**: Extracts the session token from cookies in an HTTP request.

## Security Notice

**Warning**: `WebSokotAuth` is not intended for production use. It lacks certain secure features and should be used only for demonstration purposes.