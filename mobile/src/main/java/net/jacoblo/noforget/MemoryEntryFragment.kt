package net.jacoblo.noforget

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class MemoryEntryFragment: Fragment() {

  private var listener: OnItemSelectedListener? = null

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater!!.inflate(R.layout.memory_entry_fragment, container, false)

    val memoryEntryName: EditText = view.findViewById( R.id.memory_entry_fragment_name )
    memoryEntryName.setText("HAHA Frag")

    return view
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    if ( context is OnItemSelectedListener) {
      listener = context as OnItemSelectedListener
    }
    else {
      Log.e(LOG_TAG, "Error casting OnItemSelectedListener to MemoryENtryFragment")
    }
  }

  override fun onDetach() {
    super.onDetach()
    listener = null
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    arguments?.setText()
  }

  public interface OnItemSelectedListener {
    public fun doSomething()
  }
}