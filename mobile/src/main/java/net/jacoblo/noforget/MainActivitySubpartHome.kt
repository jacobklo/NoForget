package net.jacoblo.noforget

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId

internal fun MainActivity.onCreateHomePart() {

}

internal fun MainActivity.populateMemoryEntryList(upcomingMemoryEntries: List<MemoryEntry>) {

  val titleViewManager = LinearLayoutManager(this)
  val titleViewAdapter = MemoryTitleAdapter(upcomingMemoryEntries)

  memory_titles.apply {
    setHasFixedSize(true)
    layoutManager = titleViewManager
    itemAnimator = DefaultItemAnimator()
    adapter = titleViewAdapter
    addItemDecoration( DividerItemDecoration( applicationContext, LinearLayoutManager.VERTICAL ))
    // REMEMBER : instantiate anonymous class using key word object
    addOnItemTouchListener(RecyclerTouchListener(applicationContext, this, object : RecyclerTouchListener.ClickListener {
      override fun onClick(view: View, position: Int) {
        if (position < upcomingMemoryEntries.size) {
          val titleNow = upcomingMemoryEntries[position].entry_name
          Toast.makeText(applicationContext,  "$titleNow is selected!", Toast.LENGTH_SHORT).show()
        }
      }

      override fun onLongClick(view: View?, position: Int) {}
    }))
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
