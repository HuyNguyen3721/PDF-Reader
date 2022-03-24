package com.ezstudio.pdfreaderver4.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ezstudio.pdfreaderver4.R
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(
    var context: Context,
    list: MutableList<T>
) :
    BaseRecyclerAdapter<T, VH>(context, list) {

    var listenerDelete: ((Int) -> Unit)? = null
    var listenerRemoveRecent: ((Int) -> Unit)? = null
    var listenerAddBookmark: ((Int) -> Unit)? = null
    var listenerRemoveBookmark: ((Int) -> Unit)? = null
    var listenerShare: ((Int) -> Unit)? = null
    var listenerInfo: ((Int) -> Unit)? = null
    var listenerRename: ((Int) -> Unit)? = null

    fun initPopupMenu(
        view: View,
        position: Int,
        isFavorite: Boolean,
        isRecent: Boolean,
        isShowingFragmentFavorite: Boolean = false
    ): PopupMenu {
        val popMenu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PopupMenu(context, view, Gravity.NO_GRAVITY, 0, R.style.Widget_App_PopupMenu)
        } else {
            PopupMenu(context, view)
        }
//        val popMenu = PopupMenu(context, view)
        popMenu.apply {
            inflate(R.menu.menu_file_option)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_to_bookmark -> {
                        listenerAddBookmark?.invoke(position)
                    }
                    R.id.remove_bookmark -> {
                        listenerRemoveBookmark?.invoke(position)
                    }
                    R.id.remove_recent -> {
                        listenerRemoveRecent?.invoke(position)
                    }
                    R.id.rename -> {
                        listenerRename?.invoke(position)
                    }
                    R.id.share_file -> {
                        listenerShare?.invoke(position)
                    }
                    R.id.delete -> {
                        listenerDelete?.invoke(position)
                    }
                    R.id.file_info -> {
                        listenerInfo?.invoke(position)
                    }
                }
                true
            }
        }
        val itemSetAs: Menu = popMenu.menu

        if (isRecent) {
            itemSetAs.removeItem(R.id.delete)
            if (isShowingFragmentFavorite) {
                itemSetAs.removeItem(R.id.remove_recent)
            }
        } else {
            itemSetAs.removeItem(R.id.remove_recent)
            if (isShowingFragmentFavorite) {
                itemSetAs.removeItem(R.id.delete)
            }
        }
        if (isFavorite) {
            itemSetAs.removeItem(R.id.add_to_bookmark)
        } else {
            itemSetAs.removeItem(R.id.remove_bookmark)
        }
        return popMenu
    }

    fun showPopupMenu(popMenu: PopupMenu) {
        try {
            val pop = PopupMenu::class.java.getDeclaredField("mPopup")
            pop.isAccessible = true
            val menu = pop.get(popMenu)
            menu.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            popMenu.show()
        }
    }
}