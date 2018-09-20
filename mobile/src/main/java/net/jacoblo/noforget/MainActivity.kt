package net.jacoblo.noforget

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.text.InputType
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

class MainActivity : AppCompatActivity() {

  val LOG_TAG = "NoForget Log"
  var m_CurrentMemoryEntryCount = 0;
  val m_MemoryData = MemoryData(0, ArrayList<MemoryEntry>() )
  val m_DefaultReminderDates = createDefaultReminderDates()

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    setVisibilities(item.itemId)
  }

  private fun setVisibilities(whichNavigationItemSelectedId: Int): Boolean {
    when (whichNavigationItemSelectedId) {
      R.id.navigation_home
    , R.id.navigation_notifications -> {
        create_name.visibility = View.INVISIBLE
        create_data.visibility = View.INVISIBLE
        datesContainer.visibility = View.INVISIBLE
        buttonsContainers.visibility = View.INVISIBLE
        return true
      }
      R.id.navigation_create -> {
        create_name.visibility = View.VISIBLE
        create_data.visibility = View.VISIBLE
        datesContainer.visibility = View.VISIBLE
        buttonsContainers.visibility = View.VISIBLE
        return true
      }
    }
    return false
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setVisibilities( R.id.navigation_home )

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    create_save.setOnClickListener { _ : View? ->
      saveToFile(LocalDate.now().toString()+".txt",memoryDataToJson(m_MemoryData))
    }

    create_new.setOnClickListener{ _ : View? ->
      createNewMemoryEntry()
    }

    create_add_date.setOnClickListener { _ : View? ->
      createNewDateSubView( m_DefaultReminderDates.size )
    }

  }

  private fun createNewDateSubView( defaultDatesSize: Int ) {
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


  private fun createCustomPickerEditText(context: Context, text: CharSequence, layoutWeight: Float, f: ( newPickerEditText: EditText ) -> Unit): EditText {
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


  private fun createTimePickerDialog(defaultDate: LocalDateTime, timeEntry: EditText): TimePickerDialog {
    val lis = TimePickerDialog.OnTimeSetListener {
      _ : TimePicker, hourOfDay: Int, minute: Int ->
      timeEntry.setText( LocalTime.of(hourOfDay, minute, 0).toString() )
    }
    return TimePickerDialog(this, lis, defaultDate.hour, defaultDate.minute, true)
  }

  private fun createDatePickerDialog(defaultDate: LocalDateTime, dateEntry: EditText): DatePickerDialog {
    val lis = DatePickerDialog.OnDateSetListener {
      _ : DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
      // REMEMBER : month and day started at zero
      dateEntry.setText( LocalDate.of(year, monthOfYear +1, dayOfMonth).toString() )
    }
    // REMEMBER : month and day started at 1
    return DatePickerDialog(this, lis, defaultDate.year, defaultDate.monthValue - 1, defaultDate.dayOfMonth - 1)
  }

  private fun createNewMemoryEntry() {

    var newMemoryEntry: MemoryEntry = MemoryEntry(m_CurrentMemoryEntryCount
                                                , LocalDateTime.now()
            , findViewById<EditText>(R.id.create_name).text.toString()
            , m_DefaultReminderDates
            , findViewById<EditText>(R.id.create_data).text.toString()
    )
    m_CurrentMemoryEntryCount++
    m_MemoryData.memory_entries.add(newMemoryEntry)
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

  private fun saveToFile(fileName: String, fileContents: String) {
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
}
