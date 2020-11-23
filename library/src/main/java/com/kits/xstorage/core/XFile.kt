package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class XFile(val context: Context, val targetUri: Uri) {
    var targetFile:File? = null
    constructor(context: Context,file: File?):this(context,Uri.fromFile(file)){
        this.targetFile = file
    }
    /**
     * 获取输出流
     * @param mode May be "w", "wa", "rw", or "rwt".
     */

    fun outputStream(mode:String="w"):OutputStream?{
        return context.contentResolver?.openOutputStream(targetUri,mode)
    }

    fun inputStream():InputStream?{
        return context.contentResolver?.openInputStream(targetUri)
    }



    /**
     * 此接口只能在应用内部使用，方便获取绝对路径，文件名称等等
     */
    fun file():File?{
        return targetFile
    }


}