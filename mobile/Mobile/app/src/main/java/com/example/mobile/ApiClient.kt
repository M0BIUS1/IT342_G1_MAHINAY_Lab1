package com.example.mobile

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

// ---------- DTOs ----------

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String
)

// ---------- Client ----------

object ApiClient {

    // 10.0.2.2 is the host loopback address reachable from the Android emulator
    private const val BASE_URL = "http://10.0.2.2:8080"

    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /** POST /api/auth/login  */
    @Throws(IOException::class)
    fun login(email: String, password: String): AuthResponse {
        val body = gson.toJson(LoginRequest(email, password))
            .toRequestBody(JSON_MEDIA)

        val request = Request.Builder()
            .url("$BASE_URL/api/auth/login")
            .post(body)
            .build()

        return execute(request, AuthResponse::class.java)
    }

    /** POST /api/auth/register  */
    @Throws(IOException::class)
    fun register(name: String, email: String, password: String): AuthResponse {
        val body = gson.toJson(RegisterRequest(name, email, password))
            .toRequestBody(JSON_MEDIA)

        val request = Request.Builder()
            .url("$BASE_URL/api/auth/register")
            .post(body)
            .build()

        return execute(request, AuthResponse::class.java)
    }

    /** GET /api/user/me  */
    @Throws(IOException::class)
    fun me(token: String): UserDto {
        val request = Request.Builder()
            .url("$BASE_URL/api/user/me")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        return execute(request, UserDto::class.java)
    }

    // ---------- helpers ----------

    private fun <T> execute(request: Request, clazz: Class<T>): T {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (!response.isSuccessful) {
            val msg = try {
                val map = gson.fromJson(responseBody, Map::class.java)
                map["message"]?.toString() ?: "Request failed (${response.code})"
            } catch (_: Exception) {
                "Request failed (${response.code})"
            }
            throw IOException(msg)
        }
        return gson.fromJson(responseBody, clazz)
    }
}
