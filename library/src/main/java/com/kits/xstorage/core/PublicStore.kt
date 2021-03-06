package com.kits.xstorage.core

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.kits.xstorage.FileMode
import java.io.File
import java.net.FileNameMap
import java.net.URLConnection
import java.util.regex.Pattern


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
 * 4. 使用MediaStore对文件进行操作，需要特别考虑文件分隔符的问题，这点在Linux上文件名之间可以多个分割符不同，
 * 因此还是推荐使用Uri对文件进行操作。
 * 5. 在 API >=30 之前，通过MediaStore操作公共媒体文件不支持自定义目录，只支持自定义文件名称。如果已经存在一个同名文件，系统会重命名文件
 *   默认文件夹：图片资源存放在Pictures文件夹下；文件资源在Downloads文件夹下
 * 6. 在使用非沙盒模式的存储模式下，可以使用File操作来操作多媒体文件。
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

    /**
     * 创建文本文件，存储在 /sdcard/Download 或者 /sdcard/Documents 文件夹。默认存储在 /sdcard/Download 文件夹中
     * @param context 上下文
     * @param dir 子文件夹 可以为空
     * @param file 文件名称
     * @param contentValues 自定义传入的数据
     *
     */

    fun writeFile(context: Context,type:String?,dir:String?=null,file:String,contentValues: ContentValues):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standFile.contains(type)){
            Environment.DIRECTORY_DOWNLOADS
        }else{
            type
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val mediaContentUri = if(type != null && Environment.DIRECTORY_DOWNLOADS == type){
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
        val targetFile = createFile(context,basePath,dir,file)?.targetFile
        return targetFile?.let {
            contentValues.put(MediaStore.Images.Media.DATA,targetFile.absolutePath)
            val insertUri = context.contentResolver?.insert(contentUri,contentValues)
            if (insertUri!=null){
                XFile(context,insertUri)
            }else{
                null
            }
        }
    }

    /**
     * 为了尽量不使用弃用的API 接口，封装的函数，保证可以使用相对路径
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeMediaTargetQ(context: Context,contentUri:Uri,basePath:String,dir:String?,file:String,contentValues: ContentValues):XFile?{
        if (Environment.isExternalStorageLegacy()){
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            return writeMedia(context,mediaContentUri,basePath,dir,file,contentValues)
        }
        val relativePath = buildRelativePath(basePath,dir)

        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,relativePath)
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,file)
        println("relativePath = ${relativePath};file = ${file};")
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
             val mediaId= queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.MediaColumns._ID))
             val uri = ContentUris.withAppendedId(contentUri,mediaId)
             queryCursor.close()
             return XFile(context,uri)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryMediaByNameTargetQ(context: Context,contentUri:Uri,basePath:String,dir:String?,file:String):XFile?{

        if (Environment.isExternalStorageLegacy()){
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            return queryMediaByName(context,mediaContentUri,basePath,dir,file)
        }

        val relativePath = buildRelativePath(basePath,dir)
        val queryCursor = context.contentResolver.query(contentUri,null,
            MediaStore.MediaColumns.RELATIVE_PATH+"=?"+" AND "+
                    MediaStore.MediaColumns.DISPLAY_NAME+"=?",
            arrayOf(relativePath,file),null)
        if (queryCursor != null && queryCursor.count > 0){
            // 默认第一个
            queryCursor.moveToFirst()
            val mediaId= queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val uri = ContentUris.withAppendedId(contentUri,mediaId)
            queryCursor.close()
            return XFile(context,uri)
        }

        return null
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buildRelativePath(basePath:String,dir:String?):String?{
        val path = when(basePath){
            Environment.DIRECTORY_SCREENSHOTS->
                if (dir.isNullOrEmpty()) {
                    Environment.DIRECTORY_PICTURES+ File.separator+Environment.DIRECTORY_SCREENSHOTS
                } else {
                    Environment.DIRECTORY_PICTURES + File.separator + Environment.DIRECTORY_SCREENSHOTS+
                            File.separator + dir + File.separator
                }
            else->{
                if (dir.isNullOrEmpty()) {
                    basePath + File.separator
                } else {
                    basePath + File.separator + dir+ File.separator
                }
            }
        }
        return formatPath(path)
    }

    fun getFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }
        val mediaBaseDir = if(type == null || !standFile.contains(type)){
            Environment.DIRECTORY_DOWNLOADS
        }else{
            type
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //
            val mediaContentUri = when(type){
                Environment.DIRECTORY_DOWNLOADS->MediaStore.Downloads.EXTERNAL_CONTENT_URI
                Environment.DIRECTORY_DOCUMENTS->MediaStore.Files.getContentUri("external")
                else->MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }
            queryMediaByNameTargetQ(context,mediaContentUri,mediaBaseDir,dir,file)
        }else{
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            queryMediaByName(context,mediaContentUri,mediaBaseDir,dir,file)
        }
    }

    fun getAudioFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
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

    fun getVideoFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
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

    fun getImageFile(context: Context,type:String?,dir:String?=null,file:String):XFile?{
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
        if (Environment.isExternalStorageLegacy()){
            val mediaContentUri = MediaStore.Files.getContentUri("external")
            return deleteMediaByName(context,mediaContentUri,basePath,dir,file)
        }

        val relativePath = buildRelativePath(basePath,dir)
        return context.contentResolver.delete(contentUri,
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
     * 有些文件夹删除失败，具体原因不详
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
            val mediaContentUri = when(type){
                Environment.DIRECTORY_DOWNLOADS->MediaStore.Downloads.EXTERNAL_CONTENT_URI
                Environment.DIRECTORY_DOCUMENTS->MediaStore.Files.getContentUri("external")
                else->MediaStore.Downloads.EXTERNAL_CONTENT_URI
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

    /**
     * 对多媒体文件操作之后，通知MediaStore重新扫描
     */
    fun mediaStoreScanner(context: Context,paths: Array<String>){
        val mimeTypes = mutableListOf<String>()
        paths.forEach {
            getMimeType(File(it))?.let { it1 -> mimeTypes.add(it1) }
        }
        MediaScannerConnection.scanFile(context,paths, mimeTypes.toTypedArray()){
            path: String, uri: Uri ->

        }
    }

//    fun getMimeType(context: Context, file: File?, authority: String): String? {
//        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            FileProvider.getUriForFile(context, authority, file!!)
//        } else {
//            Uri.fromFile(file)
//        }
//        val resolver: ContentResolver = context.contentResolver
//        return resolver.getType(uri)
//    }

    private fun getMimeType(file: File): String? {
        val fileNameMap: FileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(file.name)
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