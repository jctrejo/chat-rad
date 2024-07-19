package com.chat.radar.ui.register

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.chat_redar.databinding.ActivityRegisterBinding
import com.chat.radar.common.Constants.EMPTY
import com.chat.radar.util.DialogCustomProgress
import com.chat.radar.util.StaticFunctions.Companion.ShowToast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var auth: FirebaseAuth? = null
    private var rootRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var customProgressDialog: DialogCustomProgress? = null
    private var imageRequestCode = 7
    private var imageUrl: String? = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        customProgressDialog = DialogCustomProgress(this)
        auth = FirebaseAuth.getInstance()
        rootRef = FirebaseDatabase.getInstance().reference

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val aboutInfo = binding.etAboutInfo.text.toString().trim()

            if (imageUrl?.isEmpty() == true) {
                ShowToast(applicationContext, "Please Upload Image")
            } else if (name.isEmpty()) {
                ShowToast(applicationContext, "Please Enter Name")
            } else if (email.isEmpty()) {
                ShowToast(applicationContext, "Please Enter Email")
            } else if (password.isEmpty()) {
                ShowToast(applicationContext, "Please Enter Password")
            } else if (aboutInfo.isEmpty()) {
                ShowToast(applicationContext, "Please Enter About Info")
            } else {
                customProgressDialog?.show()
                getToken(name, email, password, imageUrl!!, aboutInfo)
            }
        })

        binding.imgAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, imageRequestCode)
        }
    }

    private fun getToken(
        name: String,
        email: String,
        password: String,
        imageUrl: String,
        aboutInfo: String,
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                register(name, email, password, token, imageUrl, aboutInfo)
            } else {
                ShowToast(applicationContext, task.exception?.localizedMessage ?: EMPTY)
            }
        }
    }

    private fun register(
        name: String,
        email: String,
        password: String,
        token: String,
        imageUrl: String,
        aboutInfo: String,
    ) {
        auth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            customProgressDialog?.dismiss()
            if (task.isSuccessful) {
                val firebaseUser = auth?.currentUser
                val userid = firebaseUser?.uid
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["id"] = userid ?: EMPTY
                hashMap["name"] = name
                hashMap["email"] = email
                hashMap["password"] = password
                hashMap["token"] = token
                hashMap["profilePic"] = imageUrl
                hashMap["aboutInfo"] = aboutInfo
                userRef = rootRef?.child("Users")?.child(userid ?: EMPTY)
                userRef?.setValue(hashMap)?.addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        ShowToast(applicationContext, "Registered Successfully")
                        onBackPressed()
                    } else {
                        ShowToast(applicationContext, task.exception?.localizedMessage!!)
                    }
                })
            } else {
                ShowToast(applicationContext, "Error: " + task.exception?.localizedMessage!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            customProgressDialog?.show()
            val fileUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, fileUri)
            binding.imgProfile.setImageBitmap(bitmap)
            fileUri?.let { uploadImageToFirebase(it) }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("profilePictures/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                customProgressDialog?.dismiss()
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    imageUrl = it.toString()
                }
            }.addOnFailureListener { e ->
                print(e.message)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
