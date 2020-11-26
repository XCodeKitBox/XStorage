package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.kits.xstorage.FileMode
import com.kits.xstorage.Storage
import java.io.File
import java.util.regex.Pattern

open class BaseStorage {

    companion object{
        const val DIR_REG = "(/)\\1+"
    }

    fun formatPath(path:String):String{
        return Pattern.compile(DIR_REG).matcher(path).replaceAll("$1")
    }

    fun buildFilePath(basePath:String?,dir:String?,file:String):String?{

        if (basePath == null){
            return null
        }
        val path = if (dir == null){
            basePath + File.separator + file
        }else{
            basePath + File.separator + dir + File.separator + file
        }
        return formatPath(path)
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
        // 存在外置SD卡的时候，存在外置SD卡只读的情况，尚未适配
//        if (FileMode.READ == mode && Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()){
//            return true
//        }
        return false
    }

    /**
     *文件的操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param context 上下文
     * @param contentUri 需要查询的Uri
     */
    fun getFileByUri(context: Context, contentUri: Uri):XFile?{
        val queryCursor = context.contentResolver.query(contentUri,null,
                null, null,null)
        if (queryCursor != null && queryCursor.count > 0){
            queryCursor.close()
            return XFile(context,contentUri)
        }
        return null
    }

    /**
     * 删除文件操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param context 上下文
     * @param contentUri 需要删除的Uri
     */
    fun deleteFileByUri(context: Context, contentUri: Uri):Int{
        return context.contentResolver.delete(contentUri,null,null)
    }

}