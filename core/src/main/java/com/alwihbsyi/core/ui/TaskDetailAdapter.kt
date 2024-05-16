package com.alwihbsyi.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alwihbsyi.core.R
import com.alwihbsyi.core.databinding.ItemTaskBinding
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.utils.getStatus
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show

class TaskDetailAdapter: Adapter<TaskDetailAdapter.TaskDetailViewHolder>() {
    inner class TaskDetailViewHolder(private val binding: ItemTaskBinding): ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                tvTitle.text = task.title
                tvDueDate.text = itemView.context.getString(R.string.status, getStatus(task.status))
                tvDescription.text = task.description

                btnDone.hide()
                btnInProgress.hide()

                val buttons = listOf(
                    itemView,
                    btnExpand
                )

                buttons.forEach {
                    it.setOnClickListener {
                        if (layoutExpand.isVisible) layoutExpand.hide()
                        else layoutExpand.show()
                    }
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDetailViewHolder =
        TaskDetailViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: TaskDetailViewHolder, position: Int) {
        val task = differ.currentList[position]
        holder.bind(task)
    }
}