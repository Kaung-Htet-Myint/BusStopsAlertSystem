package com.example.busstopsforybs78and94.mvp

import androidx.lifecycle.ViewModel

abstract class BasePresenter<T: BaseView>: ViewModel()  {



    protected lateinit var view: T

    fun initPresenter(view: T) {
        this.view = view
    }
}