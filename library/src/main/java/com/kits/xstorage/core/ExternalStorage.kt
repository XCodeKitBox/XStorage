package com.kits.xstorage.core

import android.content.Context
import com.kits.xstorage.FileMode
import java.io.File

/**
 * 1. 外部存储空间关于权限问题：
 *    API > 19（Android4.4） 开始，应用相关的外部存储空间不需要权限。
 * 2. obbDirFile 和 files 的区别  obb 是APK 扩展文件，在google play 中会使用，国内的APP 一般不使用
 *  参见链接：https://developer.android.com/google/play/expansion-files?hl=zh-cn
 * 3. files 和 caches的区别同内部存储
 * 4 .外部存储分为机身外部存储和外置存储，由于外置存储暂时没有机会使用，不在考虑范围内
 * 5. API >=29 externalMediaDirs 被弃用
 *
 */
class ExternalStorage:BaseStorage(){

    /**
     * 在外部存储空间创建标准文件夹, /sdcard/Android/data/<applicationId>/files/
     * 1. 可以创建任意层级的文件。
     * 2. 可以保存非指定格式的文件
     * 3. type 参数也可以传入非标准文件夹名称,也可以传入空，
     * @param context 应用上下文
     * @param type 文件类型，
     * @see android.os.Environment#STANDARD_DIRECTORIES（标准文件参见 ）
     * @param dir 文件夹名称
     * @param file 文件名称
     *
     * 注意：简化API接口，type 为null ,所有的文件夹相关的路径在dir，统一设置
     */
    fun file(context: Context, type:String?, dir:String?, file:String,mode: FileMode): XFile?{
        if (!checkExternalEnable(mode)){
            return null
        }
        return when(mode){
            FileMode.GET -> readFile(context,context.getExternalFilesDir(type)?.absolutePath, dir, file)
            FileMode.WRITE-> createFile(context,context.getExternalFilesDir(type)?.absolutePath, dir, file)
        }
    }

    /**
     * 在外部存储空间创建标准文件夹, /sdcard/Android/data/<applicationId>/cache/ 可以创建任意层级的文件。
     * @param context 应用上下文
     * @param dir 文件夹名称
     * @param file 文件名称
     */
    fun cacheFile(context: Context,dir:String?,file:String,mode: FileMode):XFile?{
        if (!checkExternalEnable(mode)){
            return null
        }
        return when(mode){
            FileMode.GET -> readFile(context,context.externalCacheDir?.absolutePath, dir, file)
            FileMode.WRITE-> createFile(context,context.externalCacheDir?.absolutePath, dir, file)
        }
    }

    /**
     * 在外部存储空间/sdcard/Android/obb/<applicationId>/ 中创建文件
     * @param context 上下文
     * @param dir 需要创建的文件夹 可以为空
     * @param file 需要创建的文件
     */
    fun obbDirFile(context: Context, dir: String?, file: String,mode: FileMode): XFile? {
        if (!checkExternalEnable(mode)){
            return null
        }
        return when(mode){
            FileMode.GET -> readFile(context,context.obbDir.absolutePath, dir, file)
            FileMode.WRITE-> createFile(context,context.obbDir.absolutePath, dir, file)
        }
    }


}