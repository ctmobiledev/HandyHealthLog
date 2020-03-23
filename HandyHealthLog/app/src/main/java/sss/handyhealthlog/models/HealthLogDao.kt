package sss.handyhealthlog.models

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface HealthLogDao {

    @Query("SELECT * FROM HealthLogModel ORDER BY logId")               // Ascending timestamps
    fun getAllEventsToList(): List<HealthLogModel>

    @Query("SELECT * FROM HealthLogModel ORDER BY logId DESC")          // Descending timestamps
    fun getAllEventsToListDesc(): List<HealthLogModel>

    @Query("SELECT * FROM HealthLogModel ORDER BY logId")               // Ascending, but LiveData
    fun getAllEventsToLiveData(): LiveData<List<HealthLogModel>>

    @Query("SELECT * FROM HealthLogModel ORDER BY logId DESC")          // Descending, but LiveData
    fun getAllEventsToLiveDataDesc(): LiveData<List<HealthLogModel>>

    // NOT CONVENIENT
    //@RawQuery
    //fun getAllEventsToLiveDataChgSort(queryString: String?): LiveData<List<HealthLogModel>>

    @Query("SELECT * FROM HealthLogModel WHERE logId = :sarg")
    fun getEventWithLogId(sarg: Long): LiveData<HealthLogModel>

    // "vararg" only needed for 2+ rows/objects
    // these don't return anything so no LiveData markers are needed

    @Insert
    fun insert(newModel: HealthLogModel)

    @Update
    fun updateUsers(users: HealthLogModel)

    @Delete
    fun delete(delModel: HealthLogModel)

    @Query("DELETE FROM HealthLogModel")
    fun deleteAllEvents()

}