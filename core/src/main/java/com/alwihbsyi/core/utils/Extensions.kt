package com.alwihbsyi.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alwihbsyi.core.R
import com.alwihbsyi.core.notification.NotificationReceiver
import com.alwihbsyi.core.utils.Constants.DONE
import com.alwihbsyi.core.utils.Constants.IN_PROGRESS
import com.alwihbsyi.core.utils.Constants.TO_DO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun showDatePicker(context: Context, func: (String, Date) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    var selectedDate: Date

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = selectedCalendar.time

            TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    selectedCalendar.set(Calendar.MINUTE, selectedMinute)
                    selectedDate = selectedCalendar.time

                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    val dateString = dateFormat.format(selectedDate)
                    func(dateString, selectedDate)
                },
                hour,
                minute,
                true
            ).show()
        },
        year,
        month,
        day
    )

    datePickerDialog.show()
}

fun dateToString(date: Date): String =
    SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(date)

fun getFileName(uri: Uri, context: Context): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

@SuppressLint("InflateParams", "MissingInflatedId")
fun Fragment.setUpDetailDialog(
    selectedDate: String,
    rvSetup: (RecyclerView) -> Unit
) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.task_detail_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val rvTasks = view.findViewById<RecyclerView>(R.id.rv_tasks)
    val tvDueDate = view.findViewById<TextView>(R.id.tv_dialog_date)
    tvDueDate.text = selectedDate
    rvTasks.apply {
        rvSetup(this)
    }
}

fun getStatus(status: String): String {
    return when (status) {
        TO_DO -> "To-Do"
        IN_PROGRESS -> "In Progress"
        DONE -> "Done"
        else -> ""
    }
}

suspend fun getMimeType(url: String): String {
    return withContext(Dispatchers.IO) {
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connect()
        connection.contentType ?: ""
    }
}

fun cancelNotification(context: Context, notificationId: Int) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

