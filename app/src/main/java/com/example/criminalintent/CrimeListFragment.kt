package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.database.CrimeDao
import java.util.*

@Suppress("NAME_SHADOWING")
class CrimeListFragment : Fragment() {

    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeListViewModel: CrimeListViewModel
    private lateinit var recyclerViewCrime: RecyclerView
    private lateinit var textViewEmpty: TextView
    private lateinit var buttonAddCrime: Button
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crimeListViewModel = ViewModelProvider(this).get(CrimeListViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.crime_list_fragment, container, false)
        //init
        recyclerViewCrime = view.findViewById(R.id.recycler_view_crime)
        recyclerViewCrime.layoutManager = LinearLayoutManager(context)
        recyclerViewCrime.adapter = adapter

        if (recyclerViewCrime.adapter?.itemCount == 0){
            val view = inflater.inflate(R.layout.empty_view, container, false)
            textViewEmpty = view.findViewById(R.id.text_view_empty)
            buttonAddCrime = view.findViewById(R.id.button_empty)
            buttonAddCrime.setOnClickListener {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
            }
            return view
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    updateUI(crimes)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime ->{
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun updateUI(crimes: List<Crime>){
        adapter = CrimeAdapter(crimes)
        recyclerViewCrime.adapter = adapter
    }

    private inner class CrimeHolder(view: View): RecyclerView.ViewHolder(view){
        //init
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)

        init {
            view.setOnClickListener {
                callbacks?.onCrimeSelected(crime.id)
            }
        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = DateFormat.format("EE - hh:mm  -  dd, MM, yyyy", crime.date)

            if (!crime.requirePolice){
                val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
                solvedImageView.visibility =
                    when{
                        crime.isSolved -> View.VISIBLE
                        else -> View.GONE
                    }
            }

            if (crime.requirePolice && !crime.isSolved){
                val policeButton: ImageButton = itemView.findViewById(R.id.police_button)
                policeButton.setOnClickListener {
                    Toast.makeText(context, "call police", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = when (viewType){
                R.id.list_item_crime -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                else -> layoutInflater.inflate(R.layout.list_item_police, parent, false)
            }
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount() = crimes.size

        override fun getItemViewType(position: Int): Int {
            return when{
                crimes[position].requirePolice -> R.id.list_item_police
                else -> R.id.list_item_crime
            }

        }
    }

    companion object {
        fun newInstance() = CrimeListFragment()
    }
}
