package com.example.maskotekafinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PetSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerPetSex = findViewById<Spinner>(R.id.spinnerPetSex)
        val options = listOf("Macho", "Hembra")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPetSex.adapter = adapter

        val etName = findViewById<EditText>(R.id.etPetName)
        val etAge = findViewById<EditText>(R.id.etPetAge)
        val etBreed = findViewById<EditText>(R.id.etPetBreed)
        val etSex = spinnerPetSex
        val etSpecie = findViewById<EditText>(R.id.etPetSpecie)

        val btnPetSignup = findViewById<Button>(R.id.btnSignup)
        btnPetSignup.setOnClickListener {
            val name = etName.text.toString()
            val age = etAge.text.toString().toInt()
            val breed = etBreed.text.toString()
            val sex = etSex.selectedItem.toString()
            val specie = etSpecie.text.toString()
            if(name.isEmpty() || age < 1 || breed.isEmpty() || sex.isEmpty() || specie.isEmpty()){
                Toast.makeText(this, "Necesitas llenar todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                sendSignupPet(name,age, sex,breed,specie)
            }
        }

    }

    private fun sendSignupPet(name: String, age: Int, sex: String, breed: String, specie: String) {
        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/pets"
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "Usuario no está loggeado", Toast.LENGTH_LONG).show()
            return
        }

        val jsonBody = JSONObject().apply {
            put("user", userId)
            put("name", name)
            put("age", age)
            put("sex", sex)
            put("breed", breed)
            put("specie", specie)
        }

        Log.d("LogRequest", jsonBody.toString())

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("LogResponse", response.toString())
                Toast.makeText(this, "Mascota registrada correctamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ScheduleAppointmentActivity::class.java)
                startActivity(intent)
            },
            { error ->
                Log.e("LogError", error.toString())
                Toast.makeText(this, "Fallo al registrar mascota: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sidemenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemPetSignup -> {
                val intent = Intent(this, PetSignupActivity::class.java)
            startActivity(intent)
                true
            }
            R.id.itemScheduleAppointment -> {
                val intent = Intent(this, ScheduleAppointmentActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.itemMyAppointments -> {
                val intent = Intent(this, AppointmentsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}