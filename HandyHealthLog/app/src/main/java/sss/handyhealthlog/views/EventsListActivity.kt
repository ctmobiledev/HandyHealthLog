package sss.handyhealthlog.views

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sss.handyhealthlog.R
import sss.handyhealthlog.viewmodels.HealthLogListAdapter
import sss.handyhealthlog.viewmodels.HealthLogViewModel


class EventsListActivity : AppCompatActivity() {

    // Globals
    private lateinit var healthLogViewModel: HealthLogViewModel
    private var sortAscending = false

    // List and Adapter support
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HealthLogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_list)

        // List and Adapter support
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = HealthLogListAdapter(this)

        // Recycler View wiring
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(this)

        // Adjust columns
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        // Get the viewModel and, in turn, the connection to the database
        healthLogViewModel = ViewModelProviders.of(this).get(HealthLogViewModel::class.java)

        // Set it up for the default sort order with the base method
        healthLogViewModel.allEventsLiveDataDesc.observe(this, Observer { logEvents ->
            //*******************************************************
            // THIS IS WHERE THE DATA SOURCE IS ACTUALLY UPDATED
            // THE ADAPTER OBJECT IS POINTED TO THE logEvents LAMBDA
            // WHICH THEN UPDATES THE CACHED COPY OF THE RECORDS.
            //*******************************************************
            logEvents?.let {
                adapter?.setEvents(it)                   // change the data source HERE
            }
        })

        Toast.makeText(this, "Events are sorted Latest to Earliest.", Toast.LENGTH_SHORT).show()

        /*
        btnClose.setOnClickListener {
            finish()
        }
         */
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_events_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val id: Int = item.getItemId()

        when(id) {
            R.id.action_sort -> changeSortOrder()
            // R.id.action_filter -> changeFilter()                 // LATER RELEASE
            else -> println("Invalid menu selection")
        }

        return true
    }

    private fun changeSortOrder() {
        var queryString = ""

        sortAscending = !(sortAscending)
        println(">>> sortAscending = " + sortAscending)

        //*******************************************************************************
        // Question: How do we trigger a refresh of LiveData?
        // Won't matter if it's a change of sort ordering or excluding certain records
        //*******************************************************************************

        var sortOrderString = "Earliest to Latest"

        if (sortAscending) {
            println(">>> Call Ascending DAO")
            sortOrderString = "Earliest to Latest"
            healthLogViewModel.allEventsLiveData.observe(this, Observer { logEvents ->
                //*******************************************************
                // THIS IS WHERE THE DATA SOURCE IS ACTUALLY UPDATED
                // THE ADAPTER OBJECT IS POINTED TO THE logEvents LAMBDA
                // WHICH THEN UPDATES THE CACHED COPY OF THE RECORDS.
                //*******************************************************
                logEvents?.let {
                    adapter?.setEvents(it)                   // change the data source HERE
                }
            })
        }
        else {
            println(">>> Call Descending DAO")
            sortOrderString = "Latest to Earliest"
            healthLogViewModel.allEventsLiveDataDesc.observe(this, Observer { logEvents ->
                //*******************************************************
                // THIS IS WHERE THE DATA SOURCE IS ACTUALLY UPDATED
                // THE ADAPTER OBJECT IS POINTED TO THE logEvents LAMBDA
                // WHICH THEN UPDATES THE CACHED COPY OF THE RECORDS.
                //*******************************************************
                logEvents?.let {
                    adapter?.setEvents(it)                   // change the data source HERE
                }
            })
        }

        Toast.makeText(this, "Sort order changed: $sortOrderString", Toast.LENGTH_SHORT).show()

    }

    fun changeFilter() {

        Toast.makeText(this, "Filter selections changed", Toast.LENGTH_SHORT).show()

    }

}
