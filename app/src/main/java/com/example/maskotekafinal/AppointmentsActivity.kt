package com.example.maskotekafinal
import android.content.Intent
import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class AppointmentsActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val appointmentList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointments)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.lvAppointments)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appointmentList)
        listView.adapter = adapter

        getAppointments()
    }

    private fun getAppointments() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
        if (userId == null) {
            Toast.makeText(this, "Usuario no está loggeado", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://maskoteka-backend-2fa48bf58972.herokuapp.com/api/appointments/$userId"
        val queue = Volley.newRequestQueue(this)
        val sdfDateIn  = SimpleDateFormat("yyyy-MM-dd", Locale("es","ES"))
        val sdfDateOut = SimpleDateFormat("dd/MM/yyyy", Locale("es","ES"))  // ej. "21/05/2025"
        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                appointmentList.clear()

                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val petName = obj.getJSONObject("pet").getString("name")
                    val date = obj.getString("date")
                    val dateObj    = sdfDateIn.parse(date)!!
                    val formattedDate = sdfDateOut.format(dateObj)
                    val hour = obj.getString("hour")
                    val itemText = "$formattedDate $hour - Mascota: $petName"
                    appointmentList.add(itemText)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
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