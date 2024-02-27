/*
 * Copyright (C) 2018 Lucio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("SysServices")

package bas.droid.core.util

import android.app.ActivityManager
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 震动服务
 */
inline val Context.vibrator: android.os.Vibrator
    get() = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator

/**
 * 布局加载服务LayoutInflater
 * @note 注意：此处不能使用applicationContext去获取服务，否则会导致创建的View通过getContext获取到的对象是application。
 */
inline val Context.layoutInflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

inline val View.layoutInflater: LayoutInflater get() = context.layoutInflater

/**
 * 网络服务ConnectivityManager
 */
inline val Context.connectivityManager: android.net.ConnectivityManager?
    get() = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager

inline val Context.wifiManager: WifiManager
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

/**
 * TelephonyManager
 */
inline val Context.telephonyManager: TelephonyManager
    get() = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

/**
 * ActivityManager
 */
inline val Context.activityManager: ActivityManager?
    get() = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

/**
 * PowerManager
 */
inline val Context.powerManager: PowerManager
    get() = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager

/**
 * InputMethodManager
 */
inline val Context.inputMethodManager
    get() = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

inline val Context.clipboardManager
    get() = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

