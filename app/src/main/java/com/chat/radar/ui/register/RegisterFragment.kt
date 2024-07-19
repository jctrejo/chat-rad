package com.chat.radar.ui.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.chat_redar.R
import com.android.chat_redar.databinding.FragmentRegisterBinding
import com.chat.radar.common.Constants.EMPTY
import com.chat.radar.common.Constants.FIREBASE_KEY_USERS
import com.chat.radar.common.Constants.IMAGE
import com.chat.radar.common.Constants.JPG
import com.chat.radar.common.UiState
import com.chat.radar.data.model.User
import com.chat.radar.util.AlertErrorDialog
import com.chat.radar.util.DialogCustomProgress
import com.chat.radar.util.LoadingDialog
import com.chat.radar.util.StaticFunctions
import com.chat.radar.util.extension.afterTextChanged
import com.chat.radar.util.extension.errorInputLayout
import com.chat.radar.util.isValidEmail
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var loader: LoadingDialog
    private var auth: FirebaseAuth? = null
    private var rootRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var customProgressDialog: DialogCustomProgress? = null
    private var imageRequestCode = 7
    private var imageUrl: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        loader = LoadingDialog(root.context)
        auth = FirebaseAuth.getInstance()
        rootRef = FirebaseDatabase.getInstance().reference

        registerBtn.setOnClickListener(View.OnClickListener {
            val name = firstNameEt.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val lastName = lastNameEt.text.toString().trim()

            if (!validateData()) {
                return@OnClickListener
            } else {
                customProgressDialog?.show()
                getToken(name, email, password, imageUrl ?: EMPTY, lastName)
            }
        })

        imgAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = IMAGE
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
                StaticFunctions.ShowToast(
                    requireActivity().applicationContext,
                    task.exception?.localizedMessage ?: EMPTY
                )
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
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
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
                userRef = rootRef?.child(FIREBASE_KEY_USERS)?.child(userid ?: EMPTY)
                userRef?.setValue(hashMap)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        StaticFunctions.ShowToast(
                            requireActivity().applicationContext,
                            "Registered Successfully"
                        )
                        findNavController().popBackStack()
                    } else {
                        StaticFunctions.ShowToast(
                            requireActivity().applicationContext,
                            task.exception?.localizedMessage ?: EMPTY
                        )
                    }
                }
            } else {
                StaticFunctions.ShowToast(
                    requireActivity().applicationContext,
                    "Error: " + task.exception?.localizedMessage ?: EMPTY
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            customProgressDialog?.show()
            val fileUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
            binding.imgProfile.setImageBitmap(bitmap)
            fileUri?.let { uploadImageToFirebase(it) }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + JPG
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
            findNavController().popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateData(): Boolean = with(binding) {
        firstNameEt.afterTextChanged { firstNameEtLayout.error = null }
        lastNameEt.afterTextChanged { lastNameEtLayout.error = null }
        emailEditText.afterTextChanged { emailEtLayout.error = null }
        passwordEditText.afterTextChanged { passEtLayout.error = null }

        if (firstNameEt.text.toString().isNullOrEmpty()) {
            firstNameEtLayout.errorInputLayout(getString(R.string.enter_first_name))
            return false
        } else if (lastNameEt.text.toString().isNullOrEmpty()) {
            lastNameEtLayout.errorInputLayout(getString(R.string.enter_last_name))
            return false
        } else if (emailEditText.text.toString().isNullOrEmpty()) {
            emailEtLayout.errorInputLayout(getString(R.string.enter_email))
            return false
        } else if (!emailEditText.text.toString().isValidEmail()) {
            emailEtLayout.errorInputLayout(getString(R.string.invalid_email))
            return false
        } else if (passwordEditText.text.toString().isNullOrEmpty()) {
            passEtLayout.errorInputLayout(getString(R.string.enter_password))
            return false
        } else if (passwordEditText.text.toString().length < 8) {
            passEtLayout.errorInputLayout(getString(R.string.invalid_password))
            return false
        } else {
            return true
        }
    }
}
