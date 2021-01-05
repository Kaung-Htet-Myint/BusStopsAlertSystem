package com.example.busstopsforybs78and94.mvp.presenters

import com.example.busstopsforybs78and94.mvp.views.BusStopsView

open class BusStopsPresenter: BasePresenter<BusStopsView>() {
    fun onTapBackBtn() {
        mView.navigateToMain()
    }

    fun onTapFab(){
        mView.showUpDown()
    }

    fun onTabMapBtn(){
        mView.showMap()
    }
}