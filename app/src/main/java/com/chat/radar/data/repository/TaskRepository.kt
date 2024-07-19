package com.chat.radar.data.repository

import com.chat.radar.data.model.Task
import com.chat.radar.data.model.User
import com.chat.radar.common.UiState

interface TaskRepository {
    fun addTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun updateTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun deleteTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun getTasks(user: User?, result: (UiState<List<Task>>) -> Unit)
    fun getMembership(membership: Int?, result: (UiState<Boolean>) -> Unit)
}