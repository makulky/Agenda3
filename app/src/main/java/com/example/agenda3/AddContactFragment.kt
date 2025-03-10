package com.example.agenda3

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.agenda3.database.DatabaseHelper

class AddContactFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nombreEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact, container, false)

        dbHelper = DatabaseHelper(requireContext())

        nombreEditText = view.findViewById(R.id.et_nombre)
        apellidosEditText = view.findViewById(R.id.et_apellidos)
        telefonoEditText = view.findViewById(R.id.et_telefono)
        addButton = view.findViewById(R.id.btn_añadir)

        addButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellidos = apellidosEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("user_id", -1)

            if (userId != -1) {
                if (nombre.isNotEmpty() && apellidos.isNotEmpty() && telefono.isNotEmpty()) {
                    val success = dbHelper.addContact(userId, nombre, apellidos, telefono, requireContext())
                    if (success) {
                        findNavController().navigate(R.id.action_addContactFragment_to_mainFragment)
                    }
                } else {
                    // Manejar error de campos vacíos
                }
            } else {
                // Manejar error: no se encontró el userId
            }
        }

        return view
    }
}
