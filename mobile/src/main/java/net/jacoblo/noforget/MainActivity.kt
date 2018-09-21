package net.jacoblo.noforget

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

  private fun convertDatesViewGroupToDatesArray( dateTimeGroup: ViewGroup): ArrayList<LocalDateTime>  {

    val result = ArrayList<LocalDateTime>()

    for ( i in 0..dateTimeGroup.childCount ) {

      // check if actually LinearLayout
      if ( dateTimeGroup.getChildAt(i) !is LinearLayout) {
        return result; //Empty as not dateTimeGroup
      }

      val child = dateTimeGroup.getChildAt(i) as LinearLayout;
      val dateEditText = child.getChildAt(0) as EditText;
      val timeEditText = child.getChildAt(1) as EditText;

      Toast.makeText(this,dateEditText.text.toString() + " " + timeEditText.text.toString(), Toast.LENGTH_LONG).show()
      val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")
      try {
        val childLocalDateTime = LocalDateTime.parse(dateEditText.text.toString() + " " + timeEditText.text.toString(), dateTimeFormatter)
        result.add(childLocalDateTime)
      }catch ( dtpe: DateTimeParseException) {}
    }
    return result
  }

  private fun createNewMemoryEntry() {

    var newMemoryEntry: MemoryEntry = MemoryEntry(m_CurrentMemoryEntryCount
                                                , LocalDateTime.now()
            , findViewById<EditText>(R.id.create_name).text.toString()
            , convertDatesViewGroupToDatesArray(datesContainer)
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
