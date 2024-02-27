package bas.droid.core.util

import android.os.Build

/**
 * Android每秒满帧为60帧（现在也有120帧的设备了）
 */
const val FULL_PER_SECOND_FRAMES = 60

/**
 * 一帧的时间，大致为16ms
 */
const val ONE_FRAME_TIME = 1000 / FULL_PER_SECOND_FRAMES


inline fun <K, V> Map<K, V>.getOrDefaultExt(key: K, defaultValue: V): V {
    return if (Build.VERSION.SDK_INT >= 24) {
        this.getOrDefault(key, defaultValue)
    } else {
        this[key] ?: defaultValue
    }
}
