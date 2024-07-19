package com.chat.radar.ui.login

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chat.radar.util.extension.afterTextChanged
import com.chat.radar.util.extension.errorInputLayout
import com.chat.radar.util.AlertErrorDialog
import com.chat.radar.util.LoadingDialog
import com.chat.radar.common.UiState
import com.chat.radar.util.isValidEmail
import com.android.chat_redar.R
import com.android.chat_redar.databinding.FragmentLoginBinding
import com.chat.radar.common.Constants
import com.chat.radar.di.DataProvider
import com.chat.radar.ui.register.AuthViewModel
import com.chat.radar.util.StaticFunctions.Companion.ShowToast
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var errorMessage: AlertErrorDialog
    private lateinit var loader: LoadingDialog
    private val viewModel: AuthViewModel by viewModels()
    var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        loader = LoadingDialog(binding.root.context)
        errorMessage = AlertErrorDialog(requireContext())
        auth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            goToHome()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (!validateData()) {
                return@setOnClickListener
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data =
                            Uri.parse(
                                String.format(
                                    "package:%s",
                                    requireActivity().applicationContext.packageName
                                )
                            )
                        startActivityForResult(intent, 2296)
                    } else {
                        checkPermission2(email, password)
                    }
                } else {
                    checkPermission(email, password)
                }
            }
        }
        binding.registerLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun checkPermission(email: String, password: String) {
        Dexter.withContext(requireActivity())
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        login(email, password)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkPermission2(email: String, password: String) {
        Dexter.withContext(requireActivity())
            .withPermissions(
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        login(email, password)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun login(email: String, password: String) {
        loader.show()
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                loader.dismiss()
                if (it.isSuccessful) {
                    goToHome()
                } else {
                    ShowToast(requireActivity(), it.exception?.localizedMessage!!)
                }
            }
    }

    private fun observer(){
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    loader.show()
                }
                is UiState.Failure -> {
                    loader.dismiss()
                    errorMessage.typeMessage(AlertErrorDialog.TypeErrorDialog.CUSTOM, state.error)
                }
                is UiState.Success -> {
                    loader.dismiss()
                    val preferences = DataProvider.providesPreferences(binding.root.context)
                    preferences.put(Constants.PREFERENCES_KEY_LOGIN, true)
                    goToHome()
                }
            }
        }
    }

    private fun goToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
    }

    private fun validateData(): Boolean = with(binding) {
        emailEditText.afterTextChanged { emailTextInputLayout.error = null }
        passwordEditText.afterTextChanged { passTextInputLayout.error = null }

        if (emailEditText.text.toString().isNullOrEmpty()) {
            emailTextInputLayout.errorInputLayout(getString(R.string.enter_email))
            return false
        } else if (!emailEditText.text.toString().isValidEmail()) {
            emailTextInputLayout.errorInputLayout(getString(R.string.invalid_email))
            return false
        } else if (passwordEditText.text.toString().isNullOrEmpty()) {
            passTextInputLayout.errorInputLayout(getString(R.string.enter_password))
            return false
        } else if (passwordEditText.text.toString().length < 8) {
            passTextInputLayout.errorInputLayout(getString(R.string.invalid_password))
            return false
        } else {
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null){
                val preferences = DataProvider.providesPreferences(binding.root.context)
                if (preferences.getBoolean(Constants.PREFERENCES_KEY_LOGIN)) {
                    goToHome()
                } else {
                    findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                }
            }
        }
    }
}
