package net.jacoblo.noforget

import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

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

    m_filePath = data?.data

    if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {

      val memoriesString = readTextFromUri(m_filePath, context)
      val tempMemoryData = readJsonToMemoryData(memoriesString)
      if (tempMemoryData == null) {
        Toast.makeText(context, "Error reading Input file", Toast.LENGTH_LONG).show()
      } else {
        m_MemoryData = tempMemoryData
      }
    } else if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {

      saveToFileOpration(context)
    }
  }
}
