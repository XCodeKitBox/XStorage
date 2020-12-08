package com.kits.xstorage.exam.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.kits.xstorage.FileType
import com.kits.xstorage.R
import com.kits.xstorage.exam.utils.SdCardUtils
import com.kits.xstorage.exam.utils.SpStoreUtils
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_external.*
import me.yokeyword.fragmentation.SupportFragment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ExternalFragment : SupportFragment(){

    companion object{
        fun newInstance() : ExternalFragment {
            val args = Bundle()
            val fragment = ExternalFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_external,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        btnStorageManager.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SdCardUtils.test(requireContext())
            }
        }
        btnPrimaryStorage.setOnClickListener {
            val externalFilesDirs = ContextCompat.getExternalFilesDirs(requireContext(), null)
            for(dirs in externalFilesDirs){
                println("externalFilesDir path == ${dirs.absolutePath}")
            }

            val cacheFilesDirs = ContextCompat.getExternalCacheDirs(requireContext())
            for(dirs in cacheFilesDirs){
                println("cacheFilesDir path == ${dirs.absolutePath}")
            }

            val externalMediaDirs = context!!.externalMediaDirs
            for(dirs in externalMediaDirs){
                println("externalMediaDir path == ${dirs.absolutePath}")
            }

            //  ContextCompat.getExternalFilesDirs 数组的第一个元素
            val externalFilesDir = requireContext().getExternalFilesDir(null)
            println("primary externalFilesDir path == ${externalFilesDir?.absolutePath}")
            val cacheFilesDir = requireContext().externalCacheDir
            println("primary cacheFilesDir path == ${cacheFilesDir?.absolutePath}")

        }
        btnSdStorage.setOnClickListener {
            // 读文件
            val testFile = File(requireContext().getExternalFilesDir(null),"simple1.txt")
            if(testFile.exists()){
                println("文件存在---")
                val inStream = FileInputStream(testFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                inStream.close()
                println("buf == ${String(buf)}")
            }else{
                println("文件不存在---")
                testFile.createNewFile()
                val outputStream = FileOutputStream(testFile)
                outputStream.write("写入简单的文本".toByteArray())
                outputStream.close()
            }
        }

        btnWriteSdStorageUri.setOnClickListener {
            val file1 = xStorage.write(FileType.EXTERNAL_FILE,"MyTestAAAAA.txt")
            file1?.let {
                val out = FileOutputStream(file1.targetFile)
                out.write("向file文件下直接创建子文件51210241024\n".toByteArray())
                out.close()
                println("Uri == ${file1.targetUri}")
                SpStoreUtils.setParam("exSdCard",file1.targetUri.toString())
            }
        }
        btnReadSdStorageUri.setOnClickListener {
            println("exSdCard == ${SpStoreUtils.getParam<String>("exSdCard")}")
            val fileUri = Uri.parse(SpStoreUtils.getParam<String>("exSdCard"))
            val file = xStorage.getFileByUri(fileUri)
            file?.let {
                file.inputStream()
            }?.let {
                val buf = ByteArray(it.available())
                it.read(buf)
                println("buf == ${String(buf)}")
            }
//            var file = File("/storage/53E2-C84B/Android/data/com.kits.xstorage/files/myTest100.txt")
//            val inputStream = FileInputStream(file)
//            val buf = ByteArray(inputStream.available())
//            inputStream.read(buf)
//            println("buf == ${String(buf)}")

        }



        btnWriteFile.setOnClickListener {
            // 直接在 file 文件夹下 创建文件
            val file1 = xStorage.write(FileType.EXTERNAL_FILE,"myTest100.txt")
            file1?.let {
                val out = FileOutputStream(file1.targetFile)
                out.write("向file文件下直接创建子文件\n".toByteArray())
                out.close()
            }

            // 在file 文件夹下，创建标准文件夹（语义比较强）
            val file2 = xStorage.write(FileType.EXTERNAL_FILE,Environment.DIRECTORY_DCIM,"myTest100.txt")
            file2?.let {
                val out = FileOutputStream(file2.targetFile)
                out.write("在file 文件夹下，创建标准文件夹（语义比较强）\n".toByteArray())
                out.close()
            }

            // 在file 文件夹下，创建任意层级的文件夹
            val file3 = xStorage.write(FileType.EXTERNAL_FILE,"aa/bb","myTest100.txt")
            file3?.let {
                val out = FileOutputStream(file3.targetFile)
                out.write("在file 文件夹下，创建任意层级的文件夹\n".toByteArray())
                out.close()
            }

        }

        btnReadFile.setOnClickListener {
            // 直接在 file 文件夹下 创建文件
            val file1 = xStorage.get(FileType.EXTERNAL_FILE,"myTest100.txt")
            file1?.let {
                val inStream = FileInputStream(file1.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }

            // 在file 文件夹下，创建标准文件夹（语义比较强）
            val file2 = xStorage.get(FileType.EXTERNAL_FILE,Environment.DIRECTORY_DCIM,"myTest100.txt")
            file2?.let {
                val inStream = FileInputStream(file2.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }

            // 在file 文件夹下，创建任意层级的文件夹
            val file3 = xStorage.get(FileType.EXTERNAL_FILE,"aa/bb","myTest100.txt")
            file3?.let {
                val inStream = FileInputStream(file3.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }
        }

        btnWriteCacheFile.setOnClickListener {
            val file1 = xStorage.write(FileType.EXTERNAL_CACHE,"myTest200.txt")
            file1?.let {
                val out = FileOutputStream(file1.targetFile)
                out.write("向Caches文件下直接创建子文件\n".toByteArray())
                out.close()
            }
        }

        btnReadCacheFile.setOnClickListener {
            val file1 = xStorage.get(FileType.EXTERNAL_CACHE,"myTest200.txt")
            file1?.let {
                val inStream = FileInputStream(file1.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }
        }

        btnMediaFiles.setOnClickListener {
            println("version == ${Build.VERSION.SDK_INT}")
            val dirs = requireContext().externalMediaDirs
            dirs.forEach {
                println("it == $it")
            }
            if (dirs.isNotEmpty()){
                val imageFiles = File(dirs[0],"simple.png")
                val outputStream = FileOutputStream(imageFiles)
                val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
                val tmpCanvas = Canvas()
                tmpCanvas.setBitmap(bitmap)
                tmpCanvas.drawColor(Color.GREEN)
                tmpCanvas.save()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                bitmap.recycle()

                MediaScannerConnection.scanFile(requireContext()
                        , arrayOf(imageFiles.absolutePath)
                        , arrayOf("image/jpeg")) { path, uri ->
                    println("path == $path ; uri == $uri")
                }
            }

        }
        // 扫描失败
        btnMediaFile.setOnClickListener {
            val imageFile = xStorage.write(FileType.EXTERNAL_FILE,Environment.DIRECTORY_DCIM,"myTest100.png")
            val outputStream = imageFile?.outputStream()
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.RED)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            bitmap.recycle()

            MediaScannerConnection.scanFile(requireContext()
                    , arrayOf(imageFile?.targetFile.toString())
                    , arrayOf("image/jpeg")) { path, uri ->
                println("path == $path ; uri == $uri")
            }
        }
    }
}