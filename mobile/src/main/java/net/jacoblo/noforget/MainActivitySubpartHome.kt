package net.jacoblo.noforget

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

internal fun MainActivity.onCreateHomePart() {
  populateMemoryEntryList()
}

internal fun MainActivity.populateMemoryEntryList() {
  val getUpcomingMemoryEntries = calcUpcomingReminders(m_MemoryData)
  val testlist = ArrayList<String>()
  for ( me in getUpcomingMemoryEntries) {
    testlist.add(me.entry_name)
  }
  val testArray = arrayOfNulls<String>(testlist.size)
  testlist.toArray(testArray)

  m_TitleViewManager = LinearLayoutManager(this)
  m_TitleViewAdapter = MemoryTitleAdapter(testArray)

  memory_titles.apply {
    setHasFixedSize(true)
    layoutManager = m_TitleViewManager
    itemAnimator = DefaultItemAnimator()
    adapter = m_TitleViewAdapter
    addItemDecoration( DividerItemDecoration( applicationContext, LinearLayoutManager.VERTICAL ))
    addOnItemTouchListener(RecyclerTouchListener(applicationContext, this, object : RecyclerTouchListener.ClickListener {
      override fun onClick(view: View, position: Int) {
        val title = testlist.get(position)
        Toast.makeText(applicationContext,  "$title is selected!", Toast.LENGTH_SHORT).show()
      }

      override fun onLongClick(view: View?, position: Int) {}
    }))
  }
}

class MemoryTitleAdapter(private val memoryTitleList: Array<String?>) : RecyclerView.Adapter<MemoryTitleAdapter.MyViewHolder>() {

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView = view.findViewById<View>(R.id.memory_list_title) as TextView
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memory_list_row, parent, false)

    return MyViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val currentTitle = if ( memoryTitleList[position] != null) memoryTitleList[position] else ""
    holder.title.text = currentTitle
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