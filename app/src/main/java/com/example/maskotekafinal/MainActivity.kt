package com.example.maskotekafinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Por favor, ingresa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendLoginRequest(email, password)
        }

    }

    private fun sendLoginRequest(email: String, password: String) {
        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/users/login"

        val jsonBody = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("LogResponse", response.toString())
                try{
                    Log.d("LogResponse", response.toString())
                    val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        val user = response.getJSONObject("user")
                        val userId = user.getString("id")
                        putString("user_id", userId)
                        apply()
                    }
                    Toast.makeText(this, "Ingresaste", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, PetSignupActivity::class.java)
                    startActivity(intent)
                } catch (e: JSONException) {
                    Log.d("LogResponse", e.toString())
                }
            },
            { error ->
                Log.d("VolleyError", error.toString())
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }
}