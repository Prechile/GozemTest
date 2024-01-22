package com.iviplay.gozemtest.repository

import com.iviplay.gozemtest.data.api.ApiClient
import com.iviplay.gozemtest.data.model.ContentData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentRepository {
    fun fetchData(
        token : String,
        onResult : (isSuccess :  Boolean, response : ArrayList<ContentData>?) -> Unit){

        ApiClient.instance.fetchData(token).enqueue(object: Callback<ArrayList<ContentData>> {
            override fun onResponse(call: Call<ArrayList<ContentData>>, response: Response<ArrayList<ContentData>>){
                if(response.code() == 200){
                    onResult(true, response.body())
                }else{
                    onResult(false, null)
                }
            }
            override fun onFailure(call: Call<ArrayList<ContentData>>, t: Throwable?){
                onResult(false,null)
            }
        })
    }


    companion object {
        private var INSTANCE: ContentRepository? = null
        fun getInstance() = INSTANCE
            ?: ContentRepository().also {
                INSTANCE = it
            }
    }
}