package sss.handyhealthlog.viewmodels

import android.app.Application
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sss.handyhealthlog.models.AppDatabase
import sss.handyhealthlog.models.HealthLogModel
import sss.handyhealthlog.models.HealthLogRepository
import java.io.*


// Class extends AndroidViewModel and requires application as a parameter.

class HealthLogViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: HealthLogRepository

    // Moved here
    private var healthLogDao = AppDatabase.getDatabase(application, viewModelScope).healthLogDao()

    // LiveData gives us updated events when they change.
    var allEventsLiveData: LiveData<List<HealthLogModel>>
    var allEventsLiveDataDesc: LiveData<List<HealthLogModel>>

    // This method is for exports only.
    var allEvents: List<HealthLogModel>

    init {

        // Gets reference to DAO from AppDatabase to construct the correct Repository.
        repository = HealthLogRepository(healthLogDao)

        // Get data from repository methods; each is its own data source
        allEventsLiveData = repository.allEventsLiveData
        allEventsLiveDataDesc = repository.allEventsLiveDataDesc
        allEvents = repository.allEvents

        println(">>> allEventsLiveData = " + allEventsLiveData.toString())
        println(">>> allEventsLiveDataDesc = " + allEventsLiveDataDesc.toString())
        println(">>> allEvents = " + allEvents.toString())

    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a co-routine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */

    // Had to update the androidx.lifecycle:lifecycle-viewmodel-ktx reference in Gradle to get
    // viewModelScope recognized.

    // The Activity calls THIS, and THIS in turn calls the 'insert' method for the repository,
    // which fires the DAO in turn and talks to SQLite. (sigh)

    fun insert(logEvent: HealthLogModel) = viewModelScope.launch {
        repository.insert(logEvent)
    }

    fun clearDatabase() = viewModelScope.launch {
        repository.clearDatabase()
    }

    fun exportEventsToCsvFile() = viewModelScope.launch {

        println(">>> exportEventsToCsvFile()")
        println(">>> repository.allEvents = " + repository.allEvents)

        var events = repository.getEventsForExport()
        var data = getApplication<Application>().getExternalFilesDir(null)

        val directoryPath:String = getApplication<Application>().getExternalFilesDir(null).toString() + File.separator.toString() + "HHL"
        val directory = File(directoryPath)

        println(">>> directoryPath = " + directoryPath)

        if (!directory.exists()) {
            println(">>> DIRECTORY DOESN'T EXIST, CREATING")
            directory.mkdir()
        }

        val newFile = File(directory, "HealthData.csv")

        if (!newFile.exists()) {
            try {
                println(">>> CSV FILE DOESN'T EXIST, CREATING")
                newFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            val fOut = FileOutputStream(newFile)
            val outputWriter = OutputStreamWriter(fOut)

            /*
            @ColumnInfo(name = "logTimestamp") var logTimestamp: String?,
            @ColumnInfo(name = "logDataType") var logDataType: String?,
            @ColumnInfo(name = "metric1") var metric1: Float?,
            @ColumnInfo(name = "unit1") var unit1: String?,
            @ColumnInfo(name = "metric2") var metric2: Float?,
            @ColumnInfo(name = "unit2") var unit2: String?,
            @ColumnInfo(name = "metric3") var metric3: Float?,
            @ColumnInfo(name = "unit3") var unit3: String?,
            @ColumnInfo(name = "eventLocation") var eventLocation: String?,
            @ColumnInfo(name = "description1") var description1: String?,
            @ColumnInfo(name = "description2") var description2: String?
            */

            val delim: String = "|"

            //*************************
            // Write column headers
            //*************************
            outputWriter.write(
                "ID" + delim
                        + "Timestamp" + delim
                        + "DataType" + delim
                        + "Metric1" + delim
                        + "Unit1" + delim
                        + "Metric2" + delim
                        + "Unit2" + delim
                        + "Metric3" + delim
                        + "Unit3" + delim
                        + "Location" + delim
                        + "Description1" + delim
                        + "Description2"
                        + "\n")

            //*************************
            // Write actual data here
            // DON'T ALLOW VERTICAL BARS IN ENTRIES
            //*************************
            for (e in events) {
                outputWriter.write(
                    e.logId.toString() + delim
                        + e.logTimestamp + delim
                        + e.logDataType + delim
                        + e.metric1 + delim
                        + e.unit1 + delim
                        + e.metric2 + delim
                        + e.unit2 + delim
                        + e.metric3 + delim
                        + e.unit3 + delim
                        + e.eventLocation + delim
                        + e.description1 + delim
                        + e.description2
                        + "\n")
            }

            outputWriter.flush()
            outputWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun importEventsFromCsvFile() = viewModelScope.launch {

        println(">>> importEventsFromCsvFile()")
        println(">>> repository.allEvents = " + repository.allEvents)

        var events = repository.getEventsForExport()
        var data = getApplication<Application>().getExternalFilesDir(null)

        val directoryPath:String = getApplication<Application>().getExternalFilesDir(null).toString() + File.separator.toString() + "HHL"
        val directory = File(directoryPath)

        println(">>> directoryPath = " + directoryPath)

        if (!directory.exists()) {
            println(">>> DIRECTORY DOESN'T EXIST, CREATING")
            directory.mkdir()
            Toast.makeText(getApplication<Application>().applicationContext,
                "A file called ImportHealthData.csv was not found on your device. Please view 'How To Use This App' to learn how to create this file.",
                Toast.LENGTH_LONG).show()
            return@launch
        }

        val importFile = File(directory, "ImportHealthData.csv")

        if (!importFile.exists()) {
            Toast.makeText(getApplication<Application>().applicationContext,
                "A file called ImportHealthData.csv was not found on your device. Please view 'How To Use This App' to learn how to create this file.",
            Toast.LENGTH_LONG).show()
        }
        else
        {

            try {
                val istream = FileInputStream(importFile)
                val ifile = InputStreamReader(istream)

                /*
                @ColumnInfo(name = "logTimestamp") var logTimestamp: String?,
                @ColumnInfo(name = "logDataType") var logDataType: String?,
                @ColumnInfo(name = "metric1") var metric1: Float?,
                @ColumnInfo(name = "unit1") var unit1: String?,
                @ColumnInfo(name = "metric2") var metric2: Float?,
                @ColumnInfo(name = "unit2") var unit2: String?,
                @ColumnInfo(name = "metric3") var metric3: Float?,
                @ColumnInfo(name = "unit3") var unit3: String?,
                @ColumnInfo(name = "eventLocation") var eventLocation: String?,
                @ColumnInfo(name = "description1") var description1: String?,
                @ColumnInfo(name = "description2") var description2: String?
                */

                val delim: String = "|"

                var fileLines = ifile.readLines()

                println(">>> **************** FILE CONTENTS: **************** ")
                for (fLine in fileLines) {

                    println(">>> " + fLine)

                    var dataColumns = fLine.split('|')

                    // Remember - first line is headers, ignore (check by the substring "ID|")

                    if (dataColumns[0] != "ID") {           // non-inclusive right value - just like Java

                        println(">>> PARSED DATA:")
                        for (c in dataColumns.indices) {            // 0 to index-1, non-inclusive
                            println(">>>    dataColumns[" + c + "] = " + dataColumns[c])
                        }

                        // Convert the strings to floats
                        val fpMetric1: Float = (if (dataColumns[3] == null) 0 else dataColumns[3].toFloat()) as Float
                        val fpMetric2: Float = (if (dataColumns[5] == null) 0 else dataColumns[5].toFloat()) as Float
                        val fpMetric3: Float = (if (dataColumns[7] == null) 0 else dataColumns[7].toFloat()) as Float

                        var importRecord = HealthLogModel(
                            null,
                            dataColumns[1],
                            dataColumns[2],
                            fpMetric1,
                            dataColumns[4],
                            fpMetric2,
                            dataColumns[6],
                            fpMetric3,
                            dataColumns[8],
                            dataColumns[9],
                            dataColumns[10],
                            dataColumns[11]
                        )

                        try {
                            insert(importRecord)
                            println(">>> Successfully inserted")
                        } catch(e: Exception)
                        {
                            println(">>> HealthLogViewModel.insert - failure on insert of record ID " + dataColumns[0])
                            println(">>> dataColumns[1] = " + dataColumns[1])
                            println(">>> dataColumns[2] = " + dataColumns[2])
                            println(">>> dataColumns[4] = " + dataColumns[4])
                            println(">>> dataColumns[6] = " + dataColumns[6])
                            println(">>> dataColumns[10] = " + dataColumns[10])
                            println(">>> dataColumns[11] = " + dataColumns[11])
                        }

                    }

                }
                println(">>> **************** END **************** ")

                ifile.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

}