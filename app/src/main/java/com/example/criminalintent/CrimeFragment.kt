package com.example.criminalintent

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 1

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {

    private lateinit var editTextTitle: EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox
    private lateinit var checkBoxPolice: CheckBox
    private lateinit var crime: Crime
    private lateinit var crimeDetailViewModel: CrimeDetailViewModel
    private lateinit var buttonTime: Button
    private lateinit var buttonSave: Button

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
        buttonTime = view.findViewById(R.id.button_time)
        checkBoxSolved = view.findViewById(R.id.checkbox_solved)
        checkBoxPolice = view.findViewById(R.id.checkbox_police)
        buttonSave = view.findViewById(R.id.button_save)

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
        val titleWatcher = object : TextWatcher {
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

        buttonDate.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        buttonTime.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_TIME)
            }
        }

        checkBoxSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
                checkBoxPolice.isEnabled = !crime.isSolved
            }
        }

        checkBoxPolice.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.requirePolice = isChecked
                checkBoxSolved.isEnabled = !crime.requirePolice
            }
        }

        buttonSave.setOnClickListener {
            if (editTextTitle.text.toString().trim().isNotEmpty()) {
                crimeDetailViewModel.saveCrime(crime)
                Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show()
            } else {
                editTextTitle.error = "title cant empty"
                editTextTitle.requestFocus()
                return@setOnClickListener
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUI() {
        editTextTitle.setText(crime.title)
        buttonDate.text = DateFormat.format("dd/ MM/ yyyy", crime.date)
        val crimeTime = SimpleDateFormat("hh:mm a").format(this.crime.date)
        buttonTime.text = crimeTime
        checkBoxSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        checkBoxPolice.apply {
            isChecked = crime.requirePolice
            jumpDrawablesToCurrentState()
        }
    }

    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}