package com.nakama.circlo.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nakama.circlo.BuildConfig
import com.nakama.circlo.ui.MainActivity
import com.nakama.circlo.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


private const val MAXIMAL_SIZE = 1000000 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun ImageView.glide(url: String) {
    Glide.with(this).load(url).into(this)
}

@RequiresApi(Build.VERSION_CODES.O)
fun AppCompatEditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val currentDate = Calendar.getInstance()
    val formatter = SimpleDateFormat(format, Locale.UK)
    val formattedDate = formatter.format(currentDate.time)

    setText(formattedDate)

    val datePickerOnDateSetListener =
        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            currentDate.set(Calendar.YEAR, year)
            currentDate.set(Calendar.MONTH, monthOfYear)
            currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setText(formatter.format(currentDate.time))
        }

    setOnClickListener {
        val datePickerDialog = DatePickerDialog(
            context,
            R.style.MyDatePickerDialogTheme,
            datePickerOnDateSetListener,
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )

        // Convert maxDate to milliseconds if provided
        maxDate?.let { datePickerDialog.datePicker.maxDate = it.time }

        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.greenLight))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.greenLight))
    }
}

fun TextView.timeSpinner(context: Context) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val btnDonate = rootView.findViewById<TextView>(R.id.btn_donate_now)

    val timeList = arrayOf("08:00 — 12:00", "12:00 — 16:00", "16:00 — 18:00")
    inputType = InputType.TYPE_NULL
    setText("")

    val spinnerTime: ArrayAdapter<String> = ArrayAdapter<String>(
        context,
        android.R.layout.simple_spinner_dropdown_item,
        timeList
    )

    this.setOnClickListener {
        AlertDialog.Builder(context)
            .setTitle("Select Time")
            .setAdapter(spinnerTime) { dialog, which ->
                setText(timeList[which])
                dialog.dismiss()
            }.create().show()
        btnDonate.isEnabled = true
    }
}

fun confirmDialog(
    context: Context,
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    positiveAction: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButton) { _, _ ->
            positiveAction()
        }
        setNegativeButton(negativeButton) { dialog, _ ->
            dialog.dismiss()
        }
        create()
        show()
    }
}

@SuppressLint("SimpleDateFormat")
fun convertDate(inputDate: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(inputDate)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return formatter.format(date!!)
}

fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int, context: Context): BitmapDescriptor {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources , id, null)
    if (vectorDrawable == null) {
        Log.e("BitmapHelper", "Resource not found")
        return BitmapDescriptorFactory.defaultMarker()
    }
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, color)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun Fragment.hideBottomNavView() {
    val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.menuBottom)
    val fab: FloatingActionButton = (activity as MainActivity).findViewById(R.id.btn_scan)

    appBar.visibility = View.GONE
    fab.visibility = View.GONE
}

fun Fragment.showBottomNavView() {
    val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.menuBottom)
    val fab: FloatingActionButton = (activity as MainActivity).findViewById(R.id.btn_scan)

    appBar.visibility = View.VISIBLE
    fab.visibility = View.VISIBLE
}

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )

}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}


fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}
