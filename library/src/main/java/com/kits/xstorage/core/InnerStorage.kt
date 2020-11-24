package com.kits.xstorage.core

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.kits.xstorage.FileMode
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * 注意点：
 * 1  API >=29 AndroidManifest 中 设置 android:hasFragileUserData="true" 应用保留数据部分包括内部存储区的数据
 * 2  cache 和 files 文件夹的区别。
 *    2.1 应用管理中清除缓存，删除的是cache下的文件夹，清除数据删除的是cache和files下的所有文件。
 *    2.2 应用在存储空间的限制下面，优先会删除cache下的文件
 * 3. fileStreamPath， openFileInput，openFileOutput 都是直接操作files文件夹下面的子文件，不能在files下创建子文件夹
 */
class InnerStorage : BaseStorage(){
    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param context 上下文
     * @param dir 需要创建/读取的文件夹 可以为空
     * @param file 需要创建/读取的文件
     *
     */
    fun files(context: Context, dir: String?, file: String,mode:FileMode): XFile? {
        return when(mode){
            FileMode.GET -> readFile(context,context.filesDir.absolutePath, dir, file)
            FileMode.WRITE-> createFile(context,context.filesDir.absolutePath, dir, file)
        }
    }

    /**
     * 在内部存储空间/data/data/<applicationId>/cache/ 中创建文件
     * @param context 上下文
     * @param dir 需要创建的文件夹 可以为空
     * @param file 需要创建的文件
     *
     */
    fun cacheFile(context: Context, dir: String?, file: String,mode:FileMode): XFile? {
        return when(mode){
            FileMode.GET -> readFile(context,context.cacheDir.absolutePath, dir, file)
            FileMode.WRITE-> createFile(context,context.cacheDir.absolutePath, dir, file)
        }
    }

    /**
     * 此位置最适合存储应用程序在运行时生成的编译或优化代码 非常少用到,Android 官方注释中
     * 建议不应用部使用这个API 接口
     */
    fun codeCachesFile(context: Context, dir: String?, file: String): XFile? {
        return createFile(context,context.codeCacheDir.absolutePath, dir, file)
    }

    /**
     * 在外部存储空间/sdcard/Android/obb/<applicationId>/ 中创建文件
     * @param context 上下文
     * @param dir 需要创建的文件夹 可以为空
     * @param file 需要创建的文件
     * 这个接口有版本要求
     */
    fun obbDirFile(context: Context, dir: String?, file: String): XFile? {
        return createFile(context,context.obbDir.absolutePath, dir, file)
    }
    /**
     * 在内部存储空间/data/data/<applicationId>/ 中创建文件
     * @param context 上下文
     * @param dir 需要创建的文件夹 可以为空
     * @param file 需要创建的文件
     * 这个接口有版本要求
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun dataFile(context: Context, dir: String?, file: String): XFile? {
        return createFile(context,context.dataDir.absolutePath, dir, file)
    }

    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param context 上下文
     * @param fullName 需要创建的文件
     * 注意通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     */

    fun fileStreamPath(context: Context, fullName: String):File{
        return context.getFileStreamPath(fullName)
    }
    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param context 上下文
     * @param fullName 需要创建的文件
     * 注意通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     */

    fun inputFile(context: Context, fullName: String): InputStream {
        return context.openFileInput(fullName)
    }
    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param context 上下文
     * @param fullName 需要创建的文件
     * @param mode 创建文件的权限
     * 注意
     * 1. 通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     * 2. API>=24 以后文件权限 只支持MODE_PRIVATE，MODE_APPEND。MODE_WORLD_READABLE，MODE_WORLD_WRITEABLE 直接抛出异常
     */
    fun outputFile(context: Context, fullName: String, mode: Int=Context.MODE_PRIVATE): OutputStream {
        return context.openFileOutput(fullName, mode)
    }

}