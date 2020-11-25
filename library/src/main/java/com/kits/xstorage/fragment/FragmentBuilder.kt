package com.kits.xstorage.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class FragmentBuilder(private val currentActivity: FragmentActivity) {
    companion object{
        /**
         * 不可见Fragment的TAG，用于标记和查找
         */
        const val FRAGMENT_TAG = "InvisibleFragment"
    }

    fun requestSingleFile(intent: Intent = Intent(),key:String?,listener : SAFListener){
        getInvisibleFragment()?.requestSingleFile(intent,key,listener)
    }

    fun requestMultiFiles(intent: Intent = Intent(),listener : SAFFilesListener){
        getInvisibleFragment()?.requestMultiFile(intent,listener)
    }


    fun requestCreateDocument(intent: Intent = Intent(),key:String?,listener : SAFListener){
        getInvisibleFragment()?.requestCreateDocument(intent,key,listener)
    }


    private fun getInvisibleFragment(): InvisibleFragment? {
        val fragmentManager: FragmentManager = getFragmentManager()
        val existedFragment: Fragment? = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
        return if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNowAllowingStateLoss()
            invisibleFragment
        }
    }

    private fun getFragmentManager():FragmentManager{
        return getTopFragment()?.childFragmentManager ?: currentActivity.supportFragmentManager

    }

    private fun getTopFragment():Fragment?{
        val fragmentManager: FragmentManager = currentActivity.supportFragmentManager
        val fragmentList  = fragmentManager.fragments
        if (fragmentList.isEmpty()){
            return null
        }
        return fragmentList[fragmentList.size - 1]
    }
}