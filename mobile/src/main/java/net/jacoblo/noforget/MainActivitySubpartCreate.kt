package net.jacoblo.noforget

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// REMEMBER : Use Kotline Extension capability to seperate functions to another file
val MainActivity.m_DefaultReminderDates: ArrayList<LocalDateTime>
    get() = createDefaultReminderDates()

internal fun MainActivity.createDefaultReminderDates(): ArrayList<LocalDateTime> {
  val result = ArrayList<LocalDateTime>()
  result.add(LocalDateTime.now().plusDays(1))
  result.add(LocalDateTime.now().plusDays(3))
  result.add(LocalDateTime.now().plusDays(7))
  result.add(LocalDateTime.now().plusDays(30))
  result.add(LocalDateTime.now().plusDays(90))
  result.add(LocalDateTime.now().plusDays(365))
  return result
}

internal fun MainActivity.onCreateCreatePart() {
  create_save.setOnClickListener { _ : View? ->
    saveToFile("NoForget.txt",memoryDataToJson(m_MemoryData))
  }

  create_new.setOnClickListener{ _ : View? ->
    createNewMemoryEntry()
  }

  create_add_date.setOnClickListener { _ : View? ->
    createNewDateSubView( m_DefaultReminderDates.size )
  }

  create_delete.setOnClickListener{ _ : View? ->
    clearUnsavedEntry()
  }
}

internal fun MainActivity.saveToFile(fileName: String, fileContents: String) {
  val saveFileDir = File(Environment.getExternalStorageDirectory(), "NoForget")
  if (!saveFileDir.exists() && !saveFileDir.mkdir()) {
    Log.e(LOG_TAG, "cannot create save file for NoForget")
    Toast.makeText(applicationContext, "ERROR: cannot save, please able storage permission.", Toast.LENGTH_LONG).show()
  }

  val saveFilePath = saveFileDir.absolutePath + "/" + fileName
  val fos = FileOutputStream(saveFilePath)
  val osw = OutputStreamWriter(fos)
  osw.write(fileContents)
  osw.close()

}

internal fun MainActivity.createNewMemoryEntry() {

  var newMemoryEntry: MemoryEntry = MemoryEntry(m_CurrentMemoryEntryCount
          , LocalDateTime.now()
          , findViewById<EditText>(R.id.create_name).text.toString()
          , convertDatesViewGroupToDatesArray(datesContainer)
          , findViewById<EditText>(R.id.create_data).text.toString()
  )
  m_CurrentMemoryEntryCount++
  m_MemoryData.memory_entries.add(newMemoryEntry)

  // clear
  clearUnsavedEntry()

  m_TitleViewAdapter.notifyDataSetChanged()
}


internal fun MainActivity.convertDatesViewGroupToDatesArray( dateTimeGroup: ViewGroup): ArrayList<LocalDateTime>  {

  val result = ArrayList<LocalDateTime>()

  for ( i in 0..dateTimeGroup.childCount ) {

    // check if actually LinearLayout
    if ( dateTimeGroup.getChildAt(i) !is LinearLayout) {
      return result; //Empty as not dateTimeGroup
    }

    val child = dateTimeGroup.getChildAt(i) as LinearLayout;
    val dateEditText = child.getChildAt(0) as EditText;
    val timeEditText = child.getChildAt(1) as EditText;

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    try {
      val childLocalDateTime = LocalDateTime.parse(dateEditText.text.toString() + " " + timeEditText.text.toString(), dateTimeFormatter)
      result.add(childLocalDateTime)
    }catch ( _ : DateTimeParseException) {}
  }
  return result
}

internal fun MainActivity.createNewDateSubView( defaultDatesSize: Int ) {
  val datesLayout = findViewById<LinearLayout>(R.id.datesContainer)
  val numOfDates = datesLayout.childCount

  val dateTimeGroup = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL
    val dateTimeGroupLL = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    layoutParams = dateTimeGroupLL
  }
  datesLayout?.addView(dateTimeGroup)

  var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val defaultNewDate = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].format(dateTimeFormatter) else "Date"
  val newDateEditText = createCustomPickerEditText(this, defaultNewDate, 0.30f ) {
    newPickerEditText: EditText ->
    createDatePickerDialog(LocalDateTime.now().plusDays(1), newPickerEditText).show()
  }

  dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
  val defaultNewTime = if (numOfDates < defaultDatesSize ) m_DefaultReminderDates[numOfDates].format(dateTimeFormatter) else "Time"
  val newTimeEditText = createCustomPickerEditText(this, defaultNewTime, 0.30f ) {
    newPickerEditText: EditText ->
    createTimePickerDialog(LocalDateTime.now(), newPickerEditText).show()
  }

  val newDeleteButton = Button(this).apply {
    text = "--"
    setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    val newDeleteButtonLL : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.40f)
    layoutParams = newDeleteButtonLL

    setOnClickListener { _ : View? ->
      datesLayout.removeView(dateTimeGroup)
    }
  }

  dateTimeGroup.addView(newDateEditText)
  dateTimeGroup.addView(newTimeEditText)
  dateTimeGroup.addView(newDeleteButton)
}

internal fun MainActivity.createCustomPickerEditText(context: Context, text: CharSequence, layoutWeight: Float, f: (newPickerEditText: EditText ) -> Unit): EditText {
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

internal fun MainActivity.clearUnsavedEntry() {
  create_name.text = null
  create_data.text = null
  datesContainer.removeAllViews()
}