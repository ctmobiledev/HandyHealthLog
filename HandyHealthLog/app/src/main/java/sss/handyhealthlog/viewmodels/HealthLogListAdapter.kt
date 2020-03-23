package sss.handyhealthlog.viewmodels

// THIS MODULE GOVERNS LAYOUT OF DATA IN A CARD VIEW - MAKE ADJUSTMENTS TO FORMATTING HERE

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.logevent_item.view.*
import sss.handyhealthlog.R
import sss.handyhealthlog.models.HealthLogModel
import java.text.DecimalFormat

class HealthLogListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<HealthLogListAdapter.HealthLogViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var logEvents = emptyList<HealthLogModel>() // Cached copy of words

    //********************************************************
    // Holds the raw data - REFERENCE NAMES MUST MATCH BELOW
    //********************************************************
    inner class HealthLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // one for each output field, to be used below

        //val healthLogId: TextView = itemView.outLogId                 // TEST ONLY

        val healthLogTimestamp: TextView = itemView.outTimestamp

        val healthDataType: TextView = itemView.outDataType

        val healthLogDataType: TextView = itemView.outDataType
        val healthLogMetric1: TextView = itemView.outMetric1
        val healthLogUnit1: TextView = itemView.outUnit1
        val healthLogMetric2: TextView = itemView.outMetric2
        val healthLogUnit2: TextView = itemView.outUnit2
        val healthLogMetric3: TextView = itemView.outMetric3
        val healthLogUnit3: TextView = itemView.outUnit3

        val healthLocation: TextView = itemView.outLocation
        val healthDescription1: TextView = itemView.outDescription1
        val healthDescription2: TextView = itemView.outDescription2

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthLogViewHolder {
        val itemView = inflater.inflate(R.layout.logevent_item, parent, false)
        return HealthLogViewHolder(itemView)
    }

    //********************************************************
    // Specify the output controls by name here
    //********************************************************
    override fun onBindViewHolder(holder: HealthLogViewHolder, position: Int) {

        val current = logEvents[position]
        println(">>> onBindViewHolder - current = " + current.logTimestamp)

        // TEST ONLY
        //holder.healthLogId.outLogId.text = current.logId.toString()       // a String? means we need toString()

        val logDate = extractDate(current.logTimestamp.toString())
        val logTime = extractTime(current.logTimestamp.toString())
        //holder.healthLogTimestamp.outTimestamp.text = current.logTimestamp.toString()
        holder.healthLogTimestamp.outTimestamp.text = logDate + "\n" + logTime

        holder.healthDataType.outDataType.text = current.logDataType.toString()

        val fmtNoTrailingZero = DecimalFormat("####0.#")
        val fpMetric1 = current.metric1
        val fpMetric2 = current.metric2
        val fpMetric3 = current.metric3

        holder.healthLogMetric1.outMetric1.text = fmtNoTrailingZero.format(fpMetric1).trim()
        holder.healthLogMetric2.outMetric2.text = fmtNoTrailingZero.format(fpMetric2).trim()
        holder.healthLogMetric3.outMetric3.text = fmtNoTrailingZero.format(fpMetric3).trim()

        holder.healthLogUnit1.outUnit1.text = current.unit1.toString()
        holder.healthLogUnit2.outUnit2.text = current.unit2.toString()
        holder.healthLogUnit3.outUnit3.text = current.unit3.toString()

        // Hide 0.0 when not in use (have formatter later)
        holder.healthLogUnit1.outUnit1.isVisible = (current.unit1.toString() != "")
        holder.healthLogMetric1.outMetric1.isVisible = (current.unit1.toString() != "")

        holder.healthLogUnit2.outUnit2.isVisible = (current.unit2.toString() != "")
        holder.healthLogMetric2.outMetric2.isVisible = (current.unit2.toString() != "")

        holder.healthLogUnit3.outUnit3.isVisible = (current.unit3.toString() != "")
        holder.healthLogMetric3.outMetric3.isVisible = (current.unit3.toString() != "")

        holder.healthLocation.outLocation.text = current.eventLocation.toString()
        holder.healthDescription1.outDescription1.text = current.description1.toString()
        holder.healthDescription2.outDescription2.text = current.description2.toString()

    }

    fun extractDate(tsArg: String): String {
        val year = tsArg.substring(0, 4)
        val month = tsArg.substring(4, 6)
        val date = tsArg.substring(6, 8)
        return "$month/$date/$year"
    }

    fun extractTime(tsArg: String): String {
        val hour = tsArg.substring(9, 11)
        val minute = tsArg.substring(11, 13)
        val second = tsArg.substring(13, 15)
        return "$hour:$minute:$second"
    }

    internal fun setEvents(logEvents: List<HealthLogModel>) {
        this.logEvents = logEvents
        notifyDataSetChanged()                          // tap the observable
    }

    override fun getItemCount() = logEvents.size

}