package sss.handyhealthlog.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import sss.handyhealthlog.models.AppDatabase
import sss.handyhealthlog.models.HealthLogDao
import sss.handyhealthlog.models.HealthLogModel
import sss.handyhealthlog.models.HealthLogRepository

// Class extends AndroidViewModel and requires application as a parameter.

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    var healthLogRepository: HealthLogRepository

    // Data objects
    //var healthEvents: LiveData<List<HealthLogModel>>

    init {
        // https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#8
        val healthLogDao = AppDatabase.getDatabase(application, viewModelScope).healthLogDao()
        healthLogRepository = HealthLogRepository(healthLogDao)
    }


    // Ideally this will go as an async process


}