package com.example.agenda3

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.agenda3.database.DatabaseHelper

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextName = view.findViewById<EditText>(R.id.etName)
        val editTextEmail = view.findViewById<EditText>(R.id.etEmail)
        val editTextPassword = view.findViewById<EditText>(R.id.etPassword)
        val checkBoxShowPassword = view.findViewById<CheckBox>(R.id.cbShowPassword)
        val buttonRegister = view.findViewById<Button>(R.id.btnRegister)

        val dbHelper = DatabaseHelper(requireContext())

        // Mostrar/Ocultar contraseña
        checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            editTextPassword.setSelection(editTextPassword.text.length)
        }

        // Registrar usuario al hacer clic en el botón
        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isRegistered = dbHelper.registerUser(name, email, password, requireContext())
            if (isRegistered) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }
}
