package com.kits.xstorage.exam.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.R
import com.kits.xstorage.xStorage
import kotlinx.android.synthetic.main.fragment_file_provider.*
import me.yokeyword.fragmentation.SupportFragment

class FileProviderFragment : SupportFragment(){
    companion object{
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
        btnSysTest1.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val targetUri = xStorage.rootFileProvider(null,"simple.png")
            println("fileUri == $targetUri")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
            startActivityForResult(takePictureIntent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        println("requestCode == $requestCode;resultCode == $resultCode, data = ${data?.data}")

    }
}