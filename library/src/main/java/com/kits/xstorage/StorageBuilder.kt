package com.kits.xstorage

import android.content.Context
import com.kits.xstorage.core.ExternalStorage
import com.kits.xstorage.core.InnerStorage
import com.kits.xstorage.core.XFile
import java.io.File

class StorageBuilder(context: Context, fileType: FileType, dir:String?, file:String,fileMode: FileMode){
    var targetFile : XFile? = null

    constructor(context: Context, fileType: FileType, file:String,fileMode: FileMode):this(context,fileType,null,file,fileMode)


    init {
        targetFile = when(fileType){
            FileType.INNER_FILE->
                InnerStorage().files(context,dir,file,fileMode)
            FileType.INNER_CACHE->
                InnerStorage().cacheFile(context,dir,file,fileMode)
            FileType.EXTERNAL_FILE->
                // 简化API接口
                ExternalStorage().file(context,null,dir,file,fileMode)
            //targetFile = ExternalStorage().file(context,fileDirName,dir,file,fileMode)
            FileType.EXTERNAL_CACHE->
                ExternalStorage().cacheFile(context,dir,file,fileMode)
        }
    }
}