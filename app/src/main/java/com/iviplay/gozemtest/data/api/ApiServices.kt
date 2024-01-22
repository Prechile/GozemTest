package com.iviplay.gozemtest.data.api

import com.iviplay.gozemtest.data.model.ContentData
import com.iviplay.gozemtest.data.model.LoginModel
import com.iviplay.gozemtest.data.model.UserRegisterModel
import com.iviplay.gozemtest.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @POST("/api/Account/Login")
    fun login(
        @Body credentials: LoginModel
    ): Call<UserResponse>

    @POST("/api/Account/Register")
    fun register(
        @Body credentials: UserRegisterModel
    ): Call<UserResponse>

    @GET("/api/data/informations")
    fun fetchData(
        @Header("Authorization") token: String,
    ): Call<ArrayList<ContentData>>
}