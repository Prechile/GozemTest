package com.iviplay.gozemtest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.iviplay.gozemtest.R
import com.iviplay.gozemtest.data.model.UserRegisterModel
import com.iviplay.gozemtest.databinding.ActivityLoginBinding
import com.iviplay.gozemtest.databinding.ActivityRegisterBinding
import com.iviplay.gozemtest.repository.UserRepository
import com.iviplay.gozemtest.utils.SharedPreferenceManager

class RegisterActivity : AppCompatActivity() {

    private val LOGTAG = RegisterActivity::class.java.simpleName
    private lateinit var bindingMainActivity: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainActivity = ActivityRegisterBinding.inflate(layoutInflater)
        val view = bindingMainActivity.root
        setContentView(view)
        initView()
        bindingMainActivity.registerHelp.setOnClickListener{
            goToLogin()
        }
    }



    private fun initView(){
        bindingMainActivity.progressbar.visibility = View.GONE
        supportActionBar!!.hide()
        bindingMainActivity.submitButton.setOnClickListener {
            if (checkFormValidity())
                sendRegistration()
            else
                Toast.makeText(applicationContext,
                    getString(R.string.fill_all_fields),
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun checkFormValidity(): Boolean {
        return (!bindingMainActivity.fullnameInput.editText?.text.isNullOrEmpty() &&
                !bindingMainActivity.emailInput.editText?.text.isNullOrEmpty() &&
                !bindingMainActivity.passwordInput.editText?.text.isNullOrEmpty())
    }

    private fun sendRegistration() {
        bindingMainActivity.progressbar.visibility = View.VISIBLE
        val userRegisterModel = UserRegisterModel(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            bindingMainActivity.fullnameInput.editText?.text.toString(),
            bindingMainActivity.emailInput.editText?.text.toString(),
            bindingMainActivity.passwordInput.editText?.text.toString()
        )
        UserRepository.getInstance().register(userRegisterModel) { isSuccess, response ->
            if (!isSuccess) {
                val home = Intent(this, MainActivity::class.java)
                SharedPreferenceManager.getInstance(this)!!.userResponse(userRegisterModel)
                home.putExtra("fullname",  bindingMainActivity.fullnameInput.editText?.text.toString())
                home.putExtra("email", bindingMainActivity.emailInput.editText?.text.toString())
                home.putExtra("pass", bindingMainActivity.passwordInput.editText?.text.toString())
                startActivity(home)
                finish()
                bindingMainActivity.progressbar.visibility = View.GONE
            } else {
                Toast.makeText(
                    applicationContext,
                    "Une erreur s'est produite, veuillez ressayer",
                    Toast.LENGTH_SHORT
                ).show()
                bindingMainActivity.progressbar.visibility = View.GONE
            }
        }
    }


    private fun goToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }

}