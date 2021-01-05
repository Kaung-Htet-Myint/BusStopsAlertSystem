package com.example.busstopsforybs78and94.mvp.presenters

import androidx.lifecycle.ViewModel
import com.example.busstopsforybs78and94.mvp.views.BaseView

abstract class BasePresenter<T: BaseView>: ViewModel()  {

    protected lateinit var mView: T

    fun initPresenter(view: T) {
        this.mView = view
    }
}