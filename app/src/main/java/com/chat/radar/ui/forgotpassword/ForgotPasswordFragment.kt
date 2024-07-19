package com.chat.radar.ui.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chat.radar.util.extension.afterTextChanged
import com.chat.radar.util.extension.errorInputLayout
import com.chat.radar.common.Constants.EMPTY
import com.chat.radar.util.AlertErrorDialog
import com.chat.radar.util.LoadingDialog
import com.chat.radar.common.UiState
import com.chat.radar.util.isValidEmail
import com.android.chat_redar.R
import com.android.chat_redar.databinding.FragmentForgotPasswordBinding
import com.chat.radar.ui.register.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var errorMessage: AlertErrorDialog
    private lateinit var loader: LoadingDialog
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = LoadingDialog(binding.root.context)
        errorMessage = AlertErrorDialog(requireContext())
        observer()
        binding.forgotPasswordButton.setOnClickListener {
            if (validateData()) {
                viewModel.forgotPassword(binding.emailEditText.text.toString())
            }
        }
    }

    private fun observer(){
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.forgotPasswordButton.text = EMPTY
                    loader.show()
                }
                is UiState.Failure -> {
                    binding.forgotPasswordButton.text = getString(R.string.sending)
                    loader.dismiss()
                    errorMessage.typeMessage(AlertErrorDialog.TypeErrorDialog.CUSTOM, state.error)
                }
                is UiState.Success -> {
                    binding.forgotPasswordButton.text = getString(R.string.send)
                    loader.dismiss()
                }
            }
        }
    }

    private fun validateData(): Boolean = with(binding) {
        emailEditText.afterTextChanged { emailEtLayout.error = null }

        if (emailEditText.text.toString().isNullOrEmpty()) {
            emailEtLayout.errorInputLayout(getString(R.string.enter_email))
            return false
        } else if (!emailEditText.text.toString().isValidEmail()) {
            emailEtLayout.errorInputLayout(getString(R.string.invalid_email))
            return false
        } else {
            return true
        }
    }
}
