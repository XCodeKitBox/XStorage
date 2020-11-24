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
import com.kits.xstorage.core.XFile
import com.kits.xstorage.exam.utils.SpStoreUtils
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
    private fun updateImage(uri:Uri){
        // 更新为红色
        val outStream = xStorage.getFileByUri(uri)?.outputStream()
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        val tmpCanvas = Canvas()
        tmpCanvas.setBitmap(bitmap)
        tmpCanvas.drawColor(Color.RED)
        tmpCanvas.save()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream?.close()
        bitmap.recycle()
    }
    private fun initView(){
        var testUri:Uri?=null
        btnUpdate.setOnClickListener {
            val tmp = SpStoreUtils.getParam<String>("aa")
            println("111testUri == $tmp")
            updateImage(Uri.parse(tmp))
        }

        btnSame.setOnClickListener {
            val file = xStorage.createImage(Environment.DIRECTORY_PICTURES,null,"index.jpg")
            testUri = file?.targetUri
            println("222testUri == $testUri")
            SpStoreUtils.setParam("aa",testUri.toString())
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

        btnSame1.setOnClickListener {
            val outStream = xStorage.createImage(Environment.DIRECTORY_PICTURES,null,"kk.jpg")?.outputStream()
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.RED)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream?.close()
            bitmap.recycle()
        }
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
            addImage(Environment.DIRECTORY_PICTURES)
            addImage(Environment.DIRECTORY_DCIM)

            addImage(Environment.DIRECTORY_PICTURES,"mm/nn")
            addImage(Environment.DIRECTORY_DCIM,"kk/hh")

        }
        btnGetImage.setOnClickListener {
            getImage(Environment.DIRECTORY_DCIM)
            getImage(Environment.DIRECTORY_PICTURES)

            getImage(Environment.DIRECTORY_PICTURES,"mm/nn")
            getImage(Environment.DIRECTORY_DCIM,"kk/hh")
        }

        btnDeleteImage.setOnClickListener {
            xStorage.deleteImageFile(Environment.DIRECTORY_DCIM,imageFile)
            xStorage.deleteImageFile(Environment.DIRECTORY_PICTURES,imageFile)

            xStorage.deleteImageFile(Environment.DIRECTORY_PICTURES,"mm/nn",imageFile)
            xStorage.deleteImageFile(Environment.DIRECTORY_DCIM,"kk/hh",imageFile)

            //xStorage.deleteImageFile(Environment.DIRECTORY_DCIM,"Camera","IMG_20201123_110205.jpg")
            //deleteMediaByUri()
        }

        btnAddFile.setOnClickListener {
            val file1 = addFile(Environment.DIRECTORY_DOCUMENTS,null,file)
            SpStoreUtils.setParam("file1",file1?.targetUri.toString())
            println("file1 == ${SpStoreUtils.getParam<String>("file1")}")
//            addFile(Environment.DIRECTORY_DOWNLOADS,null,file)
//
//            addFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
//            addFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)
//            // 关于文件分隔符问题
//            addFile(Environment.DIRECTORY_DOCUMENTS,null,"////myTest11.txt")

        }

        btnGetFile.setOnClickListener {
            println("===DIRECTORY_DOCUMENTS==无子文件夹")
            getFileByUri(Uri.parse(SpStoreUtils.getParam<String>("file1")))
//            getFile(Environment.DIRECTORY_DOCUMENTS,null,file)
//            println("===DIRECTORY_DOWNLOADS==无子文件夹")
//            getFile(Environment.DIRECTORY_DOWNLOADS,null,file)
//
//            println("===DIRECTORY_DOWNLOADS==子文件夹")
//            getFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
//            println("===DIRECTORY_DOCUMENTS==子文件夹")
//            getFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)

        }

        btnDeleteFile.setOnClickListener {
           // val ret1 = xStorage.deleteFile(Environment.DIRECTORY_DOCUMENTS,file)
            val ret1 = xStorage.deleteFileByUri(Uri.parse(SpStoreUtils.getParam<String>("file1")))
            println("===DIRECTORY_DOCUMENTS==无子文件夹 ${ret1}")
//            val ret2 = xStorage.deleteFile(Environment.DIRECTORY_DOWNLOADS,null,file)
//            println("===DIRECTORY_DOWNLOADS==无子文件夹 $ret2")
//            val ret3 = xStorage.deleteFile(Environment.DIRECTORY_DOCUMENTS,"aa/bb",file)
//            println("===DIRECTORY_DOWNLOADS==子文件夹 $ret3")
//            val ret4 = xStorage.deleteFile(Environment.DIRECTORY_DOWNLOADS,"cc/dd",file)
//            println("===DIRECTORY_DOCUMENTS==子文件夹 $ret4")


        }

    }

    private fun addFile(type: String,dir:String?=null,fileName: String):XFile?{
        val file = xStorage.createFile(type,dir,fileName)
        val outStream = file?.outputStream()
        if (outStream == null){
            println("addFile $type === outStream == null")
        }
        outStream?.let {
            println("$type;$dir;$fileName 创建文件成功")
            outStream.write("写入一些简单的文本，用于测试\n".toByteArray())
            outStream.close()
        }
        return file

    }
    private fun getFileByUri(uri: Uri){
        val file = xStorage.getFileByUri(uri)
        val outStream = file?.outputStream()
        if (outStream == null){
            println(" 获取输出流失败")
        }
        outStream?.let {
            println("获取输出流")
            outStream.write("更新 写入一些简单的文本，用于测试\n".toByteArray())
            outStream.close()
        }

        val inStream = file?.inputStream()
        if (inStream == null){
            println(" 获取输入流失败")
        }
        inStream?.let {
            println("获取输入流")
            val buf = ByteArray(inStream.available())
            inStream.read(buf)
            inStream.close()
            println("数据内容: ${String(buf)}")
        }

    }
    private fun getFile(type: String,dir:String?=null,fileName: String){
        val file = xStorage.getFile(type,dir,fileName)
        println("${file?.file()?.absolutePath};${file?.file()?.name}")
        val outStream = file?.outputStream()
        if (outStream == null){
            println("$type;$dir;$fileName 获取输出流失败")
        }
        outStream?.let {
            println("$type;$dir;$fileName 获取输出流")
            outStream.write("更新 写入一些简单的文本，用于测试\n".toByteArray())
            outStream.close()
        }

        val inStream = xStorage.getFile(type,dir,fileName)?.inputStream()
        if (outStream == null){
            println("$type;$dir;$fileName 获取输出流失败")
        }
        inStream?.let {
            println("$type;$dir;$fileName 获取输入流")
            val buf = ByteArray(inStream.available())
            inStream.read(buf)
            inStream.close()
            println("数据内容: ${String(buf)}")
        }
    }

    private fun addImage(type:String=Environment.DIRECTORY_PICTURES,dir:String? = null){
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
        val outStream = xStorage.getFileByUri(tmpUri!!)?.outputStream()
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
        val ret = xStorage.deleteFileByUri(tmpUri!!)
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

    private fun insertImage(){
//        MediaStore.Images.Media.insertImage(
//                contentResolver,
//                mShareBitmap!!,
//                "image_file",
//                "file")
    }



}