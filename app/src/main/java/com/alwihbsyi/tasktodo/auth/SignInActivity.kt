package com.alwihbsyi.tasktodo.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.ActivitySignInBinding
import com.alwihbsyi.tasktodo.home.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {

    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() {
        binding.apply {
            tvRegister.setOnClickListener {
                startActivity(Intent(this@SignInActivity, RegisterActivity::class.java))
            }

            btnSignIn.setOnClickListener {
                if (validateInput()) {
                    toast("Harap isi email dan password")
                    return@setOnClickListener
                }

                signIn()
            }
        }
    }

    private fun signIn() {
        viewModel.signIn(binding.etEmail.text.toString(), binding.etPassword.text.toString()).observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    startActivity(
                        Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.message ?: "Terjadi kesalahan")
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        return binding.etEmail.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUser().observe(this) { loggedIn ->
            if (loggedIn) {
                startActivity(
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}