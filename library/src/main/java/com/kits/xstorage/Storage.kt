package com.kits.xstorage

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.kits.xstorage.core.FileProviderStorage
import com.kits.xstorage.core.InnerStorage
import com.kits.xstorage.core.PublicStore
import com.kits.xstorage.core.XFile
import com.kits.xstorage.fragment.FragmentBuilder
import com.kits.xstorage.fragment.SAFFilesListener
import com.kits.xstorage.fragment.SAFListener
import com.kits.xstorage.lifecycle.ActivityLifecycle
import com.kits.xstorage.utils.SpStoreUtils
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.RuntimeException
import java.util.regex.Pattern
import kotlin.jvm.Throws

class Storage private constructor(){
    lateinit var context:Context
    private lateinit var builder: StorageBuilder
    private lateinit var activityLifecycle : ActivityLifecycle
    companion object{
        val instance = StorageHolder.holder
        const val DIR_REG = "(/)\\1+"
    }

    private object StorageHolder{
        val holder = Storage()
    }

    fun init(application: Application){
        activityLifecycle = ActivityLifecycle()
        application.registerActivityLifecycleCallbacks(activityLifecycle)
        this.context = application.applicationContext
    }

    /**
     * @param application 当前应用
     * @param name SAF 已申请读写权限uri保存文件名称
     */
    fun init(application: Application,name:String){
        SpStoreUtils.init(application.applicationContext,name)
        init(application)
        SpStoreUtils.enable = true
    }

    /**
     * 格式化文件夹路径，去除多余的文件分隔符
     * 后续对文件命名规则加入判断 （1. 不能使用的特殊符号，2，文件长度的限制）
     * @param dir 文件夹路径
     * @return 有文件夹路径返回格式化后的文件路径；格式为 /xxx/xxx/
     */
    fun formatDirPath(dir:String?):String?{
        return if(dir.isNullOrEmpty()){
            null
        }else{
            Pattern.compile(DIR_REG).matcher(File.separator + dir +File.separator).replaceAll("$1")
        }
    }

    /**
     * 检查文件名称
     * 后续对文件命名规则加入判断 （1. 不能使用的特殊符号，2，文件长度的限制）
     * @param name 文件名称
     * @return 文件名称
     * @throws RuntimeException 文件名称不规范抛出异常
     */
    fun checkFileName(name:String):String{
        if (name.contains("/")){
            throw RuntimeException("文件名称不能带文件分隔符")
        }
        return name
    }

    /****************************************************************************************************
     **************************************公共操作******************************************************
     ***************************************************************************************************/
    /**
     *文件的操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param uri 需要查询的Uri
     */
    fun getFileByUri(uri:Uri):XFile?{
        return PublicStore().getFileByUri(context,uri)
    }

    /**
     * 删除文件操作，更倾向于使用Uri进行直接操作，即可以把Uri看出存在存储设备的唯一ID
     * @param uri 需要删除的Uri
     */
    fun deleteFileByUri(uri:Uri):Int{
        return PublicStore().deleteFileByUri(context,uri)
    }

    /****************************************************************************************************
     * *********************内部存储空间，外部存储空间-应用专有目录读写**************************************
     ***************************************************************************************************/
    /**
     * 写文件
     * @param fileType 文件所在的存储基准路径
     * @param dir 子文件夹路径
     * @param file 文件名称
     * @return 如果文件存在则返回XFile,如果文件不存在，则创建文件，返回XFile,存储空间不可用返回null
     * @throws RuntimeException 文件名称不规范抛出异常
     */
    @Throws(RuntimeException::class)
    fun write(fileType: FileType,dir:String?,file:String): XFile?{
        builder = StorageBuilder(context,fileType, formatDirPath(dir),checkFileName(file),FileMode.WRITE)
        return builder.targetFile
    }
    /**
     * 写文件
     * @param fileType 文件所在的存储基准路径
     * @param file 文件名称
     * @return 如果文件存在则返回XFile,如果文件不存在，则创建文件，返回XFile,存储空间不可用返回null
     *  @throws RuntimeException 文件名称不规范抛出异常
     */
    @Throws(RuntimeException::class)
    fun write(fileType: FileType,file:String): XFile?{
        builder = StorageBuilder(context,fileType,checkFileName(file),FileMode.WRITE)
        return builder.targetFile
    }
    /**
     * 获取文件
     * @param fileType 文件所在的存储基准路径
     * @param dir 子文件夹路径
     * @param file 文件名称
     * @return 文件存在成功返回XFile 否则返回null
     * @throws RuntimeException 文件名称不规范抛出异常
     */
    @Throws(RuntimeException::class)
    fun get(fileType: FileType,dir:String?,file:String): XFile?{
        builder = StorageBuilder(context,fileType,formatDirPath(dir),checkFileName(file),FileMode.GET)
        return builder.targetFile
    }
    /**
     * 获取文件
     * @param fileType 文件所在的存储基准路径
     * @param file 文件名称
     * @return 文件存在成功返回XFile 否则返回null
     * @throws RuntimeException 文件名称不规范抛出异常
     */
    @Throws(RuntimeException::class)
    fun get(fileType: FileType,file:String): XFile?{
        builder = StorageBuilder(context,fileType,checkFileName(file),FileMode.GET)
        return builder.targetFile
    }
    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param file 需要创建的文件
     * @return 返回文件操作File
     * 注意通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     */
    @Throws(RuntimeException::class)
    fun fileStreamPath(file:String):File{
        return InnerStorage().fileStreamPath(context,checkFileName(file))
    }

    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param file 需要创建的文件
     * 注意通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     */
    @Throws(RuntimeException::class)
    fun inputFile(file:String):InputStream{
        return InnerStorage().inputFile(context,checkFileName(file))
    }
    /**
     * 在内部存储空间/data/data/<applicationId>/files/ 中创建文件
     * @param file 需要创建的文件
     * @param mode 创建文件的权限
     * 注意
     * 1. 通过这个接口创建的文件只能是再files文件下直接创建文件，不能创建更深一层的子目录
     * 2. API>=24 以后文件权限 只支持MODE_PRIVATE，MODE_APPEND。MODE_WORLD_READABLE，MODE_WORLD_WRITEABLE 直接抛出异常
     */
    @Throws(RuntimeException::class)
    fun outputFile(file:String,mode: Int=Context.MODE_PRIVATE):OutputStream{
        return InnerStorage().outputFile(context,checkFileName(file),mode)
    }

    /*****************************************************************************************************
     *********************************公共多媒体目录读写接口 开始*******************************************
     *****************************************************************************************************/
    /**
     * 使用MediaStore创建图片文件
     * 相同点：在标准图片文件夹下创建文件，如果存在同名文件，系统会重命名文件。
     * 不同点： API >= 30 或者API = 29(使用沙盒机制)，MediaStore支持指定系统标准文件夹和在标准文件夹下创建子文件夹
     *         API <=29(不使用沙盒机制)，MediaStore 默认将图片存储在Pictures文件夹下
     * 注意点：存储图片文件，最好传入 MediaStore.Images.Media.MIME_TYPE
     *
     * 为了统一接口，在非沙盒模式下，使用File接口，接口对文件进行操作。
     *
     * @param imageType 图片存储的基准目录
     * @param dir 子文件夹
     * @param file 文件名称
     * @param contentValues 其他信息
     * @return 创建成功返回XFile,失败返回null
     * @throws RuntimeException 文件名称不规范抛出异常
     */
    @Throws(RuntimeException::class)
    fun createImage(imageType: String=Environment.DIRECTORY_PICTURES, dir:String?, file:String, contentValues: ContentValues = ContentValues()):XFile?{
        return PublicStore().writeImageFile(context,imageType,dir,file,contentValues)
    }

    /**
     * 读取多媒体图片资源文件,默认读取Pictures文件夹下的文件
     * @param type 标准目录文件，
     * @param dir 文件夹路径，
     * @param file 文件名称
     * @return
     */
    @Throws(RuntimeException::class)
    fun getImageFile(type:String=Environment.DIRECTORY_PICTURES,dir:String?=null,file: String):XFile?{
        return PublicStore().getImageFile(context,type,formatDirPath(dir),checkFileName(file))
    }

    /**
     * 删除公共多媒体文件，在Android 10 以上的系统会有提示
     * @param type 标准图片多媒体文件夹
     * @param dir 子文件夹
     * @param file 文件名称
     * @return 返回删除的条数
     */
    fun deleteImageFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteImageFile(context,type,formatDirPath(dir),checkFileName(file))
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
        return PublicStore().getFile(context,type,dir,file)
    }

    fun getFile(type:String,file: String):XFile?{
        return PublicStore().getFile(context,type,null,file)
    }

    fun getAudioFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().getAudioFile(context,type,dir,file)
    }

    fun getAudioFile(type:String,file: String):XFile?{
        return PublicStore().getAudioFile(context,type,null,file)
    }

    fun getVideoFile(type:String,dir:String?=null,file: String):XFile?{
        return PublicStore().getAudioFile(context,type,dir,file)
    }

    fun getVideoFile(type:String,file: String):XFile?{
        return PublicStore().getAudioFile(context,type,null,file)
    }


    fun deleteFile(type:String,dir:String?=null,file: String):Int{
        return PublicStore().deleteFile(context,type,dir,file)
    }

    fun deleteFile(type:String,file: String):Int{
        return PublicStore().deleteFile(context,type,null,file)
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
    /**
     * 获取 files-path 的文件 uri。对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */
    fun filesPathUri(dir:String?,file:String): Uri ?{
        return FileProviderStorage().filesPathUri(dir,file)
    }

    /**
     * 获取 cache-path 的文件 uri 对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */

    fun cachePathUri(dir:String?,file:String):Uri?{
        return  FileProviderStorage().cachePathUri(dir,file)
    }

    /**
     * 获取 external-files-path 的文件 uri 对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */

    fun externalFilesPathUri(dir:String?,file:String):Uri?{
        return  FileProviderStorage().externalFilesPathUri(context,dir,file)
    }

    /**
     * 获取 external-cache-path  的文件 uri  对与文件，文件存在是获取文件，不存在是创建文件
     * @param dir 文件夹
     * @param file 文件名
     * @return 成功返回Uri ,否则返回null
     */

    fun externalCachePathUri(dir:String?,file:String):Uri?{
        return  FileProviderStorage().externalCachePathUri(context,dir,file)
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
        return FileProviderStorage().publicMediaPathUri(type,dir,file,fileMode)
    }

    /********************************************************************************************************
     * **********************************通过SAF访问目录*********************************************
     * 操作外部存储，且非公共目录的时候调用这个接口
     *******************************************************************************************************/
    /**
     * 使用SAF 打开单一文件（适用于外部存储空间，自定义的目录）
     * @param intent 数据
     * @param key   保存文件Uri的key，为空时不保存
     * @param listener 打开单一文件监听器
     */
    fun openSingle(intent: Intent = Intent(),key:String?,listener : SAFListener){
        FragmentBuilder(activityLifecycle.currentActivity).requestSingleFile(intent,key,listener)
    }

    /**
     * 使用SAF 打开多个文件（适用于外部存储空间，自定义的目录）
     * @param intent 数据
     * @param listener 打开多个文件监听器
     */

    fun openMulti(intent: Intent,listener: SAFFilesListener){
        FragmentBuilder(activityLifecycle.currentActivity).requestMultiFiles(intent,listener)
    }

    /**
     * 使用SAF 打开创建文件（适用于外部存储空间，自定义的目录）
     * @param intent 数据
     * @param key   保存文件Uri的key，为空时不保存
     * @param listener 打开单一文件监听器
     */

    fun createDocument(intent: Intent,key:String?,listener : SAFListener){
        FragmentBuilder(activityLifecycle.currentActivity).requestCreateDocument(intent,key,listener)
    }
}

val xStorage = Storage.instance

