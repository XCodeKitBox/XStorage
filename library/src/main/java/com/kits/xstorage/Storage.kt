package com.kits.xstorage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.kits.xstorage.core.FileProviderStorage
import com.kits.xstorage.core.InnerStorage
import com.kits.xstorage.core.PublicStore
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class Storage private constructor(){
    private lateinit var context:Context
    private lateinit var builder: StorageBuilder
    companion object{
        val instance = StorageHolder.holder
    }

    private object StorageHolder{
        val holder = Storage()
    }

    fun init(context: Context){
        this.context = context
    }

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

    fun writeImageMedia(imageType: String,dir:String?,file:String,contentValues: ContentValues = ContentValues()):OutputStream?{
        return PublicStore().writeImageFile(context,imageType,dir,file,contentValues)
    }

    fun writeImageMedia(imageType: String,file:String,contentValues: ContentValues = ContentValues()):OutputStream?{
        return PublicStore().writeImageFile(context,imageType,null,file,contentValues)
    }

    fun rootFileProvider(dir:String?,file:String): Uri ?{
        return FileProviderStorage().rootPath(context,dir,file)
    }


}

val xStorage = Storage.instance

