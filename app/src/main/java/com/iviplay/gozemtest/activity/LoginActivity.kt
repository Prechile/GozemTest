package com.iviplay.gozemtest.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iviplay.gozemtest.R
import com.iviplay.gozemtest.data.model.LoginModel
import com.iviplay.gozemtest.databinding.ActivityLoginBinding
import com.iviplay.gozemtest.utils.SharedPreferenceManager
import com.iviplay.gozemtest.utils.Toolkit

class LoginActivity : AppCompatActivity() {

    private val LOGTAG = LoginActivity::class.java.simpleName
    private lateinit var bindingMainActivity: ActivityLoginBinding
    private lateinit var toolkit: Toolkit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainActivity = ActivityLoginBinding.inflate(layoutInflater)
        val view = bindingMainActivity.root
        setContentView(view)

        toolkit = Toolkit(applicationContext)
        initView()
    }




    private fun initView(){
        supportActionBar!!.hide()
        bindingMainActivity.progressbar.visibility = View.GONE

        bindingMainActivity.submitButton.setOnClickListener { v ->
            if (bindingMainActivity.emailInput.editText?.text.toString().isEmpty() && bindingMainActivity.passwordInput.editText?.text.toString().isEmpty())
            {
                Toast.makeText(applicationContext,getText(R.string.fill_all_fields),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginProcess()
        }

        bindingMainActivity.loginHelp.setOnClickListener {
            goToRegister()
        }
    }

    private fun loginProcess(){
        if (toolkit.isConnected()){
            bindingMainActivity.submitButton.isClickable = false
            bindingMainActivity.progressbar.visibility = View.VISIBLE

            val credentials = LoginModel(
                bindingMainActivity.emailInput.editText?.text.toString(),
                bindingMainActivity.passwordInput.editText?.text.toString()
            )

            val user = SharedPreferenceManager.getInstance(this)?.getUserResponse()
            if (user != null){
                if (user.email.equals(credentials.email) &&
                    user.password.equals(credentials.password))
                {
                    val home = Intent(this, MainActivity::class.java)
                    startActivity(home)
                    finish()
                }else{
                    bindingMainActivity.submitButton.isClickable = true
                    bindingMainActivity.progressbar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "Email ou mot de passe incorrecte",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }else{
            Toast.makeText(
                applicationContext,
                getText(R.string.is_not_connected),
                Toast.LENGTH_SHORT
            ).show()
            bindingMainActivity.progressbar.visibility = View.GONE
        }
    }


    private fun goToRegister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}