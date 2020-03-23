package sss.handyhealthlog.views

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_edit_event.*
import sss.handyhealthlog.R
import sss.handyhealthlog.models.HealthLogModel
import sss.handyhealthlog.viewmodels.HealthLogViewModel
import java.lang.Float.parseFloat
import java.text.SimpleDateFormat
import java.util.*


class EditEventActivity : AppCompatActivity() {

    // Globals
    private lateinit var healthLogViewModel: HealthLogViewModel
    private var currDataType: String = String()
    private var proceedToSave: Boolean = false                      // Determined if record is complete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        // Users won't need to alter this, but this will confirm the new record is "fresh"
        DisableEditText(edtTimestamp)

        // Spinner setup
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Had to do a Stop Application, then Invalidate/Restart to get the custom layout recognized.
        val spnDataType: Spinner = findViewById(R.id.spnDataType)
        ArrayAdapter.createFromResource(
            this,
            R.array.data_types_array,
            R.layout.datatype_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnDataType.adapter = adapter
        }

        spnDataType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                pos: Int,
                id: Long
            ) {

                currDataType = parent.getItemAtPosition(pos).toString()

                // Clear previous values.

                edtMetric1.isEnabled = true
                edtMetric2.isEnabled = true
                edtMetric3.isEnabled = true
                edtMetric1.setText("")
                edtMetric2.setText("")
                edtMetric3.setText("")

                val labelBlack = "#000000"
                val labelDimmed = "#BBBBBB"

                lblMetric1.setTextColor(Color.parseColor(labelBlack))
                lblMetric2.setTextColor(Color.parseColor(labelBlack))
                lblMetric3.setTextColor(Color.parseColor(labelBlack))

                // Each type triggers a different set of units.

                if (currDataType == "BP") {
                    edtUnit1.setText("Systolic mmHg")
                    edtUnit2.setText("Diastolic mmHg")
                    edtUnit3.setText("Pulse BPM")
                }
                if (currDataType == "EXERC") {
                    edtUnit1.setText("Minutes")
                    edtUnit2.setText("Miles")
                    edtUnit3.setText("Laps")
                }
                if (currDataType == "GLU") {
                    edtUnit1.setText("mg/dL")
                    edtUnit2.setText("")
                    edtUnit3.setText("")
                    edtMetric2.isEnabled = false
                    edtMetric3.isEnabled = false
                    lblMetric2.setTextColor(Color.parseColor(labelDimmed))
                    lblMetric3.setTextColor(Color.parseColor(labelDimmed))
                }
                if (currDataType == "OTHER") {
                    edtUnit1.setText("Value 1")
                    edtUnit2.setText("Value 2")
                    edtUnit3.setText("Value 3")
                }
                if (currDataType == "RX") {
                    edtUnit1.setText("pills")
                    edtUnit2.setText("tsp")
                    edtUnit3.setText("dosage")
                }
                if (currDataType == "TEMP") {
                    edtUnit1.setText("deg F")
                    edtUnit2.setText("")
                    edtUnit3.setText("")
                    edtMetric2.isEnabled = false
                    edtMetric3.isEnabled = false
                    lblMetric2.setTextColor(Color.parseColor(labelDimmed))
                    lblMetric3.setTextColor(Color.parseColor(labelDimmed))
                }
                if (currDataType == "WGT") {
                    edtUnit1.setText("pounds")
                    edtUnit2.setText("ounces")
                    edtMetric3.isEnabled = false
                    lblMetric3.setTextColor(Color.parseColor(labelDimmed))
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // nothing
            }
        }

        // Get the viewModel and, in turn, the connection to the database
        healthLogViewModel = ViewModelProviders.of(this).get(HealthLogViewModel::class.java)

        // Populate timestamp
        var tstest = generateTimestamp()
        println(">>> tstest = " + tstest)
        edtTimestamp.setText(generateTimestamp())               // odd...EditText controls can't be set with .text = ...

        btnSave.setOnClickListener {

            // If one or more values are left blank, make sure that's what the user really wants.
            var availableFields = 0
            var completedFields = 0

            if (lblMetric1.isEnabled) {
                availableFields++
                if (edtMetric1.text.toString().trim() != "") {
                    completedFields++
                }
            }
            if (lblMetric2.isEnabled) {
                availableFields++
                if (edtMetric2.text.toString().trim() != "") {
                    completedFields++
                }
            }
            if (lblMetric3.isEnabled) {
                availableFields++
                if (edtMetric3.text.toString().trim() != "") {
                    completedFields++
                }
            }

            println(">>> availableFields = " + availableFields)
            println(">>> completedFields = " + completedFields)

            if (completedFields == 0) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Blank Fields")
                builder.setMessage("You didn't fill in any data value fields. All values will show 0. " +
                        "Are you sure you wish to save this record this way?")
                builder.setPositiveButton("Yes, Continue", DialogInterface.OnClickListener {
                        dialog, id ->
                    insertEventWithMessage()
                    return@OnClickListener
                })
                builder.setNegativeButton("No, Cancel", DialogInterface.OnClickListener {
                        dialog, id ->
                    println(">>> Save ignored")
                })
                builder.show()

            }
            else
            {

                println(">>> All metric entry fields completed; proceedToSave is $proceedToSave")
                insertEventWithMessage()

            }

        }

        btnCancel.setOnClickListener {
            finish()
        }

    }


    private fun insertEventWithMessage() {

        // Parse entered values, validate
        var strMetric1 = edtMetric1.text.toString()
        var fpMetric1 = 0f
        fpMetric1 = try {
            parseFloat(strMetric1)
        } catch (ex: Exception) {
            0f
        }

        var strMetric2 = edtMetric2.text.toString()
        var fpMetric2 = 0f
        fpMetric2 = try {
            parseFloat(strMetric2)
        } catch (ex: Exception) {
            0f
        }

        var strMetric3 = edtMetric3.text.toString()
        var fpMetric3 = 0f
        fpMetric3 = try {
            parseFloat(strMetric3)
        } catch (ex: Exception) {
            0f
        }

        // Get input values from UI controls
        // Input type mask precludes need to parse-test input values
        var newLogEvent: HealthLogModel = HealthLogModel(
            null,
            edtTimestamp.text.toString(),
            currDataType,
            fpMetric1,
            edtUnit1.text.toString(),
            fpMetric2,
            edtUnit2.text.toString(),
            fpMetric3,
            edtUnit3.text.toString(),
            edtLocation.text.toString().trim(),
            edtDescription1.text.toString().trim(),
            edtDescription2.text.toString().trim()
        )

        // Call save method in viewModel
        healthLogViewModel.insert(newLogEvent)

        // Indicate completion
        Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show()
        finish()

    }


    private fun generateTimestamp(): String {
        val currDate = Date()
        val pattern = "yyyyMMdd_HHmmss"
        val simpleDateFormat = SimpleDateFormat(pattern)
        println(">>> generateTimestamp - output date is: " + simpleDateFormat.format(currDate))
        return simpleDateFormat.format(currDate)
    }

    private fun DisableEditText(
        editText: EditText
    ) {
        val isEnabled: Boolean = false

        editText.isFocusable = isEnabled
        editText.isFocusableInTouchMode = isEnabled
        editText.isClickable = isEnabled
        editText.isLongClickable = isEnabled
        editText.isCursorVisible = isEnabled
    }

}



