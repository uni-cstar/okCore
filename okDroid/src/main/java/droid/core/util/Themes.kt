package droid.core

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import bas.droid.core.R


/**
 * 获取主题中的资源
 * @return
 */
fun Context.getThemeAttrValue(@AttrRes attr:Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

/**
 * 获取背景色
 */
fun Context.getBackgroundColor():Int{
    return getThemeAttrValue(R.attr.backgroundColor)
}

/**
 * 获取主题色
 */
fun Context.getPrimaryColor():Int{
    return getThemeAttrValue(R.attr.colorPrimary)
}

