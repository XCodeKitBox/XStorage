package com.kits.xstorage.core

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.kits.xstorage.FileMode
import com.kits.xstorage.FileType
import com.kits.xstorage.xStorage
import java.io.File

/**
 * 兼容 Android 7.0  ，通过 FileProvider 提供路径给系统应用调用
 * 支持的路径有：(参见源码/parsePathStrategy)
 *     xml 配置                       代码获取的路径                                                      实际的路径
 * 1. root-path                     File("/")                                                           /
 * 2. files-path                    requireContext().filesDir.absolutePath                             /data/data/<applicationId>/files/
 * 3. cache-path                    requireContext().cacheDir.absolutePath                           /data/data/<applicationId>/cache/
 * 4. external-path                 Environment.getExternalStorageDirectory().absolutePath           /sdcard/
 * 5. external-files-path           requireContext().getExternalFilesDir(null)                       /sdcard/Android/data/<applicationId>/files/
 * 6. external-cache-path           requireContext().externalCacheDir?.absolutePath                  /sdcard//Android/data/<applicationId>/cache/
 * 7. external-media-path           requireContext().externalMediaDirs[0]                           /sdcard/Android/media/<applicationId>/
 */
class FileProviderStorage : BaseStorage(){
    /**
     * 获取 files-path 的文件 uri。对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */
    fun filesPathUri(dir:String?,file:String):Uri?{
        return xStorage.write(FileType.INNER_FILE,dir,file)?.targetUri
    }

    /**
     * 获取 cache-path 的文件 uri 对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */

    fun cachePathUri(dir:String?,file:String):Uri?{
        return xStorage.write(FileType.INNER_CACHE,dir,file)?.targetUri
    }

    /**
     * 获取 external-cache-path 的文件 uri
     * context.externalMediaDirs[0] 接口被废弃，不封装
     */

//    fun externalMediaPathUri(context: Context,dir:String?,file:String):Uri?{
//        context.externalMediaDirs[0]
//    }
    /**
     * 获取 external-files-path 的文件 uri 对与文件，文件存在是获取文件，不存在是创建文件
     * @param context 上下文
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */
    fun externalFilesPathUri(context: Context,dir:String?,file:String):Uri?{
        return xStorage.write(FileType.EXTERNAL_FILE,dir,file)?.targetUri
    }
    /**
     * 获取 external-cache-path  的文件 uri  对与文件，文件存在是获取文件，不存在是创建文件
     * @param context 上下文
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */
    fun externalCachePathUri(context: Context,dir:String?,file:String):Uri?{
        return xStorage.write(FileType.EXTERNAL_CACHE,dir,file)?.targetUri
    }


    /**
     * external-path
     * 考虑实际场景和兼容性要求，只封装公共多媒体的接口。
     * @param type 多媒体标准路径
     * @param dir 标准路径下子文件夹路径
     * @param file 文件名称
     * @param fileMode 获取文件方式，模式是读取文件
     * @return 成功返回Uri ,否则返回null
     */
    fun publicMediaPathUri(type:String,dir: String?,file: String,fileMode: FileMode = FileMode.GET):Uri?{
        return when(type){
            Environment.DIRECTORY_DCIM,Environment.DIRECTORY_PICTURES->{
               if(fileMode == FileMode.GET){
                   xStorage.getImageFile(type,dir,file)?.targetUri
               }else{
                   xStorage.createImage(type,dir,file)?.targetUri
               }
            }

            Environment.DIRECTORY_MOVIES,Environment.DIRECTORY_DCIM->{
                if(fileMode == FileMode.GET){
                    xStorage.getVideoFile(type,dir,file)?.targetUri
                }else{
                    xStorage.createVideo(type,dir,file)?.targetUri
                }
            }

            Environment.DIRECTORY_MUSIC,Environment.DIRECTORY_PODCASTS,Environment.DIRECTORY_NOTIFICATIONS,
            Environment.DIRECTORY_ALARMS,Environment.DIRECTORY_RINGTONES->{
                if(fileMode == FileMode.GET){
                    xStorage.getAudioFile(type,dir,file)?.targetUri
                }else{
                    xStorage.createAudio(type,dir,file)?.targetUri
                }
            }

            Environment.DIRECTORY_DOWNLOADS,Environment.DIRECTORY_DOCUMENTS->{
                if(fileMode == FileMode.GET){
                    xStorage.getFile(type,dir,file)?.targetUri
                }else{
                    xStorage.createFile(type,dir,file)?.targetUri
                }
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    if(type == Environment.DIRECTORY_AUDIOBOOKS){
                        return if(fileMode == FileMode.GET){
                            xStorage.getAudioFile(type,dir,file)?.targetUri
                        }else{
                            xStorage.createAudio(type,dir,file)?.targetUri
                        }
                    }

                    if(type == Environment.DIRECTORY_SCREENSHOTS){
                        return if(fileMode == FileMode.GET){
                            xStorage.getImageFile(type,dir,file)?.targetUri
                        }else{
                            xStorage.createImage(type,dir,file)?.targetUri
                        }
                    }
                }
                null
            }
        }
    }

}