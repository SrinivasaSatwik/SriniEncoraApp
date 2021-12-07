package com.srini.encora.app.ui.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.srini.encora.app.Retrofit.ApiInterface
import com.srini.encora.app.Retrofit.RetrofitInstance
import com.srini.encora.app.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLoginSession();
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }



        getCityList()


    }

    private fun checkLoginSession() {
        val sharedPreferences : SharedPreferences= this.getSharedPreferences("user_data",
            Context.MODE_PRIVATE)
        val sharedIdValue = sharedPreferences.getString("user_name","srini")
        val sharedNameValue = sharedPreferences.getString("password","defaultname")
        if(!sharedIdValue.equals("srini") && !sharedNameValue.equals("defaultname")){
            val intent = Intent(this@LoginActivity,DashboardActivity ::class.java)
            startActivity(intent)
        }
    }


    private fun updateUiWithUser(model: LoggedInUserView) {
        val sharedPreferences : SharedPreferences= this.getSharedPreferences("user_data",
            Context.MODE_PRIVATE)
            val userName:String =  binding.username.getText().toString()
            val password:String = binding.password.getText().toString()

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("user_name",userName)
            editor.putString("password",password)
            editor.apply()
            editor.commit()


        val intent = Intent(this@LoginActivity,DashboardActivity ::class.java)
        startActivity(intent)
        /*val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()*/
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })


}

private fun getCityList(){
    val service = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
    val call = service.signin();

    call.enqueue(object : Callback<ApiInterface> {
        override fun onResponse(call: Call<ApiInterface>, response: Response<ApiInterface>) {
            Log.e("response=",""+response.code())
            if (response.code() == 200) {



            }
        }
        override fun onFailure(call: Call<ApiInterface>, t: Throwable) {
            Log.e("Status","Error")
        }
    })
}