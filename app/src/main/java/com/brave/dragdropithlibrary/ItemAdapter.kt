package com.brave.dragdropithlibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import com.woxthebox.draglistview.DragItemAdapter

class ItemAdapter(
    var list: ArrayList<Pair<Long, String>> = arrayListOf(),
    var mLayoutId: Int = 0,
    var mGrabHandleId: Int = 0,
    var mDragOnLongPress: Boolean = false
) : DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder>() {

    init {
        itemList = list
    }

    inner class ViewHolder(itemView: View) :
        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView

        init {
            mText = itemView.findViewById<View>(R.id.text) as TextView
        }

        override fun onItemClicked(view: View) {
            Toast.makeText(view.context, "Item clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onItemLongClicked(view: View): Boolean {
            Toast.makeText(view.context, "Item long clicked", Toast.LENGTH_SHORT).show()
            return true
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        val text = mItemList[position].second
        holder.mText.text = text
        holder.itemView.tag = mItemList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        )

    }

    override fun getUniqueItemId(position: Int): Long = mItemList[position].first
}
