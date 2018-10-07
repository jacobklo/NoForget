package net.jacoblo.noforget

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


const val LOG_TAG = "NoForget Log"
var m_MemoryData = MemoryData(0, ArrayList<MemoryEntry>() )

class MainActivity : AppCompatActivity() {

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    setVisibilities(item.itemId)
  }

  private fun setVisibilities(whichNavigationItemSelectedId: Int): Boolean {
    when (whichNavigationItemSelectedId) {
      R.id.navigation_home -> {
        val melf = fragmentManager.findFragmentByTag("MemoryEntryListFragment")
        if ( melf != null ) {
          ( melf as MemoryEntryListFragment).onListPage()
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
    setVisibilities( R.id.navigation_home )

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    // Home page
    fragmentManager.beginTransaction()
            .add( R.id.memory_entry_list_placeholder, MemoryEntryListFragment(), "MemoryEntryListFragment")
            .commit()

    // Create new Memory Entry Item page
    val newMemoryEntryFragment = MemoryEntryFragment()
    fragmentManager.beginTransaction()
            .add( R.id.memory_entry_item_placeholder, newMemoryEntryFragment, "MemoryEntryFragmentCreate")
            .commit()

    memory_save_to_file_placeholder
    // Create new save to file page
    val newMemorySaveToFileFragment = MemorySaveToFileFragment()
    fragmentManager.beginTransaction()
            .add( R.id.memory_save_to_file_placeholder, newMemorySaveToFileFragment, "newMemorySaveToFileFragment")
            .commit()
  }
}
