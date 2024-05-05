package com.app.eventhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.eventhub.databinding.ActivityMainBinding
import com.app.eventhub.providers.AuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val authProvider = AuthProvider();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnSignUp.setOnClickListener{
            var intent = Intent(baseContext, SignUp::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if(authProvider.existSession()){
            var intent = Intent(baseContext, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        val email = binding.txtEmail.text.toString();
        val password = binding.txtPassword.text.toString();

        if(isValidForm(email, password)){
            authProvider.login(email, password).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this@MainActivity, "Formulario Valido", Toast.LENGTH_SHORT).show();
                    var intent = Intent(baseContext, MapActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@MainActivity, "Error Iniciando Sesion", Toast.LENGTH_SHORT).show()

                }
            }
        }


    }

    private fun isValidForm(email: String, password: String):Boolean{
        if(email.isEmpty()){

            Toast.makeText(this, "Ingresa tu Email", Toast.LENGTH_SHORT).show()
            return false;

        }

        if(password.isEmpty()){

            Toast.makeText(this, "Ingresa tu Password", Toast.LENGTH_SHORT).show()
            return false;

        }

        return true;

    }
}