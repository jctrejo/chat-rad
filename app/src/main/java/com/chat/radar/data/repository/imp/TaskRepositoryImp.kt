package com.chat.radar.data.repository.imp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.chat.radar.data.model.Task
import com.chat.radar.data.model.User
import com.chat.radar.util.FireDatabase
import com.chat.radar.common.UiState
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.chat.radar.data.repository.TaskRepository
import com.chat.radar.util.FireStoreDocumentField.USER_ID

class TaskRepositoryImp(
    val database: FirebaseDatabase
) : TaskRepository {

    override fun addTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        val reference = database.reference.child(FireDatabase.TASK).push()
        val uniqueKey = reference.key ?: "invalid"
        task.id = uniqueKey
        reference
            .setValue(task)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(task,"Task has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        val reference = database.reference.child(FireDatabase.TASK).child(task.id)
        reference
            .setValue(task)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(task,"Task has been updated successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun getTasks(user: User?, result: (UiState<List<Task>>) -> Unit) {
        database.reference.child(FireDatabase.TASK).orderByChild(USER_ID).equalTo(user?.id?: "").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tasks = arrayListOf<Task?>()
                    for (item in snapshot.children){
                        val task = item.getValue(Task::class.java)
                        tasks.add(task)
                    }
                    result.invoke(UiState.Success(tasks.filterNotNull()))
                } else {
                    result.invoke(UiState.Success(emptyList()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Failure(
                        error.message
                    )
                )
            }
        })
    }

    override fun deleteTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        val reference = database.reference.child(FireDatabase.TASK).child(task.id)
        reference.removeValue()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(task,"Task has been deleted successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun getMembership(membership: Int?, result: (UiState<Boolean>) -> Unit) {
        database.reference.child(FireDatabase.MEMBERSHIP).orderByChild("code").equalTo(membership.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    result.invoke(UiState.Success(true))
                } else {
                    result.invoke(UiState.Success(false))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Failure(
                        "Algo ocurrio, vuelve a intentarlo"
                    )
                )
            }
        })
    }
}