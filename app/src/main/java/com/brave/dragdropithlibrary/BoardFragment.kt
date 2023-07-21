package com.brave.dragdropithlibrary

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brave.dragdropithlibrary.databinding.BoardLayoutBinding
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.ColumnProperties
import com.woxthebox.draglistview.DragItem

class BoardFragment : Fragment() {
    private lateinit var binding: BoardLayoutBinding
    private var sCreatedItems = 0
    private var mColumns = 0
    private var mGridLayout = false

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BoardLayoutBinding.inflate(layoutInflater, container, false)
        binding.boardView.setSnapToColumnsWhenScrolling(true)
        binding.boardView.setSnapToColumnWhenDragging(true)
        binding.boardView.setSnapDragItemToTouch(true)
        binding.boardView.setSnapToColumnInLandscape(false)
        binding.boardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        binding.boardView.setBoardListener(object : BoardView.BoardListener {
            override fun onItemDragStarted(column: Int, row: Int) {

            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
            }

            override fun onItemChangedPosition(
                oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int
            ) {

            }

            @SuppressLint("SetTextI18n")
            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
                val itemCount1: TextView =
                    binding.boardView.getHeaderView(oldColumn).findViewById(R.id.item_count)
                itemCount1.text = "(" + binding.boardView.getAdapter(oldColumn).itemCount + ")"
                val itemCount2: TextView =
                    binding.boardView.getHeaderView(newColumn).findViewById(R.id.item_count)
                itemCount2.text = "(" + binding.boardView.getAdapter(newColumn).itemCount + ")"

            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {

            }

            override fun onColumnDragStarted(position: Int) {

            }

            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {

            }

            override fun onColumnDragEnded(fromPosition: Int, toPosition: Int) {

            }

        })

        binding.boardView.setBoardCallback(object : BoardView.BoardCallback {
            override fun canDragItemAtPosition(column: Int, row: Int): Boolean {
                // Add logic here to prevent an item to be dragged
                return true
            }

            override fun canDropItemAtPosition(
                oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int
            ): Boolean {
                // Add logic here to prevent an item to be dropped
                return true
            }

            override fun canDragColumnAtPosition(index: Int): Boolean {
                // Add logic here to prevent a column to be dragged
                return true
            }

            override fun canDropColumnAtPosition(oldIndex: Int, newIndex: Int): Boolean {
                // Add logic here to prevent a column to be dropped
                return true
            }

        })
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycleScope?.launchWhenCreated {
            viewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Board"
        resetBoard()
    }

    private fun resetBoard() {
        binding.boardView.clearBoard()
        binding.boardView.setCustomDragItem(
            if (mGridLayout) null else MyDragItem(
                activity, R.layout.column_item
            )
        )
        binding.boardView.setCustomColumnDragItem(
            if (mGridLayout) null else MyColumnDragItem(
                activity, R.layout.column_drag_layout
            )
        )
        addColumn()
        addColumn()
        addColumn()
        addColumn()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_disable_drag).isVisible = binding.boardView.isDragEnabled
        menu.findItem(R.id.action_enable_drag).isVisible = !binding.boardView.isDragEnabled
        // menu.findItem(R.id.action_grid).setVisible(!mGridLayout);
        menu.findItem(R.id.action_list).isVisible = mGridLayout
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_disable_drag -> {
                binding.boardView.isDragEnabled = false
                requireActivity().invalidateOptionsMenu()
                return true
            }

            R.id.action_enable_drag -> {
                binding.boardView.isDragEnabled = true
                requireActivity().invalidateOptionsMenu()
                return true
            }

            R.id.action_list -> {
                mGridLayout = false
                resetBoard()
                requireActivity().invalidateOptionsMenu()
                return true
            }

            R.id.action_add_column -> {
                addColumn()
                return true
            }

            R.id.action_remove_column -> {
                binding.boardView.removeColumn(0)
                return true
            }

            R.id.action_clear_board -> {
                binding.boardView.clearBoard()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun addColumn() {
        val mItemArray = ArrayList<Pair<Long, String>>()
        val addItems = 4
        for (i in 0 until addItems) {
            val id = (sCreatedItems++).toLong()
            mItemArray.add(Pair(id, "Item $id"))
        }
        val listAdapter = ItemAdapter(
            mItemArray,
            if (mGridLayout) R.layout.grid_item else R.layout.column_item,
            R.id.item_layout,
            true
        )
        val header = View.inflate(activity, R.layout.column_header, null)
        (header.findViewById<View>(R.id.text) as TextView).text = "Column " + (mColumns + 1)
        (header.findViewById<View>(R.id.item_count) as TextView).text = "($addItems)"
        header.setOnClickListener { v ->
            val id = sCreatedItems++.toLong()
            val item = Pair(
                id, "Test $id"
            )
            binding.boardView.addItem(binding.boardView.getColumnOfHeader(v), 0, item, true)
            //mBoardView.moveItem(4, 0, 0, true);
            //mBoardView.removeItem(column, 0);
            //mBoardView.moveItem(0, 0, 1, 3, false);
            //mBoardView.replaceItem(0, 0, item1, true);
            (header.findViewById<View>(R.id.item_count) as TextView).text =
                "(" + mItemArray.size + ")"
        }

//        final View footer = View.inflate(getActivity(), R.layout.column_header, null);
//        ((TextView) footer.findViewById(R.id.text)).setText("Column " + (mColumns + 1) + " footer");
//        ((TextView) footer.findViewById(R.id.item_count)).setText("3");
        val layoutManager = if (mGridLayout) GridLayoutManager(context, 4) else LinearLayoutManager(
            context
        )
        val columnProperties =
            ColumnProperties.Builder.newBuilder(listAdapter).setLayoutManager(layoutManager)
                .setHasFixedItemSize(false).setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header) //  .setFooter(footer)
                .setColumnDragView(header).build()
        binding.boardView.addColumn(columnProperties)
        mColumns++
    }

    private class MyColumnDragItem(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        init {
            setSnapToTouch(false)
        }

        override fun onBindDragView(clickedView: View, dragView: View) {
            val clickedLayout = clickedView as LinearLayout
            val clickedHeader = clickedLayout.getChildAt(0)
            val clickedFooter = clickedLayout.getChildAt(2)
            val clickedRecyclerView = clickedLayout.getChildAt(1) as RecyclerView
            val dragHeader = dragView.findViewById<View>(R.id.drag_header)
            //    View dragFooter = dragView.findViewById(R.id.drag_footer);
            val dragScrollView = dragView.findViewById<ScrollView>(R.id.drag_scroll_view)
            val dragLayout = dragView.findViewById<LinearLayout>(R.id.drag_list)
            val clickedColumnBackground = clickedLayout.background
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground)
            }
            val clickedRecyclerBackground = clickedRecyclerView.background
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground)
            }
            dragLayout.removeAllViews()
            (dragHeader.findViewById<View>(R.id.text) as TextView).text =
                (clickedHeader.findViewById<View>(R.id.text) as TextView).text
            (dragHeader.findViewById<View>(R.id.item_count) as TextView).text =
                (clickedHeader.findViewById<View>(R.id.item_count) as TextView).text
            //            ((TextView) dragFooter.findViewById(R.id.text)).setText(((TextView) clickedFooter.findViewById(R.id.text)).getText());
//            ((TextView) dragFooter.findViewById(R.id.item_count)).setText(((TextView) clickedFooter.findViewById(R.id.item_count)).getText());
            for (i in 0 until clickedRecyclerView.childCount) {
                val view = View.inflate(dragView.context, R.layout.column_item, null)
                (view.findViewById<View>(R.id.text) as TextView).text =
                    (clickedRecyclerView.getChildAt(i)
                        .findViewById<View>(R.id.text) as TextView).text
                dragLayout.addView(view)
                if (i == 0) {
                    dragScrollView.scrollY = -clickedRecyclerView.getChildAt(i).top
                }
            }
            dragView.pivotY = 0f
            dragView.pivotX = (clickedView.getMeasuredWidth() / 2).toFloat()
        }

        override fun onStartDragAnimation(dragView: View) {
            super.onStartDragAnimation(dragView)
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start()
        }

        override fun onEndDragAnimation(dragView: View) {
            super.onEndDragAnimation(dragView)
            dragView.animate().scaleX(1f).scaleY(1f).start()
        }
    }

    private class MyDragItem(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            (dragView.findViewById<View>(R.id.text) as TextView).text = text
            val dragCard = dragView.findViewById<CardView>(R.id.card)
            val clickedCard = clickedView.findViewById<CardView>(R.id.card)
            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.foreground =
                clickedView.resources.getDrawable(R.drawable.card_view_drag_foreground, null)
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {
            val dragCard = dragView.findViewById<CardView>(R.id.card)
            val clickedCard = clickedView.findViewById<CardView>(R.id.card)
            val widthDiff =
                dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight - clickedCard.paddingRight
            val heightDiff =
                dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom - clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard = dragView.findViewById<CardView>(R.id.card)
            val anim =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard = dragView.findViewById<CardView>(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }
    }
}
