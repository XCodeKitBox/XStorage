package com.kits.xstorage.core

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

    fun createFile(basePath:String?,dir:String?,file:String): File?{
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
        return File(targetPath)
    }

    fun readFile(basePath:String?,dir:String?,file:String): File?{
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
        return targetFile
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

}