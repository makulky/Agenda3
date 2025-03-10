package com.example.agenda3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.agenda3.database.DatabaseHelper

class InformationFragment : Fragment() {

    private val args: InformationFragmentArgs by navArgs()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var modifyButton: Button
    private lateinit var deleteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        dbHelper = DatabaseHelper(requireContext())
        nameEditText = view.findViewById(R.id.tv_name)
        surnameEditText = view.findViewById(R.id.tv_surname)
        phoneEditText = view.findViewById(R.id.tv_phone)
        modifyButton = view.findViewById(R.id.btn_modify)
        deleteButton = view.findViewById(R.id.btn_delete)

        val contactId = args.contactId
        val contact = dbHelper.getContactById(contactId)
        contact?.let {
            nameEditText.setText(it.name)
            surnameEditText.setText(it.surname)
            phoneEditText.setText(it.phone)
        }

        modifyButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val phone = phoneEditText.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty()) {
                dbHelper.updateContact(contactId, name, surname, phone, requireContext())
                findNavController().navigate(R.id.action_informationFragment_to_mainFragment)
            } else {
                // Manejar error de campos vacíos
            }
        }

        deleteButton.setOnClickListener {
            dbHelper.deleteContact(contactId, requireContext())
            // Navegar de vuelta al MainFragment después de eliminar
            findNavController().navigate(R.id.action_informationFragment_to_mainFragment)
        }

        return view
    }
}
