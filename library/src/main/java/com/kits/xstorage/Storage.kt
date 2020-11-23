package com.kits.xstorage

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.kits.xstorage.core.FileProviderStorage
import com.kits.xstorage.core.InnerStorage
import com.kits.xstorage.core.PublicStore
import com.kits.xstorage.core.XFile
import com.kits.xstorage.lifecycle.ActivityLifecycle
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class Storage private constructor(){
    lateinit var context:Context
    private lateinit var builder: StorageBuilder
    companion object{
        val instance = StorageHolder.holder
    }

    private object StorageHolder{
        val holder = Storage()
    }

    fun init(application: Application){
        //application.registerActivityLifecycleCallbacks(ActivityLifecycle())
        this.context = application.applicationContext
    }

    /****************************************************************************************************
     * *********************公共操作**************************************
     ***************************************************************************************************/

    fun getFileByUri(uri:Uri?):XFile?{
        return PublicStore().getFileByUri(context,uri)
    }

    fun deleteFileByUri(uri:Uri?):Int{
        return PublicStore().deleteFileByUri(context,uri)
    }

    /****************************************************************************************************
     * *********************内部存储空间，外部存储空间-应用专有目录读写**************************************
     ***************************************************************************************************/

    fun write(fileType: FileType,dir:String?,file:String): File?{
        builder = StorageBuilder(context,fileType,dir,file,FileMode.WRITE)
        return builder.targetFile
    }

    fun write(fileType: FileType,file:String): File?{
        builder = StorageBuilder(context,fileType,file,FileMode.WRITE)
        return builder.targetFile
    }

    fun read(fileType: FileType,dir:String?,file:String): File?{
        builder = StorageBuilder(context,fileType,dir,file,FileMode.READ)
        return builder.targetFile
    }

    fun read(fileType: FileType,file:String): File?{
        builder = StorageBuilder(context,fileType,file,FileMode.READ)
        return builder.targetFile
    }

    fun fileStreamPath(file:String):File{
        return InnerStorage().fileStreamPath(context,file)
    }

    fun inputFile(file:String):InputStream{
        return InnerStorage().inputFile(context,file)
    }

    fun outputFile(file:String,mode: Int=Context.MODE_PRIVATE):OutputStream{
        return InnerStorage().outputFile(context,file,mode)
    }

    /*****************************************************************************************************
     * ********************************公共多媒体目录读写接口 开始*******************************************
     *****************************************************************************************************/
    fun createImage(imageType: String,dir:String?,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeImageFile(context,imageType,dir,file,contentValues)
    }
    /**
     * 注意：
     * 1 . 系统默认的扩展名是jpg/jpeg,如果是存储的非jpg/jpeg,需要传入 MediaStore.Images.Media.MIME_TYPE
     * 2.
     */
    fun createImage(imageType: String,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeImageFile(context,imageType,null,file,contentValues)
    }

    fun createAudio(audioType: String,dir:String?,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeAudioFile(context,audioType,dir,file,contentValues)
    }

    fun createAudio(audioType: String,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeAudioFile(context,audioType,null,file,contentValues)
    }

    fun createVideo(videoType: String,dir:String?,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeVideoFile(context,videoType,dir,file,contentValues)
    }

    fun createVideo(videoType: String,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeVideoFile(context,videoType,null,file,contentValues)
    }

    fun createFile(fileType: String,dir:String?,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeFile(context,fileType,dir,file,contentValues)
    }

    fun createFile(fileType: String,file:String,contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeFile(context,fileType,null,file,contentValues)
    }

    fun getFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().readFile(context,type,dir,file)
    }

    fun getFile(type:String,file: String):XFile?{
        return PublicStore().readFile(context,type,null,file)
    }

    fun getImageFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().readImageFile(context,type,dir,file)
    }

    fun getImageFile(type:String,file: String):XFile?{
        return PublicStore().readImageFile(context,type,null,file)
    }

    fun getAudioFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().readAudioFile(context,type,dir,file)
    }

    fun getAudioFile(type:String,file: String):XFile?{
        return PublicStore().readAudioFile(context,type,null,file)
    }

    fun getVideoFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().readAudioFile(context,type,dir,file)
    }

    fun getVideoFile(type:String,file: String):XFile?{
        return PublicStore().readAudioFile(context,type,null,file)
    }


    fun deleteFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteFile(context,type,dir,file)
    }

    fun deleteFile(type:String,file: String):Int{
        return PublicStore().deleteFile(context,type,null,file)
    }

    /**
     * 删除公共多媒体文件，在Android 10 以上的系统会有提示
     * @param type 标准图片多媒体文件夹
     * @param dir 子文件夹
     * @param file 文件名称
     * @return 返回删除的条数
     */
    fun deleteImageFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteImageFile(context,type,dir,file)
    }

    fun deleteImageFile(type:String,file: String):Int{
        return PublicStore().deleteImageFile(context,type,null,file)
    }

    fun deleteAudioFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteAudioFile(context,type,dir,file)
    }

    fun deleteAudioFile(type:String,file: String):Int{
        return PublicStore().deleteAudioFile(context,type,null,file)
    }

    fun deleteVideoFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteVideoFile(context,type,dir,file)
    }

    fun deleteVideoFile(type:String,file: String):Int{
        return PublicStore().deleteVideoFile(context,type,null,file)
    }



    /********************************************************************************************************
     * **********************************通过FileProvider访问目录*********************************************
     * 1. 给系统调用(拍照，录音等等)。2 分享给其他应用，3 其他应用通过contentProvider获取（不常用）
     *******************************************************************************************************/

    fun rootFileProvider(dir:String?,file:String): Uri ?{
        return FileProviderStorage().rootPath(context,dir,file)
    }

    /********************************************************************************************************
     * **********************************通过SAF访问目录*********************************************
     * 操作外部存储，且非公共目录的时候调用这个接口
     *******************************************************************************************************/

}

val xStorage = Storage.instance

