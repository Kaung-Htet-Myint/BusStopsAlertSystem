package com.example.busstopsforybs78and94.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.busstopsforybs78and94.viewholders.BaseViewHolder

abstract class BaseAdapter<VH: BaseViewHolder<T>, T>: RecyclerView.Adapter<VH>() {
    var dataList: MutableList<T> = ArrayList()

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
            holder.data = dataList[position]
    }

    fun setNewData(newData: MutableList<T>) {
        dataList = newData
        notifyDataSetChanged()
    }

    fun appendNewData(newData: MutableList<T>) {
        dataList.addAll(newData)
    }
}