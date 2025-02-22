package com.leos.dialer.dialogs

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.leos.commons.adapters.SimpleListItemAdapter
import com.leos.commons.fragments.BaseBottomSheetDialogFragment
import com.leos.commons.models.SimpleListItem
import com.leos.dialer.R
import kotlinx.android.synthetic.main.layout_simple_recycler_view.*

// same as BottomSheetChooserDialog but with dynamic updates
class DynamicBottomSheetChooserDialog : BaseBottomSheetDialogFragment() {

    var onItemClick: ((SimpleListItem) -> Unit)? = null

    override fun setupContentView(parent: ViewGroup) {
        val child = layoutInflater.inflate(R.layout.layout_simple_recycler_view, parent, false)
        parent.addView(child)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        @Suppress("UNCHECKED_CAST")
        val listItems = arguments?.getParcelableArray(ITEMS) as Array<SimpleListItem>
        getRecyclerViewAdapter().submitList(listItems.toList())
    }

    private fun getRecyclerViewAdapter(): SimpleListItemAdapter {
        var adapter = recycler_view.adapter as? SimpleListItemAdapter
        if (adapter == null) {
            adapter = SimpleListItemAdapter(requireActivity()) {
                onItemClick?.invoke(it)
                dismissAllowingStateLoss()
            }
            recycler_view.adapter = adapter
        }
        return adapter
    }

    fun updateChooserItems(newItems: Array<SimpleListItem>) {
        if (isAdded) {
            getRecyclerViewAdapter().submitList(newItems.toList())
        }
    }

    companion object {
        private const val TAG = "BottomSheetChooserDialog"
        private const val ITEMS = "data"

        fun createChooser(
            fragmentManager: FragmentManager,
            title: Int?,
            items: Array<SimpleListItem>,
            callback: (SimpleListItem) -> Unit
        ): DynamicBottomSheetChooserDialog {
            val extras = Bundle().apply {
                if (title != null) {
                    putInt(BOTTOM_SHEET_TITLE, title)
                }
                putParcelableArray(ITEMS, items)
            }
            return DynamicBottomSheetChooserDialog().apply {
                arguments = extras
                onItemClick = callback
                show(fragmentManager, TAG)
            }
        }
    }
}
