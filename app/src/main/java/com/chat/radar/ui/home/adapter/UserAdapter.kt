package com.chat.radar.ui.home.adapter

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.chat.radar.data.model.ChatModel
import com.chat.radar.data.model.UserModel
import com.android.chat_redar.R
import com.chat.radar.common.Constants.EMPTY
import com.chat.radar.common.Constants.FIREBASE_KEY_CHATS
import com.chat.radar.ui.chat.ChatActivity
import com.chat.radar.ui.home.HomeFragment
import com.chat.radar.ui.imageview.ViewImageActivity
import com.chat.radar.util.glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserAdapter(
    var context: HomeFragment,
    var list: ArrayList<UserModel>,
    var username: String,
    var profileUrl: String,
) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val view: View = LayoutInflater.from(context.requireActivity()).inflate(R.layout.item_user, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userModel: UserModel = list.get(position)
        holder.profilePic.glide(context.requireActivity(), userModel.profilePic)
        holder.tvUserName.text = userModel.name

        holder.profilePic.setOnClickListener {
            val intent = Intent(context.requireActivity(), ViewImageActivity::class.java)
            intent.putExtra("imageUrl", userModel.profilePic)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val transition =
                Pair.create<View?, String?>(holder.profilePic, "transition")
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context.requireActivity(), transition
                )
            context.startActivity(intent, options.toBundle())
        }

        if (userModel.id.equals(firebaseUser?.uid ?: EMPTY)) {
            userModel.name += " (Tu)"
            holder.tvUserName.text = userModel.name
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context.requireActivity(), ChatActivity::class.java)
            intent.putExtra("senderId", firebaseUser?.uid)
            intent.putExtra("recieverId", userModel.id)
            intent.putExtra("recieverName", userModel.name)
            intent.putExtra("token", userModel.token)
            intent.putExtra("senderName", username)
            intent.putExtra("recieverPic", userModel.profilePic)
            intent.putExtra("senderPic", profileUrl)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        checkUserStatus(userModel.id, holder)
        lastMessage(userModel.id, holder)
    }

    private fun lastMessage(userId: String, holder: MyViewHolder) {
        val chatRefrence = FirebaseDatabase.getInstance().reference.child(FIREBASE_KEY_CHATS)
        val fuser = FirebaseAuth.getInstance().currentUser
        chatRefrence.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                var unreadCount = 0
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(ChatModel::class.java)
                    if (chat!!.recieverId == fuser?.uid && chat.senderId == userId ||
                        chat.recieverId == userId && chat.senderId == fuser?.uid
                    ) {

                        if (chat.recieverId == fuser?.uid) {
                            if (chat.messageStatus == "Seen") {
                                holder.tvLastMessage.setTypeface(null, Typeface.NORMAL)
                                holder.imgUnreadMessage.visibility = View.GONE
                                holder.tvMessageCount.visibility = View.GONE
                            } else {
                                unreadCount++
                                holder.tvMessageCount.text = unreadCount.toString()
                                holder.imgUnreadMessage.visibility = View.VISIBLE
                                holder.tvMessageCount.visibility = View.VISIBLE
                                holder.tvLastMessage.setTypeface(null, Typeface.BOLD)
                            }
                        }

                        if (chat.senderId.equals(fuser?.uid)) {
                            holder.tvLastMessage.text = "YOU: " + chat.message
                        } else {
                            holder.tvLastMessage.text = chat.message
                        }

                        if (chat.messageStatus.equals("Seen")) {
                            holder.imgSeen.setImageResource(R.drawable.ic_seen)
                        } else {
                            holder.imgSeen.setImageResource(R.drawable.ic_delivered)
                        }
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    private fun checkUserStatus(id: String, holder: MyViewHolder) {
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Statuses")
        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val status: String = dataSnapshot.child("status").value.toString()
                if (status.isNotEmpty()) {
                    holder.imgOnline.visibility = View.VISIBLE
                    if (status == "online") {
                        holder.imgOnline.setImageResource(R.drawable.online_circle)
                    } else {
                        holder.imgOnline.setImageResource(R.drawable.offline_circle)
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView
        var tvLastMessage: TextView
        var profilePic: ImageView
        var imgOnline: ImageView
        var imgSeen: ImageView
        var imgUnreadMessage: ImageView
        var tvMessageCount: TextView
        var unreadLayout: RelativeLayout

        init {
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage)
            profilePic = itemView.findViewById(R.id.profilePic)
            imgOnline = itemView.findViewById(R.id.imgOnline)
            imgSeen = itemView.findViewById(R.id.imgSeen)
            imgUnreadMessage = itemView.findViewById(R.id.imgUnreadMessage)
            tvMessageCount = itemView.findViewById(R.id.tvMessageCount)
            unreadLayout = itemView.findViewById(R.id.unreadLayout)
        }
    }
}