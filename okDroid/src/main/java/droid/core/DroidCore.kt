package droid.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import bas.droid.core.R
import bas.droid.core.app.UI_MODE_TYPE_UNDEFINED
import bas.droid.core.app.currentUiModeType
import bas.droid.core.app.internal.ApplicationManagerImpl
import bas.droid.core.net.URLCoderDroid
import bas.droid.core.ui.droidExceptionHandler
import bas.droid.core.util.Logger
import bas.droid.core.util.getMetaData
import libcore.base64Decoder
import libcore.base64Encoder
import libcore.exceptionHandler
import libcore.lang.security.md5
import libcore.urlCoder

/**
 * 通过反射获取Application
 *
 */
@SuppressLint("PrivateApi")
@Deprecated("不推荐使用，内部api，可能会变更")
fun getApplication(): Application? {
    try {
        return Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication").invoke(null) as Application
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}

/**
 * 库是否已初始化
 */
internal var isInit: Boolean = false

/**
 * 全局上下文
 */
lateinit var globalContext: Application

/**
 * 全局的公用SharedPreferences
 */
val globalSharedPref: SharedPreferences by lazy {
    globalContext.applicationContext.getSharedPreferences(
        globalSharedPrefFileName(),
        Context.MODE_PRIVATE
    )
}

/**
 * 初始化Core Lib
 * @param debuggable 是否开启调试模式 默认根据编译变量确定
 */
@Synchronized
fun initDroidCore(
    app: Application,
    debuggable: Boolean = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
) {
    libcore.debuggable = debuggable
    if (isInit) {
        return
    }

    isInit = true
    globalContext = app

    currentUiModeType = app.getMetaData(app.getString(R.string.bas_ui_mode), UI_MODE_TYPE_UNDEFINED)
    Logger.d("DroidCore", "currentUiModeType=$currentUiModeType")

    //设置URL编码
    urlCoder = URLCoderDroid
    //设置base64编解码
    val base64Coder = bas.droid.core.util.Base64(android.util.Base64.NO_WRAP)
    base64Encoder = base64Coder
    base64Decoder = base64Coder
    //设置异常处理器
    exceptionHandler = droidExceptionHandler
    //初始化应用管理器
    ApplicationManagerImpl.init(app)
}

internal fun globalSharedPrefFileName(): String {
    return "_${PREF_FILE_NAME.md5()}"
}

private const val PREF_FILE_NAME = "_global_pref"
