package sss.handyhealthlog.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HealthLogModel(
    @PrimaryKey(autoGenerate = true) var logId: Int?,
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
)