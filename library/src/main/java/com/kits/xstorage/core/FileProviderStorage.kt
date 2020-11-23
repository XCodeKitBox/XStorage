package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

/**
 * Android 7.0 以后兼容 ，通过 FileProvider 提供路径给系统应用调用
 * 一般用于写，查询的话，统一使用MediaStore
 */
class FileProviderStorage : BaseStorage(){
    /**
     * 路径1 rooPath, API >= 29 要通过SAF
     */
    fun rootPath(context:Context,dir:String?,file:String):Uri?{
//        val rootPath = Environment.getRootDirectory().absolutePath
//        val fileTarget = createFile(rootPath,dir,file)
//        if (fileTarget != null){
//            return getUriForFile(context,fileTarget)
//        }
        return null

    }

    /**
     * 路径2 
     */

    private fun getUriForFile(context: Context, file: File): Uri? {
        var fileUri: Uri? = null
        fileUri = if (Build.VERSION.SDK_INT >= 24) {
              FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
        } else {
            Uri.fromFile(file)
        }
        return fileUri
    }
}