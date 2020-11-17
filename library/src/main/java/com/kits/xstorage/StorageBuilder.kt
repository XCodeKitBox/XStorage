package com.kits.xstorage

import android.content.Context
import com.kits.xstorage.core.ExternalStorage
import com.kits.xstorage.core.InnerStorage
import java.io.File

class StorageBuilder(context: Context, fileType: FileType, dir:String?, file:String,fileMode: FileMode){
    var targetFile : File? = null

    constructor(context: Context, fileType: FileType, file:String,fileMode: FileMode):this(context,fileType,null,file,fileMode)


    init {
        when(fileType){
            FileType.INNER_FILE->
                targetFile = InnerStorage().files(context,dir,file,fileMode)
            FileType.INNER_CACHE->
                targetFile = InnerStorage().cacheFile(context,dir,file,fileMode)
            FileType.EXTERNAL_FILE->
                // 简化API接口
                targetFile = ExternalStorage().file(context,null,dir,file,fileMode)
                //targetFile = ExternalStorage().file(context,fileDirName,dir,file,fileMode)
            FileType.EXTERNAL_CACHE->
                targetFile = ExternalStorage().cacheFile(context,dir,file,fileMode)
        }
    }
}