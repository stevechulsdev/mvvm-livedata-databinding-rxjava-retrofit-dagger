package com.example.study_project.model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @GET("/feed/geo:{lat};{lng}/?")
    fun getData(@Path("lat")lat: Double, @Path("lng")lng: Double, @Query("token") token: String): Observable<DataResponse>

}