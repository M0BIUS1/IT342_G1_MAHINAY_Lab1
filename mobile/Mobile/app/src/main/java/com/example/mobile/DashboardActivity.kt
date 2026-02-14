package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlin.concurrent.thread

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserId: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnLogout: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvUserId = findViewById(R.id.tvUserId)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        btnLogout = findViewById(R.id.btnLogout)
        progressBar = findViewById(R.id.progressBar)

        btnLogout.setOnClickListener {
            SessionManager.clear(this)
            startActivity(Intent(this, LogoutActivity::class.java))
            finish()
        }

        loadProfile()
    }

    private fun loadProfile() {
        // Show cached data immediately
        tvUserId.text = SessionManager.getUserId(this).let { if (it == -1L) "—" else it.toString() }
        tvUserName.text = SessionManager.getUserName(this) ?: "—"
        tvUserEmail.text = SessionManager.getUserEmail(this) ?: "—"

        // Fetch fresh data from server
        val token = SessionManager.getToken(this) ?: return
        progressBar.visibility = View.VISIBLE

        thread {
            try {
                val user = ApiClient.me(token)
                SessionManager.saveSession(this, token, user)
                runOnUiThread {
                    tvUserId.text = user.id.toString()
                    tvUserName.text = user.name
                    tvUserEmail.text = user.email
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    // If token is invalid, redirect to login
                    if (e.message?.contains("401") == true || e.message?.contains("403") == true) {
                        SessionManager.clear(this)
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}