package com.chat.radar.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.chat_redar.R
import com.android.chat_redar.databinding.HomeBinding
import com.chat.radar.common.Constants
import com.chat.radar.data.model.UserModel
import com.chat.radar.ui.home.adapter.UserAdapter
import com.chat.radar.ui.imageview.ViewImageActivity
import com.chat.radar.util.LoadingDialog
import com.chat.radar.util.StaticFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: HomeBinding
    private lateinit var loader: LoadingDialog
    private lateinit var contextBinding: Context

    private var adapter: UserAdapter? = null
    private var list: ArrayList<UserModel>? = null
    private var auth: FirebaseAuth? = null
    private var profileUrl: String = ""
    private var senderId: String = ""
    private var recieverId: String = ""
    private var userName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBinding.inflate(layoutInflater)
        contextBinding = binding.root.context
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        loader = LoadingDialog(binding.root.context)
        auth = FirebaseAuth.getInstance()

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(contextBinding)
        binding.recyclerViewUsers.setHasFixedSize(true)
        list = ArrayList()

        getAllChats()
        getFirebaseToken()

        binding.profilePic.setOnClickListener {
            if (profileUrl.isNotEmpty()) {
                val intent = Intent(contextBinding.applicationContext, ViewImageActivity::class.java)
                intent.putExtra("imageUrl", profileUrl)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val transition = Pair.create<View?, String?>(binding.profilePic, "transition")
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), transition)
                startActivity(intent, options.toBundle())
            }
        }

        binding.logOutButton.setOnClickListener {
            setStatus("offline", 1)
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val reference = FirebaseDatabase.getInstance().getReference("Users")
                reference.child(firebaseUser?.uid ?: Constants.EMPTY).child("token").setValue(token)
            } else {
                StaticFunctions.ShowToast(contextBinding.applicationContext, task.exception?.localizedMessage?: Constants.EMPTY)
            }
        }
    }

    private fun getAllChats() {
        list?.clear()
        loader.show()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                list?.clear()
                loader.dismiss()
                for (snapshot in dataSnapshot.children) {
                    val user: UserModel? = snapshot.getValue(UserModel::class.java)
                    list?.add(user ?: UserModel())
                    if (user?.id == firebaseUser?.uid) {
                        profileUrl = user?.profilePic ?: Constants.EMPTY
                        senderId = firebaseUser?.uid ?: Constants.EMPTY
                        recieverId = user?.id ?: Constants.EMPTY
                        userName = user?.name ?: Constants.EMPTY
                        binding.tvName.text = user?.name
                        Picasso.get().load(profileUrl).placeholder(R.drawable.ic_user)
                            .into(binding.profilePic)
                    }
                }
                adapter = UserAdapter(this@HomeFragment, list ?: ArrayList(), userName, profileUrl)
                binding.recyclerViewUsers.setAdapter(adapter)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun onStart() {
        super.onStart()
        setStatus("online", 0)
    }

    private fun setStatus(userStatus: String, logoutOrSetStatus: Int) {
        Handler(Looper.getMainLooper()).post {
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Statuses")
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            if (logoutOrSetStatus == 1) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["status"] = userStatus
                hashMap["lastSeen"] = StaticFunctions.GetCurrentDateAndTime()
                userRef.child(firebaseUser?.uid ?: Constants.EMPTY).setValue(hashMap)
                this.auth?.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            } else {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["status"] = userStatus
                hashMap["lastSeen"] = StaticFunctions.GetCurrentDateAndTime()
                userRef.child(firebaseUser?.uid ?: Constants.EMPTY).setValue(hashMap)
            }
        }
    }
}
