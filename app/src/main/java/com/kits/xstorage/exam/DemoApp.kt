package com.kits.xstorage.exam

import android.app.Application
import com.kits.xstorage.xStorage

class DemoApp :Application(){
    override fun onCreate() {
        super.onCreate()
        xStorage.init(this)
    }
}