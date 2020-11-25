package com.kits.xstorage.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kits.xstorage.R
import com.kits.xstorage.exam.ui.*
import kotlinx.android.synthetic.main.fragment_menu.*
import me.yokeyword.fragmentation.SupportFragment

class MenuFragment : SupportFragment(){
    companion object{
        fun newInstance() :MenuFragment{
            val args = Bundle()
            val fragment = MenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu,container,false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        btnInner.setOnClickListener {
            start(InnerFragment.newInstance())
        }

        btnExternal.setOnClickListener {
            start(ExternalFragment.newInstance())
        }

        btnPublicMedia.setOnClickListener {
            start(MediaFragment.newInstance())
        }
        btnSysProvider.setOnClickListener {
            start(FileProviderFragment.newInstance())
        }

        btnSAF.setOnClickListener {
            start(SAFFragment.newInstance())
        }
    }

}