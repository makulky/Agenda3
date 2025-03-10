package com.example.agenda3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.agenda3.database.DatabaseHelper

class InformationFragment : Fragment() {

    private val args: InformationFragmentArgs by navArgs()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nameTextView: TextView
    private lateinit var surnameTextView: TextView
    private lateinit var phoneTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        dbHelper = DatabaseHelper(requireContext())
        nameTextView = view.findViewById(R.id.tv_name)
        surnameTextView = view.findViewById(R.id.tv_surname)
        phoneTextView = view.findViewById(R.id.tv_phone)

        val contactId = args.contactId
        val contact = dbHelper.getContactById(contactId)
        contact?.let {
            nameTextView.text = it.name
            surnameTextView.text = it.surname
            phoneTextView.text = it.phone
        }

        return view
    }
}
