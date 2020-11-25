package com.kits.xstorage.fragment

import android.content.ClipData
import com.kits.xstorage.core.XFile

interface SAFListener {
    fun onSuccess(file:XFile)
    fun onFail()
}

interface SAFFilesListener{
    fun onSuccess(clipData: ClipData?)
    fun onFail()
}