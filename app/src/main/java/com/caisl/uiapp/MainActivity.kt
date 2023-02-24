package com.caisl.uiapp

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.caisl.uiapp.util.DownloadUtil
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.IUniMP
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import java.io.File

class MainActivity : AppCompatActivity() {
    /** unimp小程序实例缓存 */
    var mUniMPCaches = HashMap<String, IUniMP>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //用来测试sdkDemo 胶囊×的点击事件，是否生效；lxl增加的

        //用来测试sdkDemo 胶囊×的点击事件，是否生效；lxl增加的
        DCUniMPSDK.getInstance().setCapsuleCloseButtonClickCallBack { appid ->
            Toast.makeText(this@MainActivity, "点击×号了", Toast.LENGTH_SHORT).show()
            if (mUniMPCaches.containsKey(appid)) {
                val mIUniMP = mUniMPCaches[appid]
                if (mIUniMP != null && mIUniMP.isRuning) {
                    mIUniMP.closeUniMP()
                    mUniMPCaches.remove(appid)
                }
            }
        }
        findViewById<View>(R.id.tv_uniapp).setOnClickListener {
            try {
                val uniMP = DCUniMPSDK.getInstance().openUniMP(this@MainActivity, "__UNI__F743940")
                mUniMPCaches.put(uniMP.appid, uniMP)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.btn_net_app).setOnClickListener {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1002
            )
            updateWgt()
        }
    }

    /**
     * 模拟更新wgt
     */
    private fun updateWgt() {
        //
        val wgtUrl = "https://cdn.jin10.com/assets/minipro/__UNI__26EA1D0.wgt"
        val wgtName = "__UNI__26EA1D0.wgt"
        val downFilePath = externalCacheDir!!.path
        val uiHandler = Handler()
        DownloadUtil.get().download(
            this@MainActivity,
            wgtUrl,
            downFilePath,
            wgtName,
            object : DownloadUtil.OnDownloadListener {
                override fun onDownloadSuccess(file: File) {
                    val uniMPReleaseConfiguration = UniMPReleaseConfiguration()
                    uniMPReleaseConfiguration.wgtPath = file.path
                    uniMPReleaseConfiguration.password = "789456123"
                    uiHandler.post {
                        DCUniMPSDK.getInstance().releaseWgtToRunPath(
                            "__UNI__7AEA00D", uniMPReleaseConfiguration
                        ) { code, pArgs ->
                            Log.e("unimp", "code ---  $code  pArgs --$pArgs")
                            if (code == 1) {
                                //释放wgt完成
                                try {
                                    DCUniMPSDK.getInstance()
                                        .openUniMP(this@MainActivity, "__UNI__26EA1D0")
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                //释放wgt失败
                            }
                        }
                    }
                }

                override fun onDownloading(progress: Int) {}
                override fun onDownloadFailed() {
                    Log.e("unimp", "downFilePath  ===  onDownloadFailed")
                }
            })
    }
}