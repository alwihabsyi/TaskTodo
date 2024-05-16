package com.alwihbsyi.tasktodo.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.ActivityRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (inputNotValid()) {
                    toast("Harap isi email dan password")
                    return@setOnClickListener
                }

                registerUser(etEmail.text.toString(), etPassword.text.toString())
            }

            tvSignIn.setOnClickListener {
                finish()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        viewModel.signUp(email, password).observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    toast("Berhasil mendaftar, silahkan masuk")
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.message ?: "Terjadi kesalahan")
                }
            }
        }
    }

    private fun inputNotValid(): Boolean {
        return binding.etEmail.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}