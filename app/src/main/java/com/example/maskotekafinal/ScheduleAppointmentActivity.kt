package com.example.maskotekafinal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ScheduleAppointmentActivity : AppCompatActivity() {
    private val petIdList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etDate =  findViewById<EditText>(R.id.etAppointmentDatepicker)
        val etTime =  findViewById<EditText>(R.id.etAppointmentHourPicker)
        val registerAppointmentButton = findViewById<Button>(R.id.btnRegisterAppointment)

        registerAppointmentButton.setOnClickListener {
            scheduleAppointment()
        }
        etDate.setOnClickListener {
            DatePickerDialog(this).apply {
                setOnDateSetListener { _, year, month, day ->
                    etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                }
                show()
            }
        }

        etTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                etTime.setText(String.format("%02d:%02d", hour, minute))
            }, 12, 0, true).show()
        }
        getUserPets()
    }

    private fun getUserPets() {
        val userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getString("user_id", null)
        if (userId == null) {
            Toast.makeText(this, "El usuario no está loggeado", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/pets?user=$userId"
        val queue = Volley.newRequestQueue(this)

        val petNames = mutableListOf<String>()
        val sPets: Spinner = findViewById<Spinner>(R.id.sPet)

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                petNames.clear()
                petIdList.clear()
                petNames.clear()
                for (i in 0 until response.length()) {
                    val petObj = response.getJSONObject(i)
                    val name = petObj.getString("name")
                    val id = petObj.getString("_id")  // or "_id" depending on your API
                    petNames.add(name)
                    petIdList.add(id)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sPets.adapter = adapter
            },
            { error ->
                Toast.makeText(this, "Failed to load pets: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }

    private fun scheduleAppointment() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
        val date = findViewById<EditText>(R.id.etAppointmentDatepicker)
        val time = findViewById<EditText>(R.id.etAppointmentHourPicker)
        val vet = findViewById<Spinner>(R.id.sVet)
        val pet = findViewById<Spinner>(R.id.sPet)
        if (userId == null) {
            Toast.makeText(this, "El usuario no está loggeado", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.text.toString().isEmpty() || time.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona hora y día", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPetPosition = pet.selectedItemPosition
        val petId = petIdList.getOrNull(selectedPetPosition)

        Log.d("Vet", vet.selectedItem.toString())
        Log.d("Pet", petId.toString())
        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/appointments"

        val jsonBody = JSONObject().apply {
            put("user", userId)
            put("pet", petId)
            put("date", date.text.toString())
            put("hour", time.text.toString())
        }

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                Toast.makeText(this, "Se creó la cita correctamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, AppointmentsActivity::class.java)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "Hubo un error creando la cita", Toast.LENGTH_LONG).show()
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