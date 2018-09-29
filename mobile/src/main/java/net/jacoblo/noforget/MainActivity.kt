package net.jacoblo.noforget

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime


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

    // Add new Memory Entry Object for Create page
    val newMemoryEntry = MemoryEntry(m_MemoryData.memory_entries.size, LocalDateTime.now(),"New Entry Name", ArrayList<LocalDateTime>(), "Reminder1")
    m_MemoryData.memory_entries.add( newMemoryEntry )

    // REMEMBER : Correct way to pass data to fragment
    val memoryEntryFragmentBundle = Bundle()
    memoryEntryFragmentBundle.putString( "MemoryEntryJson", memoryEntryToJson( newMemoryEntry ))

    // Create new Memory Entry Item page
    val newMemoryEntryFragment = MemoryEntryFragment()
    newMemoryEntryFragment.arguments = memoryEntryFragmentBundle
    fragmentManager.beginTransaction()
            .add( R.id.memory_entry_item_placeholder, newMemoryEntryFragment, "MemoryEntryFragment")
            .commit()
  }

  fun updateMemoryEntryData ( updatedMemoryEntry: MemoryEntry ) {
    if ( updatedMemoryEntry.memory_entry_id > -1 && updatedMemoryEntry.memory_entry_id < m_MemoryData.memory_entries.size ) {
      m_MemoryData.memory_entries[ updatedMemoryEntry.memory_entry_id ] = updatedMemoryEntry
    }
    else {
      updatedMemoryEntry.memory_entry_id = m_MemoryData.memory_entries.size;
      m_MemoryData.memory_entries.add ( updatedMemoryEntry )
    }

    var melf: MemoryEntryListFragment = fragmentManager.findFragmentByTag("MemoryEntryListFragment") as MemoryEntryListFragment
    melf.populateMemoryEntryList( calcUpcomingReminders(m_MemoryData), this )
  }
}
