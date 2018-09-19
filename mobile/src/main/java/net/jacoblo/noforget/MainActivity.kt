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

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        create_group.visibility = View.INVISIBLE
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_create -> {
        create_group.visibility = View.VISIBLE
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_notifications -> {
        create_group.visibility = View.INVISIBLE
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    create_group.visibility = View.INVISIBLE
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    val linearLayout = findViewById<ConstraintLayout>(R.id.container)

    val saveButton: Button = findViewById(R.id.create_save)
    saveButton.setOnClickListener { _ : View? ->
      saveToFile(LocalDate.now().toString()+".txt",memoryDataToJson(m_MemoryData))
    }

    val createButton: Button = findViewById(R.id.create_new)
    createButton.setOnClickListener{ _ : View? ->
      createNewMemoryEntry()
    }

    val addDateButton = findViewById<Button>(R.id.create_add_date)
    create_add_date.setOnClickListener { v : View? ->

      val newDateEditText = EditText(this)
      newDateEditText.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
      newDateEditText.focusable = View.NOT_FOCUSABLE
      newDateEditText.setEms(10)
      newDateEditText.setEnabled(true)
      newDateEditText.setOnClickListener{ _ : View? ->
        val dpd = createDatePickerDialog(LocalDateTime.now().plusDays(1), newDateEditText)
        dpd.show()
      }

      linearLayout?.addView(newDateEditText)
      linearLayout?.requestLayout()
    }
    // TEMP
    val dateEditText = findViewById<EditText>(R.id.create_date)
    dateEditText.setOnClickListener{ _ : View? ->
      val dpd = createDatePickerDialog(LocalDateTime.now().plusDays(1), dateEditText)
      dpd.show()
    }
    val timeEditText = findViewById<EditText>(R.id.create_time)
    timeEditText.setOnClickListener{ _ : View? ->
      val dpd = createTimePickerDialog(LocalDateTime.now(), timeEditText)
      dpd.show()
    }


  }

  private fun createTimePickerDialog(defaultDate: LocalDateTime, timeEntry: EditText): TimePickerDialog {
    val lis = TimePickerDialog.OnTimeSetListener {
      view: TimePicker, hourOfDay: Int, minute: Int ->
      timeEntry.setText( LocalTime.of(hourOfDay, minute, 0).toString() )
    }
    return TimePickerDialog(this, lis, defaultDate.hour, defaultDate.minute, true)
  }

  private fun createDatePickerDialog(defaultDate: LocalDateTime, dateEntry: EditText): DatePickerDialog {
    val lis = DatePickerDialog.OnDateSetListener {
      view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
      // REMEMBER : month and day started at zero
      dateEntry.setText( LocalDate.of(year, monthOfYear +1, dayOfMonth).toString() )
    }
    // REMEMBER : month and day started at 1
    return DatePickerDialog(this, lis, defaultDate.year, defaultDate.monthValue - 1, defaultDate.dayOfMonth - 1)
  }

  private fun createNewMemoryEntry() {
    val defaultReminderDates = ArrayList<LocalDateTime>()
    defaultReminderDates.add(LocalDateTime.now().plusDays(1))
    defaultReminderDates.add(LocalDateTime.now().plusDays(3))
    defaultReminderDates.add(LocalDateTime.now().plusDays(7))
    defaultReminderDates.add(LocalDateTime.now().plusDays(30))
    defaultReminderDates.add(LocalDateTime.now().plusDays(90))
    defaultReminderDates.add(LocalDateTime.now().plusDays(365))

    var newMemoryEntry: MemoryEntry = MemoryEntry(m_CurrentMemoryEntryCount
                                                , LocalDateTime.now()
            , findViewById<EditText>(R.id.create_name).text.toString()
            , defaultReminderDates
            , findViewById<EditText>(R.id.create_data).text.toString()
    )
    m_CurrentMemoryEntryCount++
    m_MemoryData.memory_entries.add(newMemoryEntry)
  }


  private fun saveToFile(fileName: String, fileContents: String) {
    val saveFileDir = File(Environment.getExternalStorageDirectory(), "NoForget")
    if (!saveFileDir.exists() && !saveFileDir?.mkdir()) {
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
