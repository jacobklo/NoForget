package net.jacoblo.noforget

import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

private const val READ_REQUEST_CODE: Int = 42
private const val WRITE_REQUEST_CODE: Int = 43

class MemorySaveToFileFragment : Fragment() {
  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater!!.inflate(R.layout.memory_save_to_file, container, false)

    val openFrom = view.findViewById<Button>(R.id.open_from_file_butt)
    openFrom.setOnClickListener {
      val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "text/plain"
        addCategory(Intent.CATEGORY_OPENABLE)
      }
      startActivityForResult(intent, READ_REQUEST_CODE)

    }

    val saveTo = view.findViewById<Button>(R.id.save_to_file_butt)
    saveTo.setOnClickListener {

      val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "NoForget.txt")
      }
      startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    return view
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    val uri = data?.data

    if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {

      val memoriesString = readTextFromUri(uri, context)
      val tempMemoryData = readJsonToMemoryData(memoriesString)
      if (tempMemoryData == null) {
        Toast.makeText(context, "Error reading Input file", Toast.LENGTH_LONG).show()
      } else {
        m_MemoryData = tempMemoryData
      }
    } else if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {

      if (writeTextToFileFromUri(uri, context, memoryDataToJson(m_MemoryData))) {
        Toast.makeText(context, "Memory File Saved", Toast.LENGTH_LONG).show()
      } else {
        Toast.makeText(context, "Error saving memory file", Toast.LENGTH_LONG).show()
      }
    }
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
