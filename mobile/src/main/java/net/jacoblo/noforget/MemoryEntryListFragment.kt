package net.jacoblo.noforget

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.memory_entry_list.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

class MemoryEntryListFragment: Fragment() {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater!!.inflate( R.layout.memory_entry_list, container, false)
    return view
  }

  fun populateMemoryEntryList(upcomingMemoryEntries: List<MemoryEntry>, context: Context) {

    val titleViewManager = LinearLayoutManager(context)
    val titleViewAdapter = MemoryTitleAdapter(upcomingMemoryEntries)

    memory_entry_list_fragment.apply {
      setHasFixedSize(true)
      layoutManager = titleViewManager
      itemAnimator = DefaultItemAnimator()
      adapter = titleViewAdapter
      addItemDecoration( DividerItemDecoration( context, LinearLayoutManager.VERTICAL ))
      visibility = View.VISIBLE

      // REMEMBER : instantiate anonymous class using key word object
      addOnItemTouchListener(RecyclerTouchListener(context, this, object : RecyclerTouchListener.ClickListener {
        override fun onClick(view: View, position: Int) {
          if (position < upcomingMemoryEntries.size) {
            val curMemoryEntryFragmentBundle = Bundle()
            curMemoryEntryFragmentBundle.putString("MemoryEntryJson", memoryEntryToJson( upcomingMemoryEntries[position] ))

            val curMemoryEntryFragment = MemoryEntryFragment()
            curMemoryEntryFragment.arguments = curMemoryEntryFragmentBundle
            fragmentManager.beginTransaction()
                    .replace(R.id.memory_entry_list_fragment_Item, curMemoryEntryFragment,"CurMemoryEntryFragment")
                    .commit()

            visibility = View.INVISIBLE
            memory_entry_list_fragment_Item.visibility = View.VISIBLE

            val titleNow = upcomingMemoryEntries[position].entry_name
            Toast.makeText(context,  "$titleNow is selected!", Toast.LENGTH_SHORT).show()
          }
        }

        override fun onLongClick(view: View?, position: Int) {}
      }))
    }
  }

}

class MemoryTitleAdapter(private val memoryTitleList: List<MemoryEntry>) : RecyclerView.Adapter<MemoryTitleAdapter.MyViewHolder>() {

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var entryTitle: TextView = view.findViewById<View>(R.id.memory_list_title) as TextView
    var entryData: TextView = view.findViewById<View>(R.id.memory_list_data) as TextView
    var entryReminderLeft: TextView = view.findViewById<View>(R.id.memory_list_reminder_left) as TextView

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memory_list_row, parent, false)

    return MyViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val currentEntry = memoryTitleList[position]
    holder.entryTitle.text = currentEntry.entry_name
    holder.entryData.text = currentEntry.entry_data

    val zoneId = ZoneId.systemDefault()
    var remindingTimeLeftThisEntry: LocalDateTime = currentEntry.reminder_dates.first {
      it -> it.atZone(zoneId).toEpochSecond() > LocalDateTime.now().atZone(zoneId).toEpochSecond()
    }
    val reminderString = "" + calcDuration( LocalDateTime.now(), remindingTimeLeftThisEntry).toHours().toString() + " hours left"
    holder.entryReminderLeft.text = reminderString
  }

  override fun getItemCount(): Int {
    return memoryTitleList.size
  }
}

class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?)
  : RecyclerView.OnItemTouchListener {

  private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapUp(e: MotionEvent): Boolean {
      return true
    }

    override fun onLongPress(e: MotionEvent) {
      val child = recyclerView.findChildViewUnder(e.x, e.y)
      if (child != null && clickListener != null) {
        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
      }
    }
  })

  override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

    val child = rv.findChildViewUnder(e.x, e.y)
    if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
      clickListener.onClick(child, rv.getChildAdapterPosition(child))
    }
    return false
  }

  override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

  override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

  interface ClickListener {
    fun onClick(view: View, position: Int)
    fun onLongClick(view: View?, position: Int)
  }
}

fun calcDuration(ldt1: LocalDateTime, ldt2: LocalDateTime): Duration {
  return Duration.between( ldt1, ldt2 )
}
