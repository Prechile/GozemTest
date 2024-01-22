package com.iviplay.gozemtest.repository

import com.iviplay.gozemtest.data.api.ApiClient
import com.iviplay.gozemtest.data.model.LoginModel
import com.iviplay.gozemtest.data.model.UserRegisterModel
import com.iviplay.gozemtest.data.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    fun login(
        credentials : LoginModel,
        onResult : (isSuccess :  Boolean, response : UserResponse?) -> Unit){

        ApiClient.instance.login(credentials).enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>){
                if(response.code() == 200){
                    onResult(true, response.body())
                }else{
                    onResult(false, null)
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable?){
                onResult(false,null)
            }
        })
    }



    fun register(
        credentials : UserRegisterModel,
        onResult : (isSuccess :  Boolean, response : UserResponse?) -> Unit){

        ApiClient.instance.register(credentials).enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>){
                if(response.code() == 200){
                    onResult(true, response.body())
                }else{
                    onResult(false, null)
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable?){
                onResult(false,null)
            }
        })
    }


    companion object {
        private var INSTANCE: UserRepository? = null
        fun getInstance() = INSTANCE
            ?: UserRepository().also {
                INSTANCE = it
            }
    }
}