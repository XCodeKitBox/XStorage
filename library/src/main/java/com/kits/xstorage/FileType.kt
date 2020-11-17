package com.kits.xstorage

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi

enum class FileType(val value:String?) {
    /**
     * 内部存储空间 /data/data/<applicationId>/files/
     */
    INNER_FILE("INNER_FILE"),

    /**
     * 内部存储空间 /data/data/<applicationId>/cache/
     */
    INNER_CACHE("INNER_CACHE"),
    /**
     * 应用外部存储空间 /sdcard/Android/data/<applicationId>/files/
     */
    EXTERNAL_FILE("EXTERNAL_FILE"),

    /**
     * 应用外部存储空间  /sdcard/Android/data/<applicationId>/cache/
     */
    EXTERNAL_CACHE("EXTERNAL_CACHE"),

}