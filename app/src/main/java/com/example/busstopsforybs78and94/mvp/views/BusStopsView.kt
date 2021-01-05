package com.example.busstopsforybs78and94.mvp.views

import com.example.busstopsforybs78and94.data.vos.BusStopsVO

interface BusStopsView: BaseView {
    fun navigateToMain()
    fun showMap()
    fun showUpDown()
    fun onTapUp()
    fun onTapDown()
}