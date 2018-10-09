package net.jacoblo.noforget

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


const val LOG_TAG = "NoForget Log"
var m_MemoryData = MemoryData(0, ArrayList<MemoryEntry>())
var m_filePath: Uri? = null

class MainActivity : AppCompatActivity() {

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    setVisibilities(item.itemId)
  }

  private fun setVisibilities(whichNavigationItemSelectedId: Int): Boolean {
    when (whichNavigationItemSelectedId) {
      R.id.navigation_home -> {
        val melf = fragmentManager.findFragmentByTag("MemoryEntryListFragment")
        if (melf != null) {
          (melf as MemoryEntryListFragment).onListPage()
        }

        memory_entry_item_placeholder.visibility = View.INVISIBLE
        memory_entry_list_placeholder.visibility = View.VISIBLE
        memory_save_to_file_placeholder.visibility = View.INVISIBLE
        return true
      }
      R.id.navigation_create -> {
        memory_entry_item_placeholder.visibility = View.VISIBLE
        memory_entry_list_placeholder.visibility = View.INVISIBLE
        memory_save_to_file_placeholder.visibility = View.INVISIBLE
        return true
      }
      R.id.navigation_save_to_file -> {
        memory_entry_item_placeholder.visibility = View.INVISIBLE
        memory_entry_list_placeholder.visibility = View.INVISIBLE
        memory_save_to_file_placeholder.visibility = View.VISIBLE
        return true
      }
    }
    return false
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setVisibilities(R.id.navigation_home)

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    // Home page
    fragmentManager.beginTransaction()
            .add(R.id.memory_entry_list_placeholder, MemoryEntryListFragment(), "MemoryEntryListFragment")
            .commit()

    // Create new Memory Entry Item page
    val newMemoryEntryFragment = MemoryEntryFragment()
    fragmentManager.beginTransaction()
            .add(R.id.memory_entry_item_placeholder, newMemoryEntryFragment, "MemoryEntryFragmentCreate")
            .commit()

    memory_save_to_file_placeholder
    // Create new save to file page
    val newMemorySaveToFileFragment = MemorySaveToFileFragment()
    fragmentManager.beginTransaction()
            .add(R.id.memory_save_to_file_placeholder, newMemorySaveToFileFragment, "newMemorySaveToFileFragment")
            .commit()
  }
}
fun saveToFileOpration(context: Context) {
  if (writeTextToFileFromUri(m_filePath, context, memoryDataToJson(m_MemoryData))) {
    Toast.makeText(context, "Memory File Saved", Toast.LENGTH_LONG).show()
  } else {
    Toast.makeText(context, "Error saving memory file", Toast.LENGTH_LONG).show()
  }
}

// REMEMBER : How android is reading text from uri
fun readTextFromUri(uri: Uri?, context: Context): String {
  if (uri == null) return ""

  val stringBuilder = StringBuilder()
  try {

    context.contentResolver.openInputStream(uri).use { inputStream ->
      BufferedReader(InputStreamReader(inputStream)).use { reader ->
        var line: String? = reader.readLine()
        while (line != null) {
          stringBuilder.append(line)
          line = reader.readLine()
        }
      }
    }
  } catch (ioe: IOException) {
    ioe.printStackTrace()
    stringBuilder.append(ioe.message)
  }

  return stringBuilder.toString()
}

fun writeTextToFileFromUri(uri: Uri?, context: Context, text: String): Boolean {
  if (uri == null) return false

  try {
    context.contentResolver.openOutputStream(uri, "w").use { outputStream ->
      outputStream.write(text.toByteArray())
    }
  } catch (fnfe: FileNotFoundException) {
    fnfe.printStackTrace()
    return false
  } catch (ioe: IOException) {
    ioe.printStackTrace()
    return false
  }
  return true
}

