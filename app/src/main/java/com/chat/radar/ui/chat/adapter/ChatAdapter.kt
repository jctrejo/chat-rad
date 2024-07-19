package com.chat.radar.ui.chat.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.android.chat_redar.R
import com.chat.radar.common.Constants.FIREBASE_KEY_SEEN
import com.chat.radar.data.model.ChatModel
import com.chat.radar.ui.chat.ChatActivity
import com.chat.radar.ui.imageview.ViewImageActivity
import com.chat.radar.util.StaticFunctions.Companion.GetCurrentDate
import com.chat.radar.util.StaticFunctions.Companion.GetYesterdayDate
import com.chat.radar.util.StaticFunctions.Companion.OpenFile
import com.chat.radar.util.hide
import com.chat.radar.util.show
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ChatAdapter(
    var context: ChatActivity,
    var list: ArrayList<ChatModel>,
    var username: String,
    var profilePicUrl: String
) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    private val MSG_TYPE_LEFT: Int = 0
    private val MSG_TYPE_RIGHT: Int = 1
    private var fuser: FirebaseUser? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View?
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
        }
        return MyViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(@NonNull holder: MyViewHolder, position: Int) {
        val chatModel: ChatModel = list[position]
        holder.tvMessage.text = chatModel.message
        holder.tvName.text = username
        holder.tvTime.text = chatModel.time
        Picasso.get().load(profilePicUrl).placeholder(R.drawable.ic_gallery).into(holder.imgProfile)

        when(chatModel.date) {
            GetCurrentDate() -> {
                holder.tvDate.text = "Today"
            }
            GetYesterdayDate() -> {
                holder.tvDate.text = "Yesterday"
            }
            else -> {
                holder.tvDate.text = chatModel.date
            }
        }

        if (position > 0) {
            if (chatModel.date == list[position - 1].date) {
                holder.tvDate.hide()
            } else {
                holder.tvDate.show()
            }
        } else {
            holder.tvDate.show()
        }

        setAll(chatModel, holder)

        if (selectedPosition == position) {
            holder.imageVoiceType.setImageResource(R.drawable.ic_stop)
        } else {
            holder.imageVoiceType.setImageResource(R.drawable.ic_play)
        }

        holder.itemView.setOnClickListener {
            val transition = Pair.create<View?, String?>(holder.imageViewChat, "transition")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, transition)
            if (chatModel.imageUrl.isNotEmpty()) {
                val intent = Intent(context, ViewImageActivity::class.java)
                intent.putExtra("imageUrl", chatModel.imageUrl)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent, options.toBundle())
            } else if (chatModel.fileUrl.isNotEmpty()) {
                val url = chatModel.fileUrl
                if (url.contains("jpg") || url.contains("png") || url.contains("jpeg")) {
                    val intent = Intent(context, ViewImageActivity::class.java)
                    intent.putExtra("imageUrl", chatModel.fileUrl)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent, options.toBundle())
                } else {
                    val uri: Uri = Uri.parse(chatModel.fileUrl)
                    OpenFile(chatModel.fileUrl, uri, context)
                }
            }
        }
    }

    private fun setAll(chatModel: ChatModel, holder: MyViewHolder) {
        if (chatModel.imageUrl.isEmpty()) {
            holder.cv.hide()
        } else {
            Picasso.get().load(chatModel.imageUrl).placeholder(R.drawable.ic_gallery)
                .into(holder.imageViewChat)
            holder.cv.show()
        }

        if (chatModel.voiceMessage.isEmpty()) {
            holder.voiceLayout?.hide()
        } else {
            holder.tvVoiceTextView.text = chatModel.duration + " - Voz"
            holder.voiceLayout?.show()
        }

        if (chatModel.fileUrl.isNotEmpty() || chatModel.imageUrl.isNotEmpty() || chatModel.voiceMessage.isNotEmpty()) {
            holder.tvMessage.hide()
        } else {
            holder.tvMessage.show()
        }

        if (chatModel.messageStatus == FIREBASE_KEY_SEEN) {
            holder.imgSeen.setImageResource(R.drawable.ic_seen)
        } else {
            holder.imgSeen.setImageResource(R.drawable.ic_delivered)
        }

        if (chatModel.fileUrl.isNotEmpty()) {
            val url = chatModel.fileUrl
            if (url.contains("jpg") || url.contains("png") || url.contains("jpeg")) {
                Picasso.get().load(chatModel.fileUrl).placeholder(R.drawable.ic_gallery)
                    .into(holder.imageViewChat)
                holder.cv.visibility = View.VISIBLE
                holder.fileLayout?.visibility = View.GONE
            } else {
                holder.cv.visibility = View.GONE
                holder.fileLayout?.visibility = View.VISIBLE
                holder.tvFileTextView?.visibility = View.VISIBLE
                setFileText(chatModel.fileUrl, holder)
            }
        } else {
            holder.fileLayout?.visibility = View.GONE
            holder.tvFileTextView?.visibility = View.GONE
        }
    }

    private fun setFileText(ext: String, holder: MyViewHolder) {
        var fileName = "File"
        var drawable = R.drawable.ic_file
        if (ext.contains("pdf")) {
            fileName = "Pdf"
            drawable = R.drawable.ic_pdf
        } else if (ext.contains("docx")) {
            fileName = "Docx"
            drawable = R.drawable.ic_docx
        } else if (ext.contains("mp4")) {
            fileName = "Video"
            drawable = R.drawable.ic_video
        } else if (ext.contains("mp3")) {
            fileName = "Audio"
            drawable = R.drawable.ic_audio
        } else {
            fileName = ""
        }
        holder.tvFileTextView?.text = "$fileName Archivo"
        holder.imageFileType.setImageResource(drawable)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMessage: TextView
        var tvName: TextView
        var tvTime: TextView
        var fileLayout: LinearLayout? = null
        var voiceLayout: LinearLayout? = null
        var tvFileTextView: TextView? = null
        var imageViewChat: ImageView
        var imageFileType: ImageView
        var imageVoiceType: ImageView
        var tvVoiceTextView: TextView
        var tvDate: TextView
        var cv: CardView
        var imgSeen: ImageView
        var imgProfile: ImageView
        var parentLayout: LinearLayout

        init {
            tvMessage = itemView.findViewById(R.id.tvMessage)
            tvName = itemView.findViewById(R.id.tvName)
            tvTime = itemView.findViewById(R.id.tvTime)
            imageViewChat = itemView.findViewById(R.id.imageViewChat)
            imageVoiceType = itemView.findViewById(R.id.imageVoiceType)
            imageFileType = itemView.findViewById(R.id.imageFileType)
            cv = itemView.findViewById(R.id.cv)
            fileLayout = itemView.findViewById(R.id.fileLayout)
            voiceLayout = itemView.findViewById(R.id.voiceLayout)
            tvFileTextView = itemView.findViewById(R.id.tvFileTextView)
            imgSeen = itemView.findViewById(R.id.imgSeen)
            imgProfile = itemView.findViewById(R.id.imgProfile)
            tvVoiceTextView = itemView.findViewById(R.id.tvVoiceTextView)
            tvDate = itemView.findViewById(R.id.tvDate)
            parentLayout = itemView.findViewById(R.id.parentLayout)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (list.get(position).senderId == fuser?.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}
