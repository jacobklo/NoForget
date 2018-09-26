package net.jacoblo.noforget

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
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

internal fun MainActivity.onCreateCreatePart() {
//  create_save.setOnClickListener { _ : View? ->
//    saveToFile("NoForget.txt",memoryDataToJson(m_MemoryData))
//  }
//
//  create_new.setOnClickListener{ _ : View? ->
//    createNewMemoryEntry()
//  }
//
//  create_delete.setOnClickListener{ _ : View? ->
//    clearUnsavedEntry()
//  }
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

//  var newMemoryEntry = MemoryEntry(m_CurrentMemoryEntryCount
//          , LocalDateTime.now()
//          , findViewById<EditText>(R.id.create_name).text.toString()
//          , convertDatesViewGroupToDatesArray(datesContainer)
//          , findViewById<EditText>(R.id.create_data).text.toString()
//  )
//  m_CurrentMemoryEntryCount++
//  m_MemoryData.memory_entries.add(newMemoryEntry)

  // clear
  clearUnsavedEntry()


  var melf: MemoryEntryListFragment = fragmentManager.findFragmentByTag("MemoryEntryListFragment") as MemoryEntryListFragment
  melf.populateMemoryEntryList( calcUpcomingReminders(m_MemoryData), this )
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


internal fun MainActivity.clearUnsavedEntry() {
//  create_name.text = null
//  create_data.text = null
//  datesContainer.removeAllViews()
}