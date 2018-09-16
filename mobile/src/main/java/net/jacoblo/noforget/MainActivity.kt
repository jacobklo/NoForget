package net.jacoblo.noforget

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

  val LOG_TAG = "NoForget Log"

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        message.setText(R.string.title_home)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_dashboard -> {
        message.setText(R.string.title_dashboard)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_notifications -> {
        message.setText(R.string.title_notifications)
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    val saveButton: Button = findViewById(R.id.save)
    saveButton.setOnClickListener { _: View? ->
      saveToFile(LocalDate.now().toString()+".txt","b")
    }
  }

  fun saveToFile(fileName: String, fileContents: String) {
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
