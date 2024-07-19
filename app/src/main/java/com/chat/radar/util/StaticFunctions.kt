package com.chat.radar.util

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.*
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import com.android.chat_redar.R
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StaticFunctions {
    companion object {
        fun OpenFile(url: String, uri: Uri, context: Context) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                if (url.contains(".doc") || url.toString().contains(".docx")) {
                    intent.setDataAndType(uri, "application/msword")
                } else if (url.contains(".pdf")) {
                    intent.setDataAndType(uri, "application/pdf")
                } else if (url.contains(".ppt") || url.toString().contains(".pptx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
                } else if (url.contains(".xls") || url.toString().contains(".xlsx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-excel")
                } else if (url.contains(".zip")) {
                    intent.setDataAndType(uri, "application/zip")
                } else if (url.contains(".rar")) {
                    intent.setDataAndType(uri, "application/x-rar-compressed")
                } else if (url.contains(".rtf")) {
                    intent.setDataAndType(uri, "application/rtf")
                } else if (url.contains(".wav") || url.toString().contains(".mp3")) {
                    intent.setDataAndType(uri, "audio/x-wav")
                } else if (url.contains(".gif")) {
                    intent.setDataAndType(uri, "image/gif")
                } else if (url.contains(".jpg") || url
                        .contains(".jpeg") || url.contains(".png")
                ) {
                    intent.setDataAndType(uri, "image/jpeg")
                } else if (url.contains(".txt")) {
                    intent.setDataAndType(uri, "text/plain")
                } else if (url.contains(".3gp") || url.contains(".mpg") ||
                    url.contains(".mpeg") || url
                        .contains(".mpe") || url.contains(".mp4") || url
                        .contains(".avi")
                ) {
                    intent.setDataAndType(uri, "video/*")
                } else {
                    intent.setDataAndType(uri, "*/*")
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "No application found which can open the file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun GetMimeType(uri: Uri, context: Context): String? {
            val extension: String?
            extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                val mime = MimeTypeMap.getSingleton()
                mime.getExtensionFromMimeType(context.getContentResolver().getType(uri))
            } else {
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
            }
            return extension
        }

        fun GetCurrentDateAndTime(): String {
            val df: DateFormat = SimpleDateFormat("EEE, dd MMM, hh:mm aa")
            val date: String = df.format(Calendar.getInstance().getTime())
            return date
        }

        fun GetCurrentDate(): String {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return df.format(c)
        }

        fun GetYesterdayDate(): String {
            val dateFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy")
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            return dateFormat.format(cal.time)
        }

        fun CopyText(context: Context, textToCopy: String) {
            val cb: ClipboardManager? =
                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("label", textToCopy)
            cb!!.setPrimaryClip(clip)
            ShowToast(context, "Text Copied")
        }

        fun GetCurrentTime(): String {
            return SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
        }

        fun ShowToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun GetRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
            val pixels = 100;
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.setAntiAlias(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(color)
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        fun IsOnline(context: Context): Boolean {
            val activeNetworkInfo =
                (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)!!.activeNetworkInfo
            return activeNetworkInfo != null &&
                    activeNetworkInfo.isConnectedOrConnecting
        }

        fun ApiCallForNotification(
            message: String,
            messageType: Int,
            imageUrl: String,
            fileUrl: String,
            senderId: String,
            recieverId: String,
            fcmToken: String,
            senderProfilePic: String,
            recieverProfilePic: String,
            senderName: String,
            recieverName: String,
            context: Context,
        ) {
            val jsonObject = JSONObject()
            val notificationObj = JSONObject()
            val dataObj = JSONObject()

            try {
                jsonObject.put("to", fcmToken)

                dataObj.put("senderId", senderId)
                dataObj.put("recieverId", recieverId)
                dataObj.put("senderPic", senderProfilePic)
                dataObj.put("recieverPic", recieverProfilePic)
                dataObj.put("messageType", messageType)
                dataObj.put("senderName", senderName)
                dataObj.put("recieverName", recieverName)
                dataObj.put("token", fcmToken)

                if (messageType == 1) {
                    dataObj.put("body", message)
                } else if (messageType == 2) {
                    dataObj.put("body", "Shared an attachment")
                    dataObj.put("imageUrl", imageUrl)
                } else if (messageType == 3) {
                    if (fileUrl.contains("jpg") || fileUrl.contains("png")
                        || fileUrl.contains("jpeg")
                    ) {
                        dataObj.put("body", "Shared an attachment")
                    } else {
                        dataObj.put("body", "Shared a file")
                    }
                    dataObj.put("fileUrl", fileUrl)
                } else if (messageType == 4) {
                    dataObj.put("body", "Shared a voice")
                }

                jsonObject.put("notification", notificationObj)
                jsonObject.put("data", dataObj)

                Log.d("dataObj", "apiCallForNotification: " + dataObj)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val client = AsyncHttpClient()
            client.setTimeout(10000)
            client.addHeader("Content-Type", "application/json");
            client.addHeader(
                "Authorization",
                "key=AAAAm8_kL2g:APA91bEeBGFJooUZ1sG04Ti-kDEIe-kYWQ4iJEXWv2GWTSm8L7KWbgvN0dF5ya3g43I_U-oIkd9LeGb7Oj9IOXVXtAZBnssB3GgET4bkm6aXWEovofdD0jYLd6a633kP95lEOhzb0QEK"
            )
            try {
                val entity = StringEntity(jsonObject.toString())
                client.post(
                    context,
                    "https://fcm.googleapis.com/fcm/send",
                    entity,
                    "application/json",
                    object : AsyncHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<Header?>?,
                            responseBody: ByteArray?
                        ) {
                            val content = String(responseBody!!)
                            Log.d("onSuccess", "Success: $content")
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<Header?>?,
                            responseBody: ByteArray?,
                            error: Throwable?
                        ) {
                            val content = String(responseBody!!)
                            Log.d("onFailure", "Failure: $content")
                        }
                    })
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }

        fun FormateMilliSeccond(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
            if (hours > 0) {
                finalTimerString = "$hours:"
            }
            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "" + seconds
            }
            finalTimerString = "$finalTimerString$minutes:$secondsString"
            return finalTimerString
        }

        fun GetWallpapperList(): ArrayList<Int> {
            val wallpaperList: ArrayList<Int> = ArrayList()
            wallpaperList.add(R.drawable.wp1)
            return wallpaperList
        }
    }
}