package com.kits.xstorage.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.kits.xstorage.core.XFile
import com.kits.xstorage.utils.SpStoreUtils
import java.lang.Exception

/**
 * 暂不考虑多线程情况
 */
class InvisibleFragment : Fragment(){
    companion object{
        /**
         * SAF 打开单一文件请求码
         */
        const val CODE_SINGLE_FILE = 100
        /**
         * SAF 打开多个文件请求码
         */
        const val CODE_MULTI_FILE = 101

        /**
         * 创建文件请求码
         */
        const val CODE_CREATE_DOCUMENT = 102

        /**
         * 打开文件夹
         */
        const val CODE_OPEN_DOCUMENT_TREE = 106
    }

    private lateinit var singleListener : SAFListener
    private lateinit var multiListener : SAFFilesListener
    private lateinit var createListener : SAFListener

    private var mKey:String? = null

    fun requestSingleFile(intent: Intent = Intent(),key:String?,listener : SAFListener){
        this.singleListener = listener
        this.mKey = key
        if (!key.isNullOrEmpty() ){
            val fileUri = SpStoreUtils.getParam<String>(key)
            fileUri?.let {
                try{
                    requireContext().contentResolver?.takePersistableUriPermission(Uri.parse(fileUri),
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    singleListener.onSuccess(XFile(requireContext(),Uri.parse(fileUri)))
                    return
                }catch (e:Exception){
                    // 未获取到权限
                    SpStoreUtils.removeParam(key)

                }
            }
        }

        startActivityForResult(intent.apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            if (type.isNullOrEmpty()){
                type = "*/*"
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
        },CODE_SINGLE_FILE)
    }

    fun requestMultiFile(intent: Intent = Intent(),listener : SAFFilesListener){
        this.multiListener = listener
        startActivityForResult(intent.apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            if (type.isNullOrEmpty()){
                type = "*/*"
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        },CODE_MULTI_FILE)
    }

    /**
     * @param intent 必须传入文件名称，Intent.EXTRA_TITLE 文件类型 ，Intent.type
     * @param key    保存uri的 key，如果为空，则不保存
     * @param listener 创建文件的监听器
     */
    fun requestCreateDocument(intent: Intent,key:String?,listener : SAFListener){
        this.createListener = listener
        this.mKey = key

        if (!key.isNullOrEmpty()){
            val fileUri = SpStoreUtils.getParam<String>(key)
            try{
                requireContext().contentResolver?.takePersistableUriPermission(Uri.parse(fileUri),
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                listener.onSuccess(XFile(requireContext(),Uri.parse(fileUri)))
                return
            }catch (e:Exception){
                SpStoreUtils.removeParam(key)
            }
        }
        startActivityForResult(intent.apply {
            action = Intent.ACTION_CREATE_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
        }, CODE_CREATE_DOCUMENT)
    }

    fun requestDocumentTree(intent: Intent = Intent(),key:String?,listener : SAFListener){
        this.singleListener = listener
        this.mKey = key
        if (!key.isNullOrEmpty() ){
            val fileUri = SpStoreUtils.getParam<String>(key)
            fileUri?.let {
                try{
                    requireContext().contentResolver?.takePersistableUriPermission(Uri.parse(fileUri),
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    singleListener.onSuccess(XFile(requireContext(),Uri.parse(fileUri)))
                    return
                }catch (e:Exception){
                    // 未获取到权限
                    SpStoreUtils.removeParam(key)

                }
            }
        }

        startActivityForResult(intent.apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            if (type.isNullOrEmpty()){
                type = "*/*"
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
        },CODE_SINGLE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK){
            when(requestCode){
                CODE_SINGLE_FILE->{
                    val fileUri = data?.data
                    if (fileUri == null){
                        singleListener.onFail()
                    }else{
                        mKey?.let {
                            SpStoreUtils.setParam(it,fileUri.toString())
                        }
                        singleListener.onSuccess(XFile(requireContext(),fileUri))
                    }
                }

                CODE_MULTI_FILE-> multiListener.onSuccess(data?.clipData)

                CODE_CREATE_DOCUMENT->{
                    val fileUri = data?.data
                    if (fileUri == null){
                        createListener.onFail()
                    }else{
                        mKey?.let {
                            println("创建成功保存保存==== $fileUri")
                            SpStoreUtils.setParam(it,fileUri.toString())
                        }
                        createListener.onSuccess(XFile(requireContext(),fileUri))
                    }
                }

            }
        }else{
            when(requestCode){
                CODE_SINGLE_FILE->singleListener.onFail()
                CODE_MULTI_FILE->multiListener.onFail()
                CODE_CREATE_DOCUMENT->createListener.onFail()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}