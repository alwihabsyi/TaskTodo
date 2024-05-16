package com.alwihbsyi.tasktodo.task

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.ui.TaskAdapter
import com.alwihbsyi.core.utils.Constants.DONE
import com.alwihbsyi.core.utils.Constants.IN_PROGRESS
import com.alwihbsyi.core.utils.Constants.TO_DO
import com.alwihbsyi.core.utils.cancelNotification
import com.alwihbsyi.core.utils.getMimeType
import com.alwihbsyi.core.utils.getStatus
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.ActivityTaskListBinding
import com.alwihbsyi.tasktodo.home.HomeViewModel
import com.alwihbsyi.tasktodo.media.MediaActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskListActivity : AppCompatActivity() {

    private var _binding: ActivityTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HomeViewModel>()
    private var status: String = ""
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(STATUS)?.let {
            status = it
            binding.tvTitle.text = getStatus(it)
            getData()
        }
    }

    private fun getData() {
        viewModel.getTasks().observe(this) { resource ->
            when (resource) {
                is Resource.Error -> {
                    toast(resource.message ?: "Terjadi kesalahan")
                }

                is Resource.Loading -> {}
                is Resource.Success -> {
                    resource.data?.let {
                        setUpView(status, it)
                    }
                }
            }
        }
    }

    private fun setUpView(status: String, tasks: List<Task>) {
        when (status) {
            TO_DO -> {
                taskAdapter = TaskAdapter(TO_DO, true)
                binding.rvTask.apply {
                    layoutManager = LinearLayoutManager(this@TaskListActivity)
                    adapter = taskAdapter
                }
                taskAdapter.differ.submitList(tasks.filter { it.status == status })
            }
            IN_PROGRESS -> {
                taskAdapter = TaskAdapter(IN_PROGRESS, true)
                binding.rvTask.apply {
                    layoutManager = LinearLayoutManager(this@TaskListActivity)
                    adapter = taskAdapter
                }
                taskAdapter.differ.submitList(tasks.filter { it.status == status })
            }
            DONE -> {
                taskAdapter = TaskAdapter(DONE, true)
                binding.rvTask.apply {
                    layoutManager = LinearLayoutManager(this@TaskListActivity)
                    adapter = taskAdapter
                }
                taskAdapter.differ.submitList(tasks.filter { it.status == status })
            }
        }

        handleRvClick()
    }

    private fun handleRvClick() {
        taskAdapter.onDoneClicked = {
            updateTask(it.copy(status = DONE))
            cancelNotification(this, it.notificationId)
        }

        taskAdapter.onProgressClicked = {
            updateTask(it.copy(status = IN_PROGRESS))
        }

        taskAdapter.onFileClick = {
            lifecycleScope.launch {
                val mimeType = getMimeType(it.file!!)

                when {
                    mimeType.startsWith("video") -> playVideo(it.file!!)
                    else -> openFile(it.file!!, mimeType)
                }
            }
        }
    }

    private fun playVideo(file: String) {
        startActivity(Intent(this, MediaActivity::class.java).apply {
            putExtra(MediaActivity.VID_URL, file)
        })
    }

    private fun openFile(url: String, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), mimeType)
        startActivity(intent)
    }

    private fun updateTask(task: Task) {
        viewModel.updateTask(task).observe(this) {
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
                    toast(it.data ?: "")
                    getData()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val STATUS = "status"
    }
}