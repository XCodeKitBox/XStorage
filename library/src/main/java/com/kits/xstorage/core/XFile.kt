package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class XFile(val context: Context, val targetUri: Uri) {
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

    fun file():File?{
        return targetUri.toFile()
    }


}