package com.ezstudio.pdfreaderver4.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.databinding.LayoutItemAdsBinding
import com.ezstudio.pdfreaderver4.databinding.LayoutItemFileBinding
import com.ezstudio.pdfreaderver4.model.FileModel
import com.ezstudio.pdfreaderver4.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(
    context: Context,
    list: MutableList<FileModel>,
    var isRecent: Boolean = false,
    var isShowingFragmentBookmark: Boolean = false
) :
    BaseAdapter<FileModel, RecyclerView.ViewHolder>(context, list) {
    var listenerClickMore: ((Int) -> Unit)? = null
    var listenerClickItem: ((Int) -> Unit)? = null
    var listenerClickFavorite: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
        fun bindData(data: FileModel) {
            Glide.with(context).load(FileUtils.getIconResId(data.fileType)).into(binding.icFile)
            binding.apply {
                nameFile.text = data.name
                txtSize.text = data.sizeString
                txtDate.text =
                    SimpleDateFormat("dd-MM-yyyy").format(Date(data.date))
                if (data.isFavorite) {
                    icFavoriteFile.setImageResource(R.drawable.ic_bookmark_file_selected)
                } else {
                    icFavoriteFile.setImageResource(R.drawable.ic_bookmark_file)
                }
            }
            //
        }
    }

    inner class ViewHolderAds(var bindingAds: LayoutItemAdsBinding) :
        RecyclerView.ViewHolder(bindingAds.root) {
        fun bindData(data: FileModel) {
            data.ads?.let {
                if (it.parent != null) {
                    (it.parent as ViewGroup).removeView(it)
                }
                bindingAds.adsView.addView(it)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                ViewHolder(
                    LayoutItemFileBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
            else -> {
                ViewHolderAds(
                    LayoutItemAdsBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        holder.apply {
            when (holder) {
                is ViewHolder -> {
                    holder.bindData(data)
                    holder.binding.icMore.setOnClickListener {
//                        listenerClickMore?.invoke(holder.adapterPosition)
                        showPopupMenu(
                            initPopupMenu(
                                holder.binding.icMore, holder.adapterPosition,
                                data.isFavorite,
                                data.timeRecent != 0L, isShowingFragmentBookmark
                            )
                        )
                    }
                    holder.itemView.setOnClickListener {
                        listenerClickItem?.invoke(holder.adapterPosition)
                    }
                    holder.binding.icFavoriteFile.setOnClickListener {
                        listenerClickFavorite?.invoke(holder.adapterPosition)
                    }
                }
                is ViewHolderAds -> {
                    holder.bindData(data)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].ads) {
            null -> {
                1
            }
            else -> {
                0
            }
        }
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindData(list[position])
//        holder.binding.icMore.setOnClickListener {
//            listenerClickMore?.invoke(holder.adapterPosition)
//        }
//        holder.itemView.setOnClickListener {
//            listenerClickItem?.invoke(holder.adapterPosition)
//        }
//    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(LayoutItemFileBinding.inflate(layoutInflater, parent, false))
//    }
}