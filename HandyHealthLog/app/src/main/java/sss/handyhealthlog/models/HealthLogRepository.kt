package sss.handyhealthlog.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

class HealthLogRepository(private val healthLogDao: HealthLogDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data have changed.
    // MutableLiveData punted, said it couldn't figure it out in the Java generated file

    // Best solution for now: two separate queries
    //val eventQuery = "SELECT * FROM HealthLogModel ORDER BY ID DESC"
    //val allEventsLiveData: LiveData<List<HealthLogModel>> = healthLogDao.getAllEventsToLiveDataChgSort(eventQuery)
    // e: C:\Users\charl\AndroidStudioProjects\HandyHealthLog\app\build\tmp\kapt3\stubs\debug\sss\handyhealthlog\models\HealthLogDao.java:19: error: Observable query return type (LiveData, Flowable, DataSource, DataSourceFactory etc) can only be used with SELECT queries that directly or indirectly (via @Relation, for example) access at least one table. For @RawQuery, you should specify the list of tables to be observed via the observedEntities field.
    //    public abstract androidx.lifecycle.LiveData<java.util.List<sss.handyhealthlog.models.HealthLogModel>> getAllEventsToLiveDataChgSort(@org.jetbrains.annotations.Nullable()
    //                                                                                                          ^

    // This is a public property anyone can use - a LiveData list is returned
    // Should this become a RW "var" to allow another DAO method ASC/DESC?
    val allEventsLiveData: LiveData<List<HealthLogModel>> = healthLogDao.getAllEventsToLiveData()
    val allEventsLiveDataDesc: LiveData<List<HealthLogModel>> = healthLogDao.getAllEventsToLiveDataDesc()

    // This is another one, but for exporting records to CSV.
    val allEvents: List<HealthLogModel> = healthLogDao.getAllEventsToList()
    val allEventsDesc: List<HealthLogModel> = healthLogDao.getAllEventsToListDesc()

    var eventList = mutableListOf<HealthLogModel>()

    suspend fun insert(logEvent: HealthLogModel) {
        healthLogDao.insert(logEvent)
    }

    suspend fun clearDatabase() {
        healthLogDao.deleteAllEvents()
    }

    suspend fun getEventsForExport(): MutableList<HealthLogModel> {
        eventList = allEvents.toMutableList()
        println(">>> eventList = " + eventList.toString())
        return eventList
    }

}