package com.grupo.aplicativo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate de tener este layout

        auth = FirebaseAuth.getInstance()

        // Solo muestra un mensaje simple
        Toast.makeText(this, "Bienvenido a MainActivity", Toast.LENGTH_SHORT).show()
    }
}