package com.caisl.uiapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.IUniMP

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
    }
}