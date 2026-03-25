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
        etEmail.isEnabled = modoEdicion
        etPassword.isEnabled = modoEdicion
        btnChangePhoto.visibility = if (modoEdicion) View.VISIBLE else View.GONE
        btnSaveProfile.visibility = if (modoEdicion) View.VISIBLE else View.GONE

        // Cargar datos actuales desde SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("userName", "Admin Colegio")
        val email = sharedPref.getString("userEmail", "admin@mercedes.edu.pe")
        val photoUriString = sharedPref.getString("userPhotoUri", null)

        etName.setText(name)
        etEmail.setText(email)
        
        if (photoUriString != null) {
            try {
                ivProfilePic.setImageURI(Uri.parse(photoUriString))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Botón cambiar foto
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Botón guardar perfil
        btnSaveProfile.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()
            val newPassword = etPassword.text.toString().trim()

            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                with(sharedPref.edit()) {
                    putString("userName", newName)
                    putString("userEmail", newEmail)
                    if (newPassword.isNotEmpty()) {
                        putString("userPassword", newPassword)
                    }
                    apply()
                }
                
                (activity as? MainActivity)?.updateNavHeader()
                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                
                // Si venimos de Configuración, podemos volver atrás al guardar
                if (modoEdicion) {
                    parentFragmentManager.popBackStack()
                }
            } else {
                Toast.makeText(context, "El nombre y correo no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}