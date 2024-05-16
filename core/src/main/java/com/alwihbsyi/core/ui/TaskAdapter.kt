package com.alwihbsyi.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alwihbsyi.core.databinding.ItemTaskBinding
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.utils.Constants.DONE
import com.alwihbsyi.core.utils.Constants.IN_PROGRESS
import com.alwihbsyi.core.utils.hide
import com.alwihbsyi.core.utils.show

class TaskAdapter(private val status: String, private var fromList: Boolean = false): Adapter<TaskAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(private val binding: ItemTaskBinding): ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                tvTitle.text = task.title
                tvDueDate.text = task.dueDate
                tvDescription.text = task.description

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

                when (status) {
                    IN_PROGRESS -> { btnInProgress.isEnabled = false }
                    DONE -> { btnDone.isEnabled = false }
                }

                btnInProgress.setOnClickListener {
                    onProgressClicked?.invoke(task)
                }
                btnDone.setOnClickListener {
                    onDoneClicked?.invoke(task)
                }
                etFile.setOnClickListener {
                    onFileClick?.invoke(task)
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id && oldItem.status == newItem.status

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int =
        if (fromList) differ.currentList.size
        else if (differ.currentList.size > 3) 3
        else differ.currentList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = differ.currentList[position]
        holder.bind(task)
    }

    var onProgressClicked: ((Task) -> Unit)? = null
    var onDoneClicked: ((Task) -> Unit)? = null
    var onFileClick: ((Task) -> Unit)? = null
}