package com.example.study_project.viewmodel

import androidx.lifecycle.ViewModel
import com.example.study_project.di.module.DaggerViewModelInjector
import com.example.study_project.di.module.NetworkModule
import com.example.study_project.di.module.ViewModelInjector

abstract class BaseViewModel: ViewModel() {

    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject() {
        when(this) {
            is MainViewModel -> injector.inject(this)
        }
    }
}