package com.alwihbsyi.tasktodo.task

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.notification.NotificationReceiver
import com.alwihbsyi.core.utils.getFileName
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.showDatePicker
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.core.utils.uriToFile
import com.alwihbsyi.tasktodo.databinding.ActivityCreateTaskBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Date

class CreateTaskActivity : AppCompatActivity() {

    private var _binding: ActivityCreateTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<TaskViewModel>()
    private var file: File? = null
    private var selectedDate: Date? = null
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
        _binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() {
        binding.apply {
            etDueDate.setOnClickListener {
                showDatePicker(this@CreateTaskActivity) { dateString, date ->
                    etDueDate.setText(dateString)
                    selectedDate = date
                }
            }

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

                val task = Task(
                    title = etTitle.text.toString(),
                    description = etDescription.text.toString(),
                    dueDate = etDueDate.text.toString()
                )
                createTask(task)
            }
        }
    }

    private fun createTask(task: Task) {
        viewModel.createTask(task, file!!).observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.message ?: "Terjadi kesalahan")
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    toast(it.data ?: "Task created")
                    scheduleNotification(task.notificationId)
                    finish()
                }
            }
        }
    }

    private fun scheduleNotification(id: Int) {
        selectedDate?.let {
            val notificationDate = it.time - 3 * 60 * 60 * 1000
            val intent = Intent(this, NotificationReceiver::class.java)
            intent.putExtra("title", "Task Reminder")
            intent.putExtra("text", "Due date for task ${binding.etTitle.text.toString()} is in 3 hours")
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationDate, pendingIntent)
        }
    }

    private fun inputNotValid(): Boolean {
        return binding.etTitle.text.toString().isEmpty() || binding.etDescription.text.toString().isEmpty() ||
                binding.etDueDate.text.toString().isEmpty() || file == null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}