package com.kits.xstorage.exam.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.R
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_media.*
import me.yokeyword.fragmentation.SupportFragment
import java.util.regex.Pattern

class MediaFragment : SupportFragment(){
    private val imageFile ="Simple.jpg"
    private val file = "Simple.txt"
    private var tmpUri:Uri? = null
    companion object{
        fun newInstance() : MediaFragment {
            val args = Bundle()
            val fragment = MediaFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        btnQueryMedia.setOnClickListener {
            queryMedia(imageFile)
        }
        btnFileSeparator.setOnClickListener {
            // 在MediaStore上的文件分隔符问题
            val aa = "/////test1/////test2"
            val regex = "(/)\\1+"
            val res = Pattern.compile(regex).matcher(aa).replaceAll("$1");
            println(res)

            val res1 = Pattern.compile(regex).matcher("/////ffffff").replaceAll("$1")
            println(res1)

            val res2 = Pattern.compile(regex).matcher("afaf").replaceAll("$1")
            println(res2)

            val res3 = Pattern.compile(regex).matcher("afaf///aa").replaceAll("$1")
            println(res3)
        }
        btnAddImage.setOnClickListener {
            //addImage(Environment.DIRECTORY_DCIM)
            addImage(Environment.DIRECTORY_PICTURES,"MyKK")
        }
        btnGetImage.setOnClickListener {
            //getImage(Environment.DIRECTORY_PICTURES,"MyKK")
            getImageByUri()
        }

        btnDeleteImage.setOnClickListener {
           //xStorage.deleteImageFile(Environment.DIRECTORY_PICTURES,"MyKK",imageFile)
            //xStorage.deleteImageFile(Environment.DIRECTORY_DCIM,"Camera","IMG_20201123_110205.jpg")
            deleteMediaByUri()
        }

        btnAddFile.setOnClickListener {

            addFile(Environment.DIRECTORY_DOCUMENTS,null,file)
            addFile(Environment.DIRECTORY_DOWNLOADS,null,file)

            addFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
            addFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)
            // 关于文件分隔符问题
            addFile(Environment.DIRECTORY_DOCUMENTS,null,"////myTest11.txt")

        }

        btnGetFile.setOnClickListener {
            println("===DIRECTORY_DOCUMENTS==无子文件夹")
            getFile(Environment.DIRECTORY_DOCUMENTS,null,file)

            println("===DIRECTORY_DOWNLOADS==无子文件夹")
            getFile(Environment.DIRECTORY_DOWNLOADS,null,file)

            println("===DIRECTORY_DOWNLOADS==子文件夹")
            getFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
            println("===DIRECTORY_DOCUMENTS==子文件夹")
            getFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)

        }

        btnDeleteFile.setOnClickListener {
            val ret1 = xStorage.deleteFile(Environment.DIRECTORY_DOCUMENTS,file)
            println("===DIRECTORY_DOCUMENTS==无子文件夹 ${ret1}")
            val ret2 = xStorage.deleteFile(Environment.DIRECTORY_DOWNLOADS,null,file)
            println("===DIRECTORY_DOWNLOADS==无子文件夹 $ret2")
            val ret3 = xStorage.deleteFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
            println("===DIRECTORY_DOWNLOADS==子文件夹 $ret3")
            val ret4 = xStorage.deleteFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)
            println("===DIRECTORY_DOCUMENTS==子文件夹 $ret4")


        }

    }

    private fun addFile(type: String,dir:String?=null,fileName: String){
        val outStream = xStorage.createFile(type,dir,fileName)?.outputStream()
        outStream?.let {
            outStream.write("写入一些简单的文本，用于测试\n".toByteArray())
            outStream.close()
        }

    }

    private fun getFile(type: String,dir:String?=null,fileName: String){
        val file = xStorage.getFile(type,dir,fileName)
        println("${file?.file()?.absolutePath};${file?.file()?.name}")
        val outStream = file?.outputStream()
        outStream?.let {
            outStream.write("更新 写入一些简单的文本，用于测试\n".toByteArray())
            outStream.close()
        }

        val inStream = xStorage.getFile(type,dir,fileName)?.inputStream()
        inStream?.let {
            val buf = ByteArray(inStream.available())
            inStream.read(buf)
            inStream.close()
            println("数据内容: ${String(buf)}")
        }
    }

    private fun addImage(type:String,dir:String? = null){
        val file = xStorage.createImage(type,dir,imageFile)
        tmpUri = file?.targetUri
        println("存储文件 == $tmpUri")
        val outStream = file?.outputStream()
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        val tmpCanvas = Canvas()
        tmpCanvas.setBitmap(bitmap)
        tmpCanvas.drawColor(Color.GREEN)
        tmpCanvas.save()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream?.close()
        bitmap.recycle()

    }

    /**
     * 可以通过 mimetype 判断文件类型
     */
    private fun getImageByUri(){
        println("获取文件 == $tmpUri")
        val outStream = xStorage.getFileByUri(tmpUri)?.outputStream()
        outStream?.let {
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.RED)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.close()
            bitmap.recycle()
        }
    }

    private fun deleteMediaByUri(){
        val ret = xStorage.deleteFileByUri(tmpUri)
        println("ret === $ret")
    }

    private fun getImage(type: String,dir:String ?= null){
        val file = xStorage.getImageFile(type,dir,imageFile)
        file?.let {
            val outStream = file.outputStream()
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.RED)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream?.close()
            bitmap.recycle()
        }
    }

    private fun  queryMediaByUri(uri: Uri){
        val queryCursor = requireContext().contentResolver.query(uri,null,
                null,
                null,null)
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

            } while (it.moveToNext())
            queryCursor.close()
        }
    }

    private fun queryMedia(fileName:String){
        val queryCursor = requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Images.Media.DISPLAY_NAME+"=?",
                arrayOf(fileName),null)
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

            } while (it.moveToNext())
            queryCursor.close()
        }
    }



}