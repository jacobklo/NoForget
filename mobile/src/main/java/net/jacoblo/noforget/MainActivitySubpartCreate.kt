package net.jacoblo.noforget

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// REMEMBER : Use Kotline Extension capability to seperate functions to another file

internal fun MainActivity.createNewDateSubView( defaultDatesSize: Int ) {
  val datesLayout = findViewById<LinearLayout>(R.id.datesContainer)
  val numOfDates = datesLayout.childCount

  val dateTimeGroup = LinearLayout(this)
  dateTimeGroup.orientation = LinearLayout.HORIZONTAL
  val dateTimeGroupLL = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
  dateTimeGroup.layoutParams = dateTimeGroupLL
  datesLayout?.addView(dateTimeGroup)

  val defaultNewDate = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].toLocalDate().toString() else "Date"
  val newDateEditText = createCustomPickerEditText(this, defaultNewDate, 0.30f ) {
    newPickerEditText: EditText ->
    createDatePickerDialog(LocalDateTime.now().plusDays(1), newPickerEditText).show()
  }

  val defaultNewTime = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].toLocalTime().toString() else "Time"
  val newTimeEditText = createCustomPickerEditText(this, defaultNewTime, 0.30f ) {
    newPickerEditText: EditText ->
    createTimePickerDialog(LocalDateTime.now(), newPickerEditText).show()
  }

  val newDeleteButton = Button(this)
  newDeleteButton.text = "--"
  newDeleteButton.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
  val newDeleteButtonLL : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.40f)
  newDeleteButton.layoutParams = newDeleteButtonLL

  newDeleteButton.setOnClickListener { _ : View? ->
    datesLayout.removeView(dateTimeGroup)
  }

  dateTimeGroup.addView(newDateEditText)
  dateTimeGroup.addView(newTimeEditText)
  dateTimeGroup.addView(newDeleteButton)
}

internal fun MainActivity.createCustomPickerEditText(context: Context, text: CharSequence, layoutWeight: Float, f: (newPickerEditText: EditText ) -> Unit): EditText {
  val newPickerEditText = EditText(context)
  newPickerEditText.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
  newPickerEditText.focusable = View.NOT_FOCUSABLE
  newPickerEditText.setText(text, TextView.BufferType.EDITABLE)
  newPickerEditText.setEnabled(true)
  val newPickerEditTextLL : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, layoutWeight)
  newPickerEditText.layoutParams = newPickerEditTextLL

  newPickerEditText.setOnClickListener{ _ : View? ->
    f(newPickerEditText)
  }
  return newPickerEditText
}

internal fun MainActivity.createTimePickerDialog(defaultDate: LocalDateTime, timeEntry: EditText): TimePickerDialog {
  val lis = TimePickerDialog.OnTimeSetListener {
    _ : TimePicker, hourOfDay: Int, minute: Int ->
    timeEntry.setText( LocalTime.of(hourOfDay, minute, 0).toString() )
  }
  return TimePickerDialog(this, lis, defaultDate.hour, defaultDate.minute, true)
}

internal fun MainActivity.createDatePickerDialog(defaultDate: LocalDateTime, dateEntry: EditText): DatePickerDialog {
  val lis = DatePickerDialog.OnDateSetListener {
    _ : DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
    // REMEMBER : month and day started at zero
    dateEntry.setText( LocalDate.of(year, monthOfYear +1, dayOfMonth).toString() )
  }
  // REMEMBER : month and day started at 1
  return DatePickerDialog(this, lis, defaultDate.year, defaultDate.monthValue - 1, defaultDate.dayOfMonth - 1)
}