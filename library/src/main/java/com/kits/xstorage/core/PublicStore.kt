package com.kits.xstorage.core

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.kits.xstorage.FileMode
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * 权限问题：
 * 1. API < 29 需要获取读写权限
 * 2. API >=29 如果要读写非本应用创建的多媒体文件，需要获取读写权限
 * 公共多媒体目录分为：视频，音频，图像，文件目录
 * API >= 29  接口 Environment.getExternalStoragePublicDirectory() 被弃用
 * 3. 查询，删除，更新接口，这些接口本质 是熟悉SQL 语句，且使用场景不多，如果需要深度开发
 * Android 提供的接口不是非常友好，可以考虑将媒体相关的数据封装成实体类，通过接口将数据组装成SQL语句，
 * 再通过MediaStore进行查询（即将MediaStore看出是数据看，封装一个类似greenDao的库，对MediaStore进行操作）
 * 可能某些相片墙的APP有这样的需求，文档操作管理器有这样的需求
 */


class PublicStore : BaseStorage(){

    companion object{

        val standImageDir = mutableListOf<String>(Environment.DIRECTORY_DCIM,
                Environment.DIRECTORY_PICTURES,)

        val standVideo = mutableListOf<String>(Environment.DIRECTORY_MOVIES,
                Environment.DIRECTORY_DCIM,)

        val standAudio = mutableListOf<String>(Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_PODCASTS,Environment.DIRECTORY_NOTIFICATIONS,
                Environment.DIRECTORY_ALARMS,Environment.DIRECTORY_RINGTONES,)

        val standFile = mutableListOf<String>(Environment.DIRECTORY_DOWNLOADS,Environment.DIRECTORY_DOCUMENTS, )
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            standAudio.add(Environment.DIRECTORY_AUDIOBOOKS)
            standImageDir.add(Environment.DIRECTORY_SCREENSHOTS)
        }
    }



    fun writeFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(imageType == null || !standFile.contains(imageType)){
            Environment.DIRECTORY_DOWNLOADS
        }else{
            imageType
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val mediaContentUri = if(imageType == null || !standFile.contains(imageType)){
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }else{
                MediaStore.Files.getContentUri("external")
            }
            writeMediaTargetQ(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }else{
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            writeMedia(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }
    }


    fun writeImageFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(imageType == null || !standImageDir.contains(imageType)){
            Environment.DIRECTORY_PICTURES
        }else{
            imageType
        }
        val mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            writeMediaTargetQ(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }else{
            writeMedia(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }
    }

    fun writeAudioFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(imageType == null || !standVideo.contains(imageType)){
            Environment.DIRECTORY_MUSIC
        }else{
            imageType
        }
        val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            writeMediaTargetQ(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }else{
            writeMedia(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }
    }

    fun writeVideoFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(imageType == null || !standVideo.contains(imageType)){
            Environment.DIRECTORY_MOVIES
        }else{
            imageType
        }
        val mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            writeMediaTargetQ(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }else{
            writeMedia(context,mediaContentUri,mediaBaseDir,dir,file,contentValues)
        }
    }

    private fun writeMedia(context: Context,contentUri:Uri,standDir:String,dir:String?,file:String,contentValues: ContentValues):XFile?{
        val basePath = Environment.getExternalStoragePublicDirectory(standDir).absolutePath
        val targetFile = createFile(basePath,dir,file)
        return if (targetFile != null){
            contentValues.put(MediaStore.Images.Media.DATA,targetFile.absolutePath)
            val insertUri = context.contentResolver?.insert(contentUri,contentValues)
            if (insertUri!=null){
                XFile(context,insertUri)
            }else{
                null
            }
        }else{
            null
        }
    }

    /**
     * 为了尽量不使用弃用的API 接口，封装的函数，保证可以使用相对路径
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeMediaTargetQ(context: Context,contentUri:Uri,basePath:String,dir:String?,file:String,contentValues: ContentValues):XFile?{
        val relativePath = buildRelativePath(basePath,dir)

        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,relativePath)
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,file)
        val insertUri = context.contentResolver?.insert(contentUri,contentValues)

        return if (insertUri!=null){
            XFile(context,insertUri)
        }else{
            null
        }
    }

    /**
     * 简单的根据文件路径查询，默认区第一个，更为复杂的接口，需要定制化封装
     */
    private fun queryMediaByName(context: Context,contentUri:Uri,standDir:String,dir:String?,file:String):XFile?{
        val basePath = Environment.getExternalStoragePublicDirectory(standDir).absolutePath

        val targetPath = buildFilePath(basePath,dir,file)

        val queryCursor = context.contentResolver.query(contentUri,null,
            MediaStore.MediaColumns.DATA+"=?", arrayOf(targetPath),null)

         if (queryCursor != null && queryCursor.count > 0){
             // 默认第一个
             queryCursor.moveToFirst()
             val media= queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Images.Media._ID))
             val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,media)
             queryCursor.close()
             return XFile(context,uri)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryMediaByNameTargetQ(context: Context,contentUri:Uri,basePath:String,dir:String?,file:String):XFile?{
        val relativePath = buildRelativePath(basePath,dir)
        val queryCursor = context.contentResolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,null,
            MediaStore.MediaColumns.RELATIVE_PATH+"=?"+" AND "+
                    MediaStore.MediaColumns.DISPLAY_NAME+"=?",
            arrayOf(relativePath,file),null)
        if (queryCursor != null && queryCursor.count > 0){
            // 默认第一个
            queryCursor.moveToFirst()
            val mediaId= queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Images.Media._ID))
            val uri = ContentUris.withAppendedId(contentUri,mediaId)
            queryCursor.close()
            return XFile(context,uri)
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buildRelativePath(basePath:String,dir:String?):String?{
        return when(basePath){
            Environment.DIRECTORY_SCREENSHOTS->
                if (dir.isNullOrEmpty()) {
                    Environment.DIRECTORY_PICTURES+ File.separator+Environment.DIRECTORY_SCREENSHOTS
                } else {
                    Environment.DIRECTORY_PICTURES + File.separator + Environment.DIRECTORY_SCREENSHOTS + File.separator + dir + File.separator
                }
            else->{
                if (dir.isNullOrEmpty()) {
                    basePath + File.separator
                } else {
                    basePath + File.separator + dir + File.separator
                }
            }
        }
    }

    fun readFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standFile.contains(type)){
            Environment.DIRECTORY_DOWNLOADS
        }else{
            type
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val mediaContentUri = if(type == null || !standFile.contains(type)){
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }else{
                MediaStore.Files.getContentUri("external")
            }
            queryMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            queryMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun readAudioFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standVideo.contains(type)){
            Environment.DIRECTORY_MUSIC
        }else{
            type
        }
        val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            queryMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            queryMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun readVideoFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standVideo.contains(type)){
            Environment.DIRECTORY_MOVIES
        }else{
            type
        }
        val mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            queryMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            queryMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun readImageFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standImageDir.contains(type)){
            Environment.DIRECTORY_PICTURES
        }else{
            type
        }
        val mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            queryMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            queryMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }



    private fun deleteMediaByName(context: Context,contentUri:Uri,standDir:String,dir:String?,file:String):Int{
        val basePath = Environment.getExternalStoragePublicDirectory(standDir).absolutePath
        val targetPath = buildFilePath(basePath,dir,file)
        return context.contentResolver.delete(contentUri,
            MediaStore.MediaColumns.DATA+"=?", arrayOf(targetPath))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteMediaByNameTargetQ(context: Context,contentUri:Uri,basePath:String,dir:String?,file:String):Int{
        val relativePath = buildRelativePath(basePath,dir)
        return context.contentResolver.delete(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            MediaStore.MediaColumns.RELATIVE_PATH+"=?"+" AND "+
                    MediaStore.MediaColumns.DISPLAY_NAME+"=?",
            arrayOf(relativePath,file))
    }

    /**
     * @param context 上下文
     * @param type 文件媒体类型
     * @param dir 文件夹路径
     * @param file 文件名称
     * @return 等于0 删除失败，大于0 表明删除成功，同时表明删除了几个文件
     */
    fun deleteFile(context: Context,type:String?,dir:String?=null,file:String):Int{
        if(!checkExternalEnable(FileMode.WRITE)){
            return 0
        }
        val mediaBaseDir = if(type == null || !standFile.contains(type)){
            Environment.DIRECTORY_DOWNLOADS
        }else{
            type
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val mediaContentUri = if(type == null || !standFile.contains(type)){
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }else{
                MediaStore.Files.getContentUri("external")
            }
            deleteMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            deleteMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun deleteAudioFile(context: Context,type:String?,dir:String?=null,file:String):Int{
        if(!checkExternalEnable(FileMode.WRITE)){
            return 0
        }
        val mediaBaseDir = if(type == null || !standVideo.contains(type)){
            Environment.DIRECTORY_MUSIC
        }else{
            type
        }
        val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            deleteMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            deleteMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun deleteVideoFile(context: Context,type:String?,dir:String?=null,file:String):Int{
        if(!checkExternalEnable(FileMode.WRITE)){
            return 0
        }
        val mediaBaseDir = if(type == null || !standVideo.contains(type)){
            Environment.DIRECTORY_MOVIES
        }else{
            type
        }
        val mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            deleteMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            deleteMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun deleteImageFile(context: Context,type:String?,dir:String?=null,file:String):Int{
        if(!checkExternalEnable(FileMode.WRITE)){
            return 0
        }
        val mediaBaseDir = if(type == null || !standImageDir.contains(type)){
            Environment.DIRECTORY_PICTURES
        }else{
            type
        }
        val mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            deleteMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            deleteMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

}



//    enum class STAND_AUDIO_DIR(val value:String){
//        DIRECTORY_MUSIC(Environment.DIRECTORY_MUSIC), //音乐存放的标准目录。
//        DIRECTORY_PODCASTS(Environment.DIRECTORY_PODCASTS), // 系统广播存放的标准目录。
//        DIRECTORY_NOTIFICATIONS(Environment.DIRECTORY_NOTIFICATIONS), //  系统通知铃声存放的标准目录
//        DIRECTORY_ALARMS(Environment.DIRECTORY_ALARMS), // 系统提醒铃声存放的标准目录。
//        DIRECTORY_RINGTONES(Environment.DIRECTORY_RINGTONES), // 系统铃声存放的标准目录
////        @RequiresApi(Build.VERSION_CODES.Q)
////        DIRECTORY_AUDIOBOOKS(Environment.DIRECTORY_AUDIOBOOKS) // 音频书籍存放的标准目录
//    }
//
//    enum class STAND_VIDEO_DIR(val value:String){
//        DIRECTORY_MOVIES(Environment.DIRECTORY_MOVIES ),// 视频文件存储的标准目录
//        DIRECTORY_DCIM(Environment.DIRECTORY_DCIM),//视频文件通过摄像头拍摄存放在"/DCIM"
//    }
//
//    enum class STAND_IMAGE_DIR(val value:String){
//        DIRECTORY_PICTURES(Environment.DIRECTORY_PICTURES), // 图片存放的标准目录
//        DIRECTORY_DCIM(Environment.DIRECTORY_DCIM),// 相机拍摄照片和视频的标准目录@RequiresApi(Build.VERSION_CODES.Q)
//       @RequiresApi(Build.VERSION_CODES.Q)
//        DIRECTORY_SCREENSHOTS(Environment.DIRECTORY_SCREENSHOTS),// 截屏文件存储的标准目录，默认在Pictures下
//    }
//
//    enum class STAND_FILES_DIR(val value:String){
//        DIRECTORY_DOWNLOADS(Environment.DIRECTORY_DOWNLOADS),// 下载文件存储的标准目录
//        DIRECTORY_DOCUMENTS(Environment.DIRECTORY_DOCUMENTS)
//    }