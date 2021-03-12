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
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val ARG_CRIME_ID = "crime_id"

class CrimeFragment : Fragment() {

    private lateinit var editTextTitle:EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox
    private lateinit var crime: Crime
    private lateinit var crimeDetailViewModel: CrimeDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel = ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
        crimeDetailViewModel.loadCrime(crimeId)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
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

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI(){
        editTextTitle.setText(crime.title)
        buttonDate.text = crime.date.toString()
        checkBoxSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}