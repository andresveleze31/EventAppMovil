package com.app.eventhub

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.app.eventhub.databinding.ActivityMainBinding
import com.app.eventhub.databinding.ActivitySignUpBinding
import com.app.eventhub.models.User
import com.app.eventhub.providers.AuthProvider
import com.app.eventhub.providers.UserProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val authProvider = AuthProvider();
    private val userProvider = UserProvider();

    var driverImageNull: String?=null


    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            var intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            register();
        }

        binding.circleImageProfile.setOnClickListener { checkPermissionAndSelectImage() }

    }


    private fun register(){
        val name = binding.txtNombre.text.toString();
        val apellido = binding.txtApellido.text.toString();
        val phone = binding.txtId.text.toString();
        val email = binding.txtEmail.text.toString();
        val password = binding.txtPassword.text.toString();

        if(imageFile == null){
            return Toast.makeText(this, "Ingresa tu Foto de Perfil", Toast.LENGTH_SHORT).show()

        }

        if(isValidForm(email, password, name, phone, apellido)){
            Toast.makeText(this, "Formulario Valido", Toast.LENGTH_SHORT).show()
            authProvider.register(email, password).addOnCompleteListener{
                if(it.isSuccessful){
                    val user = User(id = authProvider.getId(), name = name, identification = phone, email = email, apellido = apellido);

                    userProvider.create(user).addOnCompleteListener{
                        if(it.isSuccessful){
                            var intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this@SignUp, "Error almacenando datos del usuario", Toast.LENGTH_LONG).show()

                        }
                    }

                    if(imageFile != null){
                        userProvider.uploadImage(authProvider.getId(), imageFile!!).addOnSuccessListener { taskSnapshot ->
                            userProvider.getImageUrl().addOnSuccessListener { url ->
                                val imageUrl = url.toString()
                                user.image = imageUrl

                                userProvider.updateUserInfo(user).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        Toast.makeText(this@SignUp, "Registro Existoso", Toast.LENGTH_LONG).show()
                                    }
                                    else{
                                        Toast.makeText(this@SignUp, "Error almacenando datos del usuario", Toast.LENGTH_LONG).show()
                                    }
                                }

                            }



                        }

                    }




                }
                else{
                    Toast.makeText(this@SignUp, "Registro Fallido ${it.exception.toString()}", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Si se otorgan los permisos, selecciona la imagen.
            selectImage()
        } else {
            // Si no se otorgan los permisos, muestra un mensaje de advertencia.
            Toast.makeText(this, "Los permisos de la cámara y la galería son necesarios.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndSelectImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si los permisos ya están otorgados, selecciona la imagen.
            selectImage()
        } else {
            // Si los permisos no están otorgados, solicítalos.
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }



    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if(resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            imageFile = File(fileUri?.path)
            binding.circleImageProfile.setImageURI(fileUri)
        }
        else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Tarea Cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage(){
        ImagePicker.with(this).crop().compress(1024).maxResultSize(1080,1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }

    private fun isValidForm(email: String, password: String, name: String, phone: String, apellido: String):Boolean{
        if(email.isEmpty()){
            Toast.makeText(this, "Ingresa tu Email", Toast.LENGTH_SHORT).show()
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this, "Ingresa tu Password", Toast.LENGTH_SHORT).show()
            return false;

        }
        if(password.length < 6){
            Toast.makeText(this, "Tu Password debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false;

        }
        if(name.isEmpty()){
            Toast.makeText(this, "Ingresa tu Nombre", Toast.LENGTH_SHORT).show()
            return false;


        }
        if(apellido.isEmpty()){
            Toast.makeText(this, "Ingresa tu Apellido", Toast.LENGTH_SHORT).show()
            return false;


        }

        if(phone.isEmpty()){
            Toast.makeText(this, "Ingresa tu Identificacion", Toast.LENGTH_SHORT).show()
            return false;

        }

        return true;

    }
}