package com.wzh.trylsmodule

import android.app.Application
import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 排除系统应用和本模块自身
        if (lpparam.packageName == "android" || 
            lpparam.packageName == "com.wzh.trylsmodule" ||
            lpparam.packageName.startsWith("com.android.") ||
            lpparam.packageName.startsWith("com.google.android.")) {
            return
        }
        
        try {
            // Hook Application的onCreate方法
            XposedHelpers.findAndHookMethod(
                Application::class.java,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        
                        val application = param.thisObject as Application
                        val context: Context = application.applicationContext
                        
                        // 在主线程中显示Toast
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            try {
                                Toast.makeText(
                                    context,
                                    "哈哈哈我来啦 - ${lpparam.packageName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                // 忽略Toast显示异常
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            // 忽略Hook异常，避免影响目标应用
        }
    }
}