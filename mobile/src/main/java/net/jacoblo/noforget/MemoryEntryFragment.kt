package net.jacoblo.noforget

import android.app.DatePickerDialog
import android.app.Fragment
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MemoryEntryFragment: Fragment() {

  var m_DefaultReminderDates: ArrayList<LocalDateTime> = createDefaultReminderDates()

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater!!.inflate(R.layout.memory_entry_fragment, container, false)

    val addDateButton = view.findViewById<Button>( R.id.memory_entry_fragment_add_date )
    addDateButton?.setOnClickListener{
      createNewDateSubView( m_DefaultReminderDates.size )
    }
    return view
  }

  private fun createNewDateSubView( defaultDatesSize: Int ) {
    val datesLayout = view.findViewById<LinearLayout>(R.id.memory_entry_fragment_dates_container)
    if (datesLayout == null ) return

    val numOfDates = datesLayout.childCount

    val dateTimeGroup = LinearLayout(activity.applicationContext).apply {
      orientation = LinearLayout.HORIZONTAL
      val dateTimeGroupLL = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
      layoutParams = dateTimeGroupLL
    }
    datesLayout.addView(dateTimeGroup)

    var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val defaultNewDate = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].format(dateTimeFormatter) else "Date"
    val newDateEditText = createCustomPickerEditText(activity.applicationContext, defaultNewDate, 0.30f ) {
      newPickerEditText: EditText ->
      createDatePickerDialog(LocalDateTime.now().plusDays(1), newPickerEditText, activity.applicationContext).show()
    }

    dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val defaultNewTime = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].format(dateTimeFormatter) else "Time"
    val newTimeEditText = createCustomPickerEditText(activity.applicationContext, defaultNewTime, 0.30f ) {
      newPickerEditText: EditText ->
      createTimePickerDialog(LocalDateTime.now(), newPickerEditText, activity.applicationContext).show()
    }

    val newDeleteButton = Button(activity.applicationContext).apply {
      text = "--"
      setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
      val newDeleteButtonLL : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.40f)
      layoutParams = newDeleteButtonLL

      setOnClickListener {
        datesLayout.removeView(dateTimeGroup)
      }
    }

    dateTimeGroup.addView(newDateEditText)
    dateTimeGroup.addView(newTimeEditText)
    dateTimeGroup.addView(newDeleteButton)
  }

  private fun createCustomPickerEditText(context: Context, text: CharSequence, layoutWeight: Float, f: (newPickerEditText: EditText ) -> Unit): EditText {
    // REMEMBER : use .apply{} kotlin method to save code
    val newPickerEditText = EditText(context).apply {
      setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
      focusable = View.NOT_FOCUSABLE
      setText(text, TextView.BufferType.EDITABLE)
      setEnabled(true)
      val newPickerEditTextLL : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, layoutWeight)
      layoutParams = newPickerEditTextLL
    }
    newPickerEditText.setOnClickListener{ _ : View? ->
      f(newPickerEditText)
    }
    return newPickerEditText
  }

  private fun createTimePickerDialog(defaultDate: LocalDateTime, timeEntry: EditText, context: Context): TimePickerDialog {
    val lis = TimePickerDialog.OnTimeSetListener {
      _ : TimePicker, hourOfDay: Int, minute: Int ->
      timeEntry.setText( LocalTime.of(hourOfDay, minute, 0).toString() )
    }
    return TimePickerDialog(context, lis, defaultDate.hour, defaultDate.minute, true)
  }

  private fun createDatePickerDialog(defaultDate: LocalDateTime, dateEntry: EditText, context: Context): DatePickerDialog {
    val lis = DatePickerDialog.OnDateSetListener {
      _ : DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
      // REMEMBER : month and day started at zero
      dateEntry.setText( LocalDate.of(year, monthOfYear +1, dayOfMonth).toString() )
    }
    // REMEMBER : month and day started at 1
    return DatePickerDialog(context, lis, defaultDate.year, defaultDate.monthValue - 1, defaultDate.dayOfMonth - 1)
  }

  private fun createDefaultReminderDates(): ArrayList<LocalDateTime> {
    val result = ArrayList<LocalDateTime>()
    result.add(LocalDateTime.now().plusDays(1))
    result.add(LocalDateTime.now().plusDays(3))
    result.add(LocalDateTime.now().plusDays(7))
    result.add(LocalDateTime.now().plusDays(30))
    result.add(LocalDateTime.now().plusDays(90))
    result.add(LocalDateTime.now().plusDays(365))
    return result
  }
}
