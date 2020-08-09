package com.example.busstopsforybs78and94.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.busstopsforybs78and94.BusStopsVO
import kotlinx.android.synthetic.main.bus_stop_item_view.view.*

class BustopsViewHolder(itemView: View): BaseViewHolder<BusStopsVO>(itemView) {
    override fun bindData(data: BusStopsVO) {
        itemView.tvBusStopName.text = data.BusStop_Name
    }

}