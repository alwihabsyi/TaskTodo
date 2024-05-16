package com.alwihbsyi.tasktodo.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.FragmentHomeBinding
import com.alwihbsyi.tasktodo.media.MediaActivity
import com.alwihbsyi.tasktodo.task.TaskListActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var todoAdapter: TaskAdapter
    private lateinit var inProgressAdapter: TaskAdapter
    private lateinit var doneAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            tvSeeTodo.setOnClickListener {
                startActivity(Intent(requireContext(), TaskListActivity::class.java).apply {
                    putExtra(TaskListActivity.STATUS, TO_DO)
                })
            }

            tvSeeInProgress.setOnClickListener {
                startActivity(Intent(requireContext(), TaskListActivity::class.java).apply {
                    putExtra(TaskListActivity.STATUS, IN_PROGRESS)
                })
            }

            tvSeeDone.setOnClickListener {
                startActivity(Intent(requireContext(), TaskListActivity::class.java).apply {
                    putExtra(TaskListActivity.STATUS, DONE)
                })
            }
        }
    }

    private fun observer() {
        viewModel.getTasks().observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    toast(resource.message ?: "Terjadi kesalahan")
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    resource.data?.let {
                        initRv()
                        setUpRv(it)
                    }
                }
            }
        }
    }

    private fun initRv() {
        todoAdapter = TaskAdapter(TO_DO)
        inProgressAdapter = TaskAdapter(IN_PROGRESS)
        doneAdapter = TaskAdapter(DONE)
        binding.rvTodo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoAdapter
        }

        binding.rvInProgress.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inProgressAdapter
        }

        binding.rvDone.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = doneAdapter
        }
    }

    private fun setUpRv(task: List<Task>) {
        val todoList = task.filter { it.status == TO_DO }
        todoList.isNotEmpty().let {
            todoAdapter.differ.submitList(todoList)
        }

        val inProgressList = task.filter { it.status == IN_PROGRESS }
        inProgressList.isNotEmpty().let {
            inProgressAdapter.differ.submitList(inProgressList)
        }

        val doneList = task.filter { it.status == DONE }
        doneList.isNotEmpty().let {
            doneAdapter.differ.submitList(doneList)
        }

        setRvActions()
    }

    private fun setRvActions() {
        val listAdapter = listOf(todoAdapter, inProgressAdapter, doneAdapter)
        listAdapter.forEach { adapter ->
            adapter.onDoneClicked = {
                updateTask(it.copy(status = DONE))
                cancelNotification(requireContext(), it.notificationId)
            }

            adapter.onProgressClicked = {
                updateTask(it.copy(status = IN_PROGRESS))
            }

            adapter.onFileClick = {
                lifecycleScope.launch {
                    val mimeType = getMimeType(it.file!!)

                    when {
                        mimeType.startsWith("video") -> playVideo(it.file!!)
                        else -> openFile(it.file!!, mimeType)
                    }
                }
            }
        }
    }

    private fun playVideo(file: String) {
        startActivity(Intent(requireContext(), MediaActivity::class.java).apply {
            putExtra(MediaActivity.VID_URL, file)
        })
    }

    private fun openFile(url: String, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), mimeType)
        startActivity(intent)
    }

    private fun updateTask(task: Task) {
        viewModel.updateTask(task).observe(viewLifecycleOwner) {
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
                    observer()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}