package com.alwihbsyi.tasktodo.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.utils.getFileName
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.core.utils.uriToFile
import com.alwihbsyi.tasktodo.databinding.ActivityAddGalleryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddGalleryActivity : AppCompatActivity() {

    private var _binding: ActivityAddGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<GalleryViewModel>()
    private var file: File? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Uri? = result.data?.data
                data?.let { uri ->
                    val name = getFileName(uri, this)
                    file = uriToFile(uri, this)
                    if (name != null) {
                        binding.etFile.setText(name)
                    } else {
                        toast("Gagal mendapat dokumen")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() {
        binding.apply {
            etFile.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                getContent.launch(intent)
            }

            btnCreate.setOnClickListener {
                if (inputNotValid()) {
                    toast("Harap isi semua bagian")
                    return@setOnClickListener
                }

                val gallery = Gallery(name = etTitle.text.toString())
                uploadGallery(gallery, file!!)
            }
        }
    }

    private fun uploadGallery(gallery: Gallery, file: File) {
        viewModel.setGallery(gallery, file).observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.data ?: "")
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    toast(it.data ?: "")
                    finish()
                }
            }
        }
    }

    private fun inputNotValid(): Boolean {
        return binding.etTitle.text.toString().isEmpty() || file == null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}