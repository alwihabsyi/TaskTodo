package com.alwihbsyi.tasktodo.task

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.ui.TaskDetailAdapter
import com.alwihbsyi.core.utils.dateToString
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.setUpDetailDialog
import com.alwihbsyi.core.utils.show
import com.alwihbsyi.core.utils.toast
import com.alwihbsyi.tasktodo.databinding.FragmentTaskBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<TaskViewModel>()
    private val taskDetailAdapter by lazy { TaskDetailAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpActions()
    }

    private fun setUpActions() {
        binding.apply {
            btnCreate.setOnClickListener {
                startActivity(Intent(requireContext(), CreateTaskActivity::class.java))
            }

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.time
                getTaskDetail(dateToString(selectedDate))
            }
        }
    }

    private fun getTaskDetail(selectedDate: String) {
        viewModel.getTaskDetail(selectedDate).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(resource.message ?: "Terjadi kesalahan")
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    resource.data?.let { tasks ->
                        taskDetailAdapter.differ.submitList(tasks)
                        setUpDetailDialog(selectedDate) {
                            it.layoutManager = LinearLayoutManager(requireContext())
                            it.adapter = taskDetailAdapter
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}