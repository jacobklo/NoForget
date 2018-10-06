package net.jacoblo.noforget

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime


val LOG_TAG = "NoForget Log"
val m_MemoryData = MemoryData(0, ArrayList<MemoryEntry>() )

class MainActivity : AppCompatActivity() {

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    setVisibilities(item.itemId)
  }

  private fun setVisibilities(whichNavigationItemSelectedId: Int): Boolean {
    when (whichNavigationItemSelectedId) {
      R.id.navigation_home
    , R.id.navigation_notifications -> {
        memory_entry_item_placeholder.visibility = View.INVISIBLE
        memory_entry_list_placeholder.visibility = View.VISIBLE
        return true
      }
      R.id.navigation_create -> {
        memory_entry_item_placeholder.visibility = View.VISIBLE
        memory_entry_list_placeholder.visibility = View.INVISIBLE
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

    // Home page
    fragmentManager.beginTransaction()
            .add( R.id.memory_entry_list_placeholder, MemoryEntryListFragment(), "MemoryEntryListFragment")
            .commit()

    // Create new Memory Entry Item page
    val newMemoryEntryFragment = MemoryEntryFragment()
    fragmentManager.beginTransaction()
            .add( R.id.memory_entry_item_placeholder, newMemoryEntryFragment, "MemoryEntryFragment")
            .commit()
  }

  fun updateMemoryEntryData ( updatedMemoryEntry: MemoryEntry ) {
    var melf: MemoryEntryListFragment = fragmentManager.findFragmentByTag("MemoryEntryListFragment") as MemoryEntryListFragment
    melf.populateMemoryEntryList( )

    saveToFile( "NoForget.txt", memoryDataToJson( m_MemoryData ))
  }
}

private fun saveToFile(fileName: String, fileContents: String) {
  val saveFileDir = File(Environment.getExternalStorageDirectory(), "NoForget")
  if (!saveFileDir.exists() && !saveFileDir.mkdir()) {
    Log.e(LOG_TAG, "cannot create save file for NoForget")
  }

  val saveFilePath = saveFileDir.absolutePath + "/" + fileName
  val fos = FileOutputStream(saveFilePath)
  val osw = OutputStreamWriter(fos)
  osw.write(fileContents)
  osw.close()

}