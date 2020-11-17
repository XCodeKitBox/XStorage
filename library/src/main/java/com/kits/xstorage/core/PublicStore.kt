package com.kits.xstorage.core

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.kits.xstorage.FileMode
import java.io.File
import java.io.OutputStream


/**
 * 权限问题：
 * 1. API < 29 需要获取读写权限
 * 2. API >=29 如果要读写非本应用创建的多媒体文件，需要获取读写权限
 * 公共多媒体目录分为：视频，音频，图像，文件目录
 * API >= 29  接口 Environment.getExternalStoragePublicDirectory() 被弃用
 */
class PublicStore : BaseStorage(){

    /**
     * 简易的查询接口封装，通过文件名查询，更多查询通过原生接口查询，其他多媒体，进行类似的封装
     * 由于没有想好怎么进一步优化，先不写
     */
    fun writeFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):OutputStream?{
        return null
    }

    /**
     * 如果是监听截屏数据不能使用这个接口，
     */
    fun writeImageFile(context: Context,imageType:String?,dir:String?=null,file:String,contentValues: ContentValues):OutputStream?{
        if(!checkExternalEnable(FileMode.WRITE)){
            return null
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return writeImageTargetQ(context,imageType,dir,file,contentValues)
        }else{
            val basePath = when(imageType){
                Environment.DIRECTORY_DCIM->Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                else->Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
            }
            val targetFile = createFile(basePath,dir,file)
            // 考虑兼容Android 7.0
            return if (targetFile != null){
                contentValues.put(MediaStore.Images.Media.DATA,targetFile.absolutePath)
                val insertUri = context.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                if (insertUri!=null){
                    context.contentResolver?.openOutputStream(insertUri)
                }else{
                    null
                }
            }else{
                null
            }
        }
    }

    /**
     * 为了尽量不使用弃用的API 接口，封装的函数，保证可以使用相对路径
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeImageTargetQ(context: Context,imageType:String?,dir:String?,file:String,contentValues: ContentValues):OutputStream?{
        val relativePath = when(imageType){
            Environment.DIRECTORY_PICTURES-> {
                if(dir.isNullOrEmpty()){
                    Environment.DIRECTORY_PICTURES+ File.separator+file
                }else{
                    Environment.DIRECTORY_PICTURES+ File.separator+dir+File.separator+file
                }
            }
            Environment.DIRECTORY_DCIM->{
                if(dir.isNullOrEmpty()){
                    Environment.DIRECTORY_DCIM+ File.separator+file
                }else{
                    Environment.DIRECTORY_DCIM+ File.separator+dir+File.separator+file
                }
            }
            Environment.DIRECTORY_SCREENSHOTS->{
                if (dir.isNullOrEmpty()) {
                    Environment.DIRECTORY_DCIM+ File.separator+Environment.DIRECTORY_SCREENSHOTS+File.separator+file
                } else {
                    Environment.DIRECTORY_DCIM + File.separator + Environment.DIRECTORY_SCREENSHOTS + File.separator + dir + File.separator + file
                }
            }
            else->null
        }?.let {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,it)
        }
        val insertUri = context.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)

        return if (insertUri!=null){
            context.contentResolver?.openOutputStream(insertUri)
        }else{
            null
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