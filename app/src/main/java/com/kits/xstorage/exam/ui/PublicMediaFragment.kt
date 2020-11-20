package com.kits.xstorage.exam.ui

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.kits.xstorage.R
import com.kits.xstorage.core.PublicStore
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_public_media.*
import me.yokeyword.fragmentation.SupportFragment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PublicMediaFragment : SupportFragment(){

    companion object{
        fun newInstance() : PublicMediaFragment {
            val args = Bundle()
            val fragment = PublicMediaFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_public_media,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){

        btnQueryMedia.setOnClickListener {
            queryFile()
        }

        btnTest.setOnClickListener {
            // API >=29 崩溃 Permission denied
            val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val outputStream = FileOutputStream(File(imageDir,"myTest1116.jpg"))
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.GREEN)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            bitmap.recycle()
        }

        btnWriteImage.setOnClickListener {
            val outputStream = xStorage.createImage(Environment.DIRECTORY_DCIM,
                null,"just.png", ContentValues())?.outputStream()
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.GREEN)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            bitmap.recycle()

        }

        btnWriteDocument.setOnClickListener {
            val outputStream = xStorage.createFile(Environment.DIRECTORY_DOWNLOADS,"kk","MySimple.txt")?.outputStream()
            outputStream?.write("在Downloads中写下文件kk\n".toByteArray())
            outputStream?.close()
        }

        btnReadDocument.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                queryFile(requireContext(),"MySimple.txt")
//            }
            val inputStream = xStorage.getFile(Environment.DIRECTORY_DOWNLOADS,"kk","MySimple.txt")?.inputStream()
            inputStream?.let {
                val buf = ByteArray(inputStream.available())
                inputStream.read(buf)
                inputStream.close()
                println("buf == ${String(buf)}")
            }
        }

        btnWritePicture.setOnClickListener {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            }
            for (index in 0..5){
                val fileName = "Simple${index}.jpg"
                val outputStream = xStorage.createImage(Environment.DIRECTORY_DCIM,fileName)?.outputStream()
                if (outputStream == null){
                    println("outputStream == null")
                }
                outputStream?.let {
                    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
                    val tmpCanvas = Canvas()
                    tmpCanvas.setBitmap(bitmap)
                    tmpCanvas.drawColor(Color.RED)
                    tmpCanvas.save()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                    bitmap.recycle()
                }
            }

        }

        btnDeleteFile.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                deleteFile(requireContext(),"MySimple.txt")
//            }
//           val  ret = xStorage.deleteFile(Environment.DIRECTORY_DOWNLOADS,"kk","MySimple.txt")
//           println("ret == $ret")
            // 删除非本应用创建的文件
            val ret = xStorage.deleteImageFile(Environment.DIRECTORY_DCIM,null,"just.png.jpg")
            println("ret == $ret")
        }
    }

    private fun writePictures(){
        val outputStream = xStorage.createImage(Environment.DIRECTORY_PICTURES,"simple.png")?.outputStream()
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        val tmpCanvas = Canvas()
        tmpCanvas.setBitmap(bitmap)
        tmpCanvas.drawColor(Color.GREEN)
        tmpCanvas.save()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
        bitmap.recycle()
    }
    private fun writeInDownloads(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS)
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"simple")
            val contentUri = MediaStore.Files.getContentUri("external")
            val insertUri = requireContext().contentResolver?.insert(contentUri,contentValues)
            // val outputStream = xStorage.writeFile(Environment.DIRECTORY_DOWNLOADS,"MyTest1.txt")
            val outputStream = requireContext().contentResolver.openOutputStream(insertUri!!)
            outputStream?.write("在Downloads中写下文件\n".toByteArray())
            outputStream?.close()
        }
    }

    private fun writeInDocuments(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOCUMENTS)
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"simple")
        val contentUri = MediaStore.Files.getContentUri("external")
        val insertUri = requireContext().contentResolver?.insert(contentUri,contentValues)
        // val outputStream = xStorage.writeFile(Environment.DIRECTORY_DOWNLOADS,"MyTest1.txt")
        val outputStream = requireContext().contentResolver.openOutputStream(insertUri!!)
        outputStream?.write("在 Documents 中写下文件\n".toByteArray())
        outputStream?.close()
    }

    private fun queryFile(){
        val selection = MediaStore.MediaColumns.DISPLAY_NAME +"LIKE Simple%"
        val cursor = requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,
            MediaStore.Images.Media.DISPLAY_NAME+" LIKE 'Simple%' ", null,null)

        cursor?.takeIf { it.count > 0 }?.let {
                cursor.moveToFirst()
                do {
                    println("filename ${cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))}")
                }while (cursor.moveToNext())
        }

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteFile(context: Context, name:String){
        val ret = context.contentResolver.delete(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            MediaStore.MediaColumns.RELATIVE_PATH+"=?"+" AND "+
                    MediaStore.MediaColumns.DISPLAY_NAME+"=?",
                    arrayOf(Environment.DIRECTORY_DOWNLOADS+File.separator,name))
        println("ret == $ret")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryFile(context: Context, name:String){
        val queryCursor = context.contentResolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,null,
            MediaStore.MediaColumns.RELATIVE_PATH+"=?"+" AND "+
            MediaStore.MediaColumns.DISPLAY_NAME+"=?",
            arrayOf(Environment.DIRECTORY_DOWNLOADS+File.separator,name),null)
        println("queryUri == $queryCursor")
        queryCursor?.let {
            println("number == ${it.count}")
            if (it.count == 0){
                return
            }
            println("columnCount == ${it.columnCount}")

            it.moveToFirst()
            do {

                for (index in 0 until it.columnCount){
                    println("${it.getColumnName(index)} == ${it.getString(index)}")
                }
                val mediaId= queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Images.Media._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI,mediaId)
                val inputStream = context.contentResolver?.openInputStream(uri)
                inputStream?.let {
                    val buf = ByteArray(inputStream.available())
                    inputStream.read(buf)
                    inputStream.close()
                    println("buf == ${String(buf)}")
                }
            } while (it.moveToNext())

            queryCursor.close()
        }
    }
}