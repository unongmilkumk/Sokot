package com.sokot

import com.sun.net.httpserver.HttpExchange
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class SokotAuth(val databaseFile : File) {
    constructor(databaseLocation : String) : this(File(databaseLocation))
    data class User (val id : Int, val username : String, val hashedPassword : String, val salt : String)
    private val users = mutableListOf<User>()
    private var userIdCounter = 1

    init {
        if (databaseFile.exists()) {
            loadUsersFromFile()
        }
    }

    fun saveUser(username: String, password: String) {
        val salt = generateSalt()
        val hashedPassword = hashPassword(password, salt)
        val newUser = User(userIdCounter++, username, hashedPassword, salt)
        users.add(newUser)
        saveUsersToFile()
    }

    fun getUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    fun getUsernameByExchange(exchange : HttpExchange): String? {
        if (getSessionTokenFromRequest(exchange) == null) return null
        return getUsernameByToken(getSessionTokenFromRequest(exchange)!!)
    }

    private fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private fun hashPassword(password: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        val hashedBytes = messageDigest.digest(saltedPassword.toByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    private fun saveUsersToFile() {
        val jsonString = buildJsonString(users)
        databaseFile.writeText(jsonString)
    }

    private fun loadUsersFromFile() {
        val jsonString = databaseFile.readText()
        users.clear()
        users.addAll(parseJsonString(jsonString))
    }

    private fun buildJsonString(users: List<User>): String {
        val userEntries = users.joinToString(",") { """{"id":${it.id},"username":"${it.username}","hashedPassword":"${it.hashedPassword}","salt":"${it.salt}"}""" }
        return "[$userEntries]"
    }

    private fun parseJsonString(jsonString: String): List<User> {
        val userRegex = Regex("""\{"id":(\d+),"username":"([^"]+)","hashedPassword":"([^"]+)","salt":"([^"]+)"}""")
        return userRegex.findAll(jsonString).map {
            User(it.groupValues[1].toInt(), it.groupValues[2], it.groupValues[3], it.groupValues[4])
        }.toList()
    }
    private val sessionTokens = mutableMapOf<String, String>()

    fun authenticateUser(username: String, password: String): String? {
        val user = getUserByUsername(username)
        return if (user != null && user.hashedPassword == hashPassword(password, user.salt)) {
            val sessionToken = generateSessionToken()
            sessionTokens[sessionToken] = username
            sessionToken
        } else {
            null
        }
    }

    fun getUsernameByToken(sessionToken: String): String? {
        return sessionTokens[sessionToken]
    }

    private fun generateSessionToken(): String {
        return UUID.randomUUID().toString()
    }

    fun getSessionTokenFromRequest(exchange: HttpExchange): String? {
        val headers = exchange.requestHeaders["Cookie"]
        return headers?.flatMap { it.split(";") }
            ?.map { it.trim() }
            ?.find { it.startsWith("sessionToken=") }
            ?.substringAfter("sessionToken=")
    }
}