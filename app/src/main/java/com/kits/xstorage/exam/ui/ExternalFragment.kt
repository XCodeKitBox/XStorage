package com.kits.xstorage.exam.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.FileType
import com.kits.xstorage.R
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
            val file1 = xStorage.read(FileType.EXTERNAL_FILE,"myTest100.txt")
            file1?.let {
                val inStream = FileInputStream(file1.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }

            // 在file 文件夹下，创建标准文件夹（语义比较强）
            val file2 = xStorage.read(FileType.EXTERNAL_FILE,Environment.DIRECTORY_DCIM,"myTest100.txt")
            file2?.let {
                val inStream = FileInputStream(file2.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }

            // 在file 文件夹下，创建任意层级的文件夹
            val file3 = xStorage.read(FileType.EXTERNAL_FILE,"aa/bb","myTest100.txt")
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
            val file1 = xStorage.read(FileType.EXTERNAL_CACHE,"myTest200.txt")
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
            }


        }
    }
}