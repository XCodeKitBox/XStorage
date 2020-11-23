package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.kits.xstorage.FileMode
import java.io.File

open class BaseStorage {

    fun buildFilePath(basePath:String?,dir:String?,file:String):String?{

        if (basePath == null){
            return null
        }
        return if (dir == null){
            basePath + File.separator + file
        }else{
            basePath + File.separator + dir + File.separator + file
        }

    }

    fun createFile(context: Context,basePath:String?,dir:String?,file:String): XFile?{
        var targetPath:String? = null
        if (basePath == null){
            return null
        }
        if (dir == null){
            targetPath = basePath + File.separator + file
        }else{
            val targetDir = File(basePath + File.separator + dir)
            if(targetDir.exists() || targetDir.mkdirs()){
                targetPath = basePath + File.separator + dir + File.separator + file
            }
        }
        if (targetPath == null){
            return null
        }
        return XFile(context,File(targetPath))
    }

    fun readFile(context: Context,basePath:String?,dir:String?,file:String): XFile?{
        var targetPath:String? = null
        if (basePath == null){
            return null
        }
        if (dir == null){
            targetPath = basePath + File.separator + file
        }else{
            val targetDir = File(basePath + File.separator + dir)
            if(!targetDir.exists()){
                return null
            }else{
                targetPath = basePath + File.separator + dir + File.separator + file
            }
        }
        val targetFile = File(targetPath)

        if (!targetFile.exists()){
            return null
        }
        return XFile(context,File(targetPath))
    }

    fun checkExternalEnable(mode: FileMode):Boolean{
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
            return true
        }
        if (FileMode.READ == mode && Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()){
            return true
        }
        return false
    }

    /**
     *文件的操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param context 上下文
     * @param contentUri 需要查询的Uri
     */
    fun getFileByUri(context: Context, contentUri: Uri?):XFile?{
        contentUri?.let {
            val queryCursor = context.contentResolver.query(contentUri,null,
                    null, null,null)
            if (queryCursor != null && queryCursor.count > 0){
                queryCursor.close()
                return XFile(context,contentUri)
            }
        }
        return null
    }

    /**
     * 删除文件操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param context 上下文
     * @param contentUri 需要删除的Uri
     */
    fun deleteFileByUri(context: Context, contentUri: Uri?):Int{
        contentUri?.let {
            return context.contentResolver.delete(contentUri,null,null)
        }
        return 0
    }

}