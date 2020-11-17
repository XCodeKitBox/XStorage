package com.kits.xstorage.exam.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.R
import com.kits.xstorage.core.PublicStore
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_public_media.*
import me.yokeyword.fragmentation.SupportFragment
import java.io.File
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
//        btnTest.setOnClickListener {
//            // API >=29 崩溃 Permission denied
//            val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//            val outputStream = FileOutputStream(File(imageDir,"myTest1116.jpg"))
//            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
//            val tmpCanvas = Canvas()
//            tmpCanvas.setBitmap(bitmap)
//            tmpCanvas.drawColor(Color.GREEN)
//            tmpCanvas.save()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.close()
//            bitmap.recycle()
//        }

        btnWriteImage.setOnClickListener {
            val outputStream = xStorage.writeImageMedia(Environment.DIRECTORY_DCIM,
                null,"kk.png", ContentValues())
            val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas()
            tmpCanvas.setBitmap(bitmap)
            tmpCanvas.drawColor(Color.GREEN)
            tmpCanvas.save()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            bitmap.recycle()

        }
    }
}