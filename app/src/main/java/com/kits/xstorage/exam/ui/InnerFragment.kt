package com.kits.xstorage.exam.ui

import android.content.Context
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.FileType
import com.kits.xstorage.R
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_inner.*
import me.yokeyword.fragmentation.SupportFragment
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class InnerFragment : SupportFragment(){

    companion object{
        fun newInstance() : InnerFragment {
            val args = Bundle()
            val fragment = InnerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inner,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        btnRead.setOnClickListener {
            val file = xStorage.read(FileType.INNER_FILE,"////myTest//test1","test1116.txt")
            file?.let {
                val inStream = FileInputStream(file.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }
        }

        btnWrite.setOnClickListener {
            val file = xStorage.write(FileType.INNER_FILE,"//myTest//test1","test1116.txt")
            file?.let {
                var outStream: OutputStream? = null
                try {
                    outStream = FileOutputStream(file.targetFile)
                    outStream.write("多层文件夹简单的测试一下\n".toByteArray())
                }catch (e:FileNotFoundException){

                }
                finally {
                    outStream?.close()
                }

            }
        }


        btnCacheRead.setOnClickListener {
            val file = xStorage.read(FileType.INNER_CACHE,"/myTest/test1","test1116.txt")
            file?.let {
                val inStream = FileInputStream(file.targetFile)
                val buf = ByteArray(inStream.available())
                inStream.read(buf)
                println("buf == ${String(buf)}")
            }

        }

        btnCacheWrite.setOnClickListener {
            val file = xStorage.write(FileType.INNER_CACHE,"/myTest/test1","test1116.txt")
            file?.let {
                var outStream: FileOutputStream? = null
                try {
                    outStream = FileOutputStream(file.targetFile)
                    outStream?.write("多层文件夹cache简单的测试一下\n".toByteArray())
                }catch (e:FileNotFoundException){

                }
                finally {
                    outStream?.close()
                }

            }
        }
    }
}