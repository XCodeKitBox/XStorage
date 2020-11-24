package com.kits.xstorage.exam.utils

import android.R.attr
import android.content.Context
import android.content.SharedPreferences
import kotlin.jvm.Throws


object SpStoreUtils {

    private const val FILE_NAME = "external_sp"
    private lateinit var context: Context
    lateinit var sp:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    fun init(context: Context){
        this.context = context
        sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        editor = sp.edit()
    }

    @Throws(Exception::class)
    fun <T> setParam(key: String, value: T,apply:Boolean = true) {
        when (value) {
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key,value)
            is Double-> editor.putString(key,value.toString())
            else -> throw  Exception("不支持的类型")
        }
        if (apply){
            editor.apply()
        }
    }
    @Throws(Exception::class)
    inline fun <reified T> getParam(key: String,default:Any? = null): T{
        if (default != null){
            return default.let {
                when(default){
                    is Int -> sp.getInt(key,0) as T
                    is Long -> sp.getLong(key,0) as T
                    is String -> sp.getString(key,"") as T
                    is Boolean -> sp.getBoolean(key,false) as T
                    is Float -> sp.getFloat(key,0f) as T
                    is Double-> {
                        val t = sp.getString(key,"0")
                        return t?.toDoubleOrNull() as T
                    }
                    else -> throw  Exception("不支持的类型")
                }
            }
        }

        return when (T::class.java){
            java.lang.Integer::class.java -> sp.getInt(key,0) as T
            java.lang.Long::class.java -> sp.getLong(key,0) as T
            java.lang.String::class.java->sp.getString(key,"") as T
            java.lang.Boolean::class.java->sp.getBoolean(key,false) as T
            java.lang.Float::class.java->sp.getFloat(key,0f) as T
            java.lang.Double::class.java->{
                val t = sp.getString(key,"0")
                return t?.toDoubleOrNull() as T
            }
            else->throw  Exception("不支持的类型")
        }
    }

    fun apply() = editor.apply()

    fun commit() = editor.commit()

    fun clear() = editor.clear().commit()
}