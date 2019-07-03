package com.example.study_project.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.study_project.BuildConfig
import com.example.study_project.model.PostApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel: BaseViewModel() {

    @Inject
    lateinit var postApi: PostApi

    val dataResponse = MutableLiveData<Array<String>>()

    private val compositeDisposable = CompositeDisposable()

    fun getAirData(lat: Double, lng: Double) {
        compositeDisposable.add(
            postApi.getData(lat, lng, BuildConfig.AIR_TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it?.let {
                    dataResponse.postValue(arrayOf(it?.data?.iaqi?.pm10?.v, it?.data?.iaqi?.pm25?.v))
                }
            })
    }

    fun onDestroy() {
        if(!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}