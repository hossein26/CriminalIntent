package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText


class CrimeFragment : Fragment() {

    private lateinit var editTextTitle:EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox
    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        //init
        editTextTitle = view.findViewById(R.id.edit_text_title)
        buttonDate = view.findViewById(R.id.button_date)
        checkBoxSolved = view.findViewById(R.id.checkbox_solved)

        return view
    }

    override fun onStart() {
        super.onStart()

        //listeners
        val titleWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //blank
            }
        }
        editTextTitle.addTextChangedListener(titleWatcher)

        buttonDate.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        checkBoxSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }

    companion object {

        fun newInstance() =
            CrimeFragment().apply {

            }
    }
}