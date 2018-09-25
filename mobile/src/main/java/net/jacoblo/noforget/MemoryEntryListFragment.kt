package net.jacoblo.noforget

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.memory_entry_list.*
import java.time.LocalDateTime

class MemoryEntryListFragment: Fragment() {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater!!.inflate( R.layout.memory_entry_list, container, false)
    return view
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)

    // TODO test
    val me = MemoryEntry(10, LocalDateTime.now(),"Item 10", ArrayList<LocalDateTime>(), "Data 10")
    if ( context != null) {
      populateMemoryEntryList(listOf(me), context)
    }
  }

  fun populateMemoryEntryList(upcomingMemoryEntries: List<MemoryEntry>, context: Context) {

    val titleViewManager = LinearLayoutManager(context)
    val titleViewAdapter = MemoryTitleAdapter(upcomingMemoryEntries)

    memory_entry_list_layout.apply {
      setHasFixedSize(true)
      layoutManager = titleViewManager
      itemAnimator = DefaultItemAnimator()
      adapter = titleViewAdapter
      addItemDecoration( DividerItemDecoration( activity.applicationContext, LinearLayoutManager.VERTICAL ))
      // REMEMBER : instantiate anonymous class using key word object
      addOnItemTouchListener(RecyclerTouchListener(activity.applicationContext, this, object : RecyclerTouchListener.ClickListener {
        override fun onClick(view: View, position: Int) {
          if (position < upcomingMemoryEntries.size) {
            val titleNow = upcomingMemoryEntries[position].entry_name
            Toast.makeText(activity.applicationContext,  "$titleNow is selected!", Toast.LENGTH_SHORT).show()
          }
        }

        override fun onLongClick(view: View?, position: Int) {}
      }))
    }
  }

}