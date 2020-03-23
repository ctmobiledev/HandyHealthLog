package sss.handyhealthlog.views

import android.Manifest
import android.R.id.message
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import sss.handyhealthlog.R
import sss.handyhealthlog.viewmodels.HealthLogViewModel
import sss.handyhealthlog.viewmodels.MainViewModel
import java.net.HttpURLConnection
import java.net.URL


private const val PERMISSION_TO_WRITE: Int = 1
private const val PERMISSION_TO_READ: Int = 2


class MainActivity : AppCompatActivity() {

    // Globals
    private lateinit var mainViewModel: MainViewModel
    private lateinit var healthLogViewModel: HealthLogViewModel

    private var permissionToWrite: Boolean = false
    private var permissionToRead: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserPermissions()

        // Get the viewModel
        healthLogViewModel = ViewModelProviders.of(this).get(HealthLogViewModel::class.java)

        // Wire up the buttons
        cardAddNewEvent.setOnClickListener {
            val i = Intent(this, EditEventActivity::class.java)
            startActivity(i)
        }

        cardViewAllEvents.setOnClickListener {
            val i = Intent(this, EventsListActivity::class.java)
            startActivity(i)
        }

        cardExportEvents.setOnClickListener {
            ExportAsyncTask().execute()
            //healthLogViewModel.exportEventsToCsvFile()
            //Toast.makeText(this, "Events exported to folder 'HHL' on your Android device.", Toast.LENGTH_SHORT).show()
        }

        cardImportEvents.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Import Will Overwrite Everything")
            builder.setMessage("You are about to import new CSV data. This will delete any previous data in the database. This operation cannot " +
                "be undone. Are you sure you wish to import?")
            builder.setPositiveButton("Yes, Proceed", DialogInterface.OnClickListener {
                    dialog, id ->
                ImportAsyncTask().execute()
                //healthLogViewModel.clearDatabase()
                //healthLogViewModel.importEventsFromCsvFile()
                //Toast.makeText(this, "Events imported successfully.", Toast.LENGTH_SHORT).show()
            })
            builder.setNegativeButton("No, Cancel", null)
            builder.show()

        }

        cardHowToUse.setOnClickListener {
            val i = Intent(this, HowToUseActivity::class.java)
            startActivity(i)
        }

        cardAbout.setOnClickListener {
            val i = Intent(this, AboutActivity::class.java)
            startActivity(i)
        }

    }


    private fun makeWriteRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_TO_WRITE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_TO_WRITE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionToWrite = true
                    println(">>> WRITE PERMISSION: GRANTED")
                } else {
                    println(">>> WRITE PERMISSION: DENIED")
                    finish()            // we're outta here
                }

                // Turn Export and Import buttons on
                cardExportEvents.isVisible = permissionToWrite
                return

            }
            PERMISSION_TO_READ -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionToRead = true
                    println(">>> READ PERMISSION: GRANTED")

                    // Turn Export and Import buttons on
                    //cardExportEvents.isVisible = true
                } else {
                    println(">>> READ PERMISSION: DENIED")
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun checkUserPermissions() {

        println(">>> checkUserPermissions() fired")

        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            println(">>> PERMISSION TO WRITE NOT GRANTED YET")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access storage is required to allow exporting of data to CSV files.")
                    .setTitle("Permission required")
                builder.setPositiveButton("OK") {
                        dialog, id ->
                    println(">>> Clicked")
                    makeWriteRequest()
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                makeWriteRequest()
            }
        } else {
            println(">>> PERMISSION TO WRITE GRANTED PREVIOUSLY")
            permissionToWrite = true
            cardExportEvents.isVisible = true
        }

    }


    //******************************************************
    // ASYNC TASKS
    // Drop the heavy lifting in "doInBackground" and the
    // other methods are for communicating with the user.
    //******************************************************

    inner class ExportAsyncTask : AsyncTask<Void, Int, String>() {          // parms: <doInBackground, onProgressUpdate, onPostExecute>

        override fun onPreExecute() {
            println(">>> ExportAsyncTask.onPreExecute() fired")
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String? {
            println(">>> ExportAsyncTask.doInBackground() fired")
            healthLogViewModel.exportEventsToCsvFile()
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            // This just loops an animation for now
            println(">>> ExportAsyncTask.onProgressUpdate() fired")
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            println(">>> ExportAsyncTask.onPostExecute() fired")
            progressBar.visibility = View.GONE
            Toast.makeText(applicationContext,
                "Events exported to folder 'HHL' on your Android device.",
                Toast.LENGTH_SHORT).show()
        }
    }

    inner class ImportAsyncTask : AsyncTask<Void, Int, String>() {          // parms: <doInBackground, onProgressUpdate, onPostExecute>

        override fun onPreExecute() {
            println(">>> ImportAsyncTask.onPreExecute() fired")
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String? {
            println(">>> ImportAsyncTask.doInBackground() fired")
            healthLogViewModel.clearDatabase()
            healthLogViewModel.importEventsFromCsvFile()
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            // This just loops an animation for now
            println(">>> ImportAsyncTask.onProgressUpdate() fired")
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            println(">>> ImportAsyncTask.onPostExecute() fired")
            progressBar.visibility = View.GONE
            Toast.makeText(applicationContext,
                "Events imported successfully.",
                Toast.LENGTH_SHORT).show()
        }
    }

}
