package com.example.busstopsforybs78and94.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.busstopsforybs78and94.BusStopsVO


abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    var data: T? = null
        set(value) {
            field = value

            if(value != null) {
                bindData(value)
            }
        }

    protected abstract fun bindData(data: T)
}