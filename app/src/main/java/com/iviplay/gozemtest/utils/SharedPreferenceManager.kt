package com.iviplay.gozemtest.utils

import android.content.Context
import com.iviplay.gozemtest.data.model.UserRegisterModel

class SharedPreferenceManager(private val mCtx: Context) {

    fun userResponse(user: UserRegisterModel): Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN, user.token)
        editor.putString(EMAIL, user.email)
        editor.putString(FULLNAME, user.fullName)
        editor.putString(PASSWORD, user.password)

        editor.apply()
        return true
    }

    fun getUserResponse(): UserRegisterModel {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return UserRegisterModel(
            sharedPreferences.getString(TOKEN,null),
            sharedPreferences.getString(FULLNAME,null),
            sharedPreferences.getString(EMAIL,null),
            sharedPreferences.getString(PASSWORD,null)
        )
    }

    fun clear(){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("fullName")
        editor.remove("email")
        editor.remove("password")
        editor.remove("token")
        editor.apply()
    }


    companion object {
        private var mInstance: SharedPreferenceManager? = null
        private const val SHARED_PREF_NAME = "gozemTest"

        private const val TOKEN = "token"
        private const val EMAIL = "email"
        private const val FULLNAME = "fullName"
        private const val PASSWORD = "password"


        @Synchronized
        fun getInstance(context: Context): SharedPreferenceManager? {
            if (mInstance == null) {
                mInstance =
                    SharedPreferenceManager(context)
            }
            return mInstance
        }
    }
}