package com.example.maskotekafinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.math.log

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNames = findViewById<EditText>(R.id.etNames)
        val etLastNames = findViewById<EditText>(R.id.etLastNames)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnSignup.setOnClickListener {
            val name = etNames.text.toString().trim() + " " + etLastNames.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if(!validateEmail(email)){
                etEmail.error = "Error en el correo"
                return@setOnClickListener
            }
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendSignupRequest(name, phone, email, password)
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendSignupRequest(name: String, phone: String, email: String, password: String) {
        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/users"

        val jsonBody = JSONObject().apply {
            put("name", name)
            put("phone", phone)
            put("email", email)
            put("password", password)
        }

        Log.d("LogResponse", jsonBody.toString())

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("LogResponse", response.toString())
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, PetSignupActivity::class.java)
                    startActivity(intent)


            },
            { error ->
                Log.d("LogResponse", error.toString())
                Toast.makeText(this, "Fallo al momento de registrar usuario: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }
}