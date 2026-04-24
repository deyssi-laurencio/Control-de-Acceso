package com.example.controldeacceso

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilFragment : Fragment() {

    private lateinit var ivProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private var imageUri: Uri? = null
    private var modoEdicion: Boolean = false

    // Contrato para seleccionar imagen de la galería
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            ivProfilePic.setImageURI(imageUri)
            
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("userPhotoUri", imageUri.toString())
                apply()
            }
            (activity as? MainActivity)?.updateNavHeader()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modoEdicion = it.getBoolean("modoEdicion", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        ivProfilePic = view.findViewById(R.id.ivProfilePic)
        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        val btnChangePhoto = view.findViewById<FloatingActionButton>(R.id.btnChangePhoto)
        val btnSaveProfile = view.findViewById<MaterialButton>(R.id.btnSaveProfile)

        // Aplicar modo edición o solo lectura
        etName.isEnabled = modoEdicion
        etEmail.isEnabled = false // El correo no se edita por ser el ID único
        etPassword.isEnabled = modoEdicion
        btnChangePhoto.visibility = if (modoEdicion) View.VISIBLE else View.GONE
        btnSaveProfile.visibility = if (modoEdicion) View.VISIBLE else View.GONE

        // Cargar datos desde la API
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "") ?: ""
        
        if (userEmail.isNotEmpty()) {
            RetrofitClient.api.obtenerUsuario(userEmail).enqueue(object : Callback<UsuarioResponse> {
                override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val usuario = response.body()!!
                        etName.setText(usuario.nombre)
                        etEmail.setText(usuario.correo)
                        etPassword.setText(usuario.password)
                        
                        // Cargar foto local si existe (según requerimiento de mantener lógica actual de imagen)
                        val photoUriString = sharedPref.getString("userPhotoUri", null)
                        if (photoUriString != null) {
                            ivProfilePic.setImageURI(Uri.parse(photoUriString))
                        }
                    }
                }
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    Toast.makeText(context, "Error al cargar perfil del servidor", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Botón cambiar foto
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Botón guardar perfil
        btnSaveProfile.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPassword = etPassword.text.toString().trim()

            if (newName.isNotEmpty() && newPassword.isNotEmpty()) {
                val request = UsuarioRequest(
                    nombre = newName,
                    password = newPassword,
                    foto = sharedPref.getString("userPhotoUri", "") ?: ""
                )

                RetrofitClient.api.actualizarUsuario(userEmail, request).enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                        if (response.isSuccessful) {
                            with(sharedPref.edit()) {
                                putString("userName", newName)
                                apply()
                            }
                            
                            (activity as? MainActivity)?.updateNavHeader()
                            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(context, "Campos vacíos no permitidos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}