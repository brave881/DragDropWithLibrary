package com.brave.dragdropithlibrary

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MySwipeRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {
    private var mScrollingView: View? = null

    override fun canChildScrollUp(): Boolean {
        return mScrollingView != null && canScrollVertically(-1)
    }

    fun setScrollingView(scrollingView: View?) {
        mScrollingView = scrollingView
    }
}
