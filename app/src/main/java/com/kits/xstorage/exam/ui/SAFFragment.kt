package com.kits.xstorage.exam.ui

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.R
import com.kits.xstorage.core.XFile
import com.kits.xstorage.fragment.SAFFilesListener
import com.kits.xstorage.fragment.SAFListener
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_saf.*
import me.yokeyword.fragmentation.SupportFragment
import java.text.SimpleDateFormat
import java.util.*

/**
 * 测试 SAF 的 fragment
 */
class SAFFragment : SupportFragment(){

    companion object{
        fun newInstance() : SAFFragment {
            val args = Bundle()
            val fragment = SAFFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saf,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        btnSingleFile.setOnClickListener {
            xStorage.openSingle(Intent(),"first",object:SAFListener{
                override fun onSuccess(file: XFile) {
                    println("请求单一文件成功")
                    val inStream = file.inputStream()
                    inStream?.let {
                        val buf = ByteArray(inStream.available())
                        inStream.read(buf)
                        inStream.close()
                        println("文件内容=== ${String(buf)}")
                    }
                }

                override fun onFail() {
                    println("请求单一文件失败")
                }

            })
        }

        btMultiFile.setOnClickListener {
            xStorage.openMulti(Intent(),object :SAFFilesListener{
                override fun onSuccess(clipData: ClipData?) {
                    println("请求多个文件成功")
                    clipData?.let {
                        for (i in 0 until clipData.itemCount){
                            val selectUri = clipData.getItemAt(i).uri
                            println("选择的uri === $selectUri")
                        }
                    }
                }

                override fun onFail() {
                    println("请求多个文件失败")
                }
            })
        }

        btnCreateFile.setOnClickListener {
            xStorage.createDocument(Intent().apply {
                type="text/plain"
                // 文件名称
                val filename: String = "MyExam.txt"
                putExtra(Intent.EXTRA_TITLE, filename)
            },"second",object :SAFListener{
                override fun onSuccess(file: XFile) {
                    println("创建文件成功==")
                    val outStream = requireContext().contentResolver.openOutputStream(file.targetUri)
                    outStream?.run {
                        write("随便写入一串数据，测试使用\n".toByteArray())
                        outStream.close()
                    }

                }

                override fun onFail() {
                    println("创建文件失败")
                }
            })
        }
    }

}