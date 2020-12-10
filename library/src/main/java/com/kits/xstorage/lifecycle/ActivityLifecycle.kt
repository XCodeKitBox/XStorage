package com.kits.xstorage.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * 监听Activity的创建，在栈顶的Activity中添加不可见的Fragment用于执行SAF，分享等操作
 */
class ActivityLifecycle : Application.ActivityLifecycleCallbacks{
    lateinit var currentActivity:FragmentActivity
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is FragmentActivity){
            println("监听到-----$activity")
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }




}