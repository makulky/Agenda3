package com.example.agenda3

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.agenda3.database.DatabaseHelper

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextEmail = view.findViewById<EditText>(R.id.etEmail)
        val editTextPassword = view.findViewById<EditText>(R.id.etPassword)
        val checkBoxShowPassword = view.findViewById<CheckBox>(R.id.cbShowPassword)
        val textViewRegister = view.findViewById<TextView>(R.id.tvRegister)

        val dbHelper = DatabaseHelper(requireContext())  // Crear instancia de DatabaseHelper

        textViewRegister.setOnClickListener {
            // Navegar a la pantalla de registro
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mostrar la contraseña
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // Ocultar la contraseña
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // Mueve el cursor al final después de cambiar la transformación
            editTextPassword.setSelection(editTextPassword.text.length)
        }

        val buttonLogin = view.findViewById<Button>(R.id.btnLogin)  // Suponiendo que tienes un botón de inicio de sesión
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val success = dbHelper.loginUser(email, password, requireContext())
                if (success) {
                    // Si el inicio de sesión es exitoso, navega a la siguiente pantalla
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)  // Cambia 'homeFragment' por el destino que prefieras
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
