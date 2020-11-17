package com.kits.xstorage.exam

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kits.xstorage.R
import kotlinx.coroutines.*
import me.yokeyword.fragmentation.SupportActivity

/**
 * 操作外部存储(公共)
 */
class MainActivity : SupportActivity(), CoroutineScope by MainScope() {

    companion object{
        const val REQUEST_PERMISSION_CODE = 100
        const val SHOULD_REQUEST_PERMISSION_CODE = 101
        const val REQUEST_CAMERA_CODE = 102
    }

    /**
     * 申请读写权限
     */
    private val permissionList = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val needRequestList = mutableListOf<String>()
    private val necessaryPermissionList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionList.forEach{
            // 未申请权限
            val ret = ActivityCompat.shouldShowRequestPermissionRationale(this, it)
            println("未申请权限 ret = $ret")
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED){
                // 没有申请权限加入申请权限表
                println("没有申请权限 $it")
                needRequestList.add(it)
            }
        }

        if (needRequestList.size > 0){
            ActivityCompat.requestPermissions(
                this,
                needRequestList.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }else{
            initView();
        }

    }

    /**
     * @param permissions 请求码  requestPermissions 传入
     * @param permissions 权限表
     * @param grantResults 权限申请结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == SHOULD_REQUEST_PERMISSION_CODE){
            permissions.forEach {
                println("permissions ======= $it")
            }
        }
        if (requestCode == REQUEST_PERMISSION_CODE){
            for (index in grantResults.indices){
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED){
                    println("权限 ：${permissions[index]} 允许")
                }
                if (grantResults[index] == PackageManager.PERMISSION_DENIED){
                    println("权限 ：${permissions[index]} 拒绝")
                    val ret = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permissions[index]
                    )
                    println("shouldShowRequestPermissionRationale ret = $ret")
                    necessaryPermissionList.add(permissions[index])
                }
            }
            if (necessaryPermissionList.size > 0){
                initView()
                // 创建对话框，说明权限理由 分两个 允许 和 拒绝
                // 再次启动系统会有禁止后不再询问的提示，
                //ActivityCompat.requestPermissions(this, necessaryPermissionList.toTypedArray(),REQUEST_PERMISSION_CODE)
            }else{
                initView();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initView(){
        loadRootFragment(R.id.flContainer,MenuFragment.newInstance())
    }
}