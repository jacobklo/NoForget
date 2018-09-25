package net.jacoblo.noforget

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

val LOG_TAG = "NoForget Log"

class MainActivity : AppCompatActivity() {

  var m_CurrentMemoryEntryCount = 0;
  val m_MemoryData = MemoryData(0, ArrayList<MemoryEntry>() )

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
        memory_titles.visibility = View.VISIBLE
        return true
      }
      R.id.navigation_create -> {
        create_name.visibility = View.VISIBLE
        create_data.visibility = View.VISIBLE
        datesContainer.visibility = View.VISIBLE
        buttonsContainers.visibility = View.VISIBLE
        memory_titles.visibility = View.INVISIBLE
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

    onCreateCreatePart()

    onCreateHomePart()
  }

}
