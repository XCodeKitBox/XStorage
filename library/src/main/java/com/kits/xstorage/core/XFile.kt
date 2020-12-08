package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class XFile(val context: Context, val targetUri: Uri) {
    var targetFile:File? = null

    companion object{
        fun getUriForFile(context: Context, file: File): Uri {
            var fileUri: Uri? = null
            fileUri = if (Build.VERSION.SDK_INT >= 24) {
                println("import == ${file.getCanonicalPath()}")
                FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
            } else {
                Uri.fromFile(file)
            }
            return fileUri
        }
    }

    constructor(context: Context,file: File):this(context,getUriForFile(context,file)){
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