package com.example.busstopsforybs78and94.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import com.example.busstopsforybs78and94.BusStopsVO
import com.example.busstopsforybs78and94.R
import com.example.busstopsforybs78and94.viewholders.BustopsViewHolder

class BusStopsAdapter : BaseAdapter<BustopsViewHolder, BusStopsVO>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BustopsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_stop_item_view,parent,false)
        return BustopsViewHolder(view)
    }

}