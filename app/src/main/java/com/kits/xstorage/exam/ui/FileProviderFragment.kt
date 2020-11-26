package com.kits.xstorage.exam.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.FileMode
import com.kits.xstorage.R
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_file_provider.*
import me.yokeyword.fragmentation.SupportFragment

class FileProviderFragment : SupportFragment(){
    private var targetUri: Uri? = null
    companion object{
        const val CODE_APP_FILES = 100
        fun newInstance() : FileProviderFragment {
            val args = Bundle()
            val fragment = FileProviderFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_provider,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }
    private fun initView(){
        btnAppFiles.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            targetUri = xStorage.filesPathUri(null,"simple1.jpg")
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, CODE_APP_FILES)
        }

        btnAppCache.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            targetUri = xStorage.cachePathUri(null,"simple1.jpg")
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, CODE_APP_FILES)
        }
        btnAppExFiles.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            targetUri = xStorage.externalFilesPathUri(null,"simple1.jpg")
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, CODE_APP_FILES)
        }

        btnAppExCache.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            targetUri = xStorage.externalCachePathUri(null,"simple1.jpg")
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, CODE_APP_FILES)
        }

        btnExMedia.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            targetUri = xStorage.publicMediaPathUri(Environment.DIRECTORY_PICTURES,null,"simple1.jpg", FileMode.WRITE)
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, CODE_APP_FILES)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        println("requestCode == $requestCode;resultCode == $resultCode, data = ${data?.data}")
        ivShow.setImageURI(targetUri)

    }
}