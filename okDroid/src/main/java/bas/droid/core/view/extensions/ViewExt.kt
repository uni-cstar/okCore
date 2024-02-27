/**
 * Created by Lucio on 2021/9/27.
 */

@file:JvmName("Views")

package bas.droid.core.view.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import bas.droid.core.app.activityContext
import bas.droid.core.ui.tryUi
import libcore.lang.orDefault

/**
 * 获取控件的[Activity]上下文
 */
val View.activityContext: Activity?
    get() {
        return context.activityContext
    }

inline fun MMLayoutParams(): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

typealias ViewClickBlock = ((View) -> Unit)

inline var View.isVisibleOrNot: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

inline fun View.visible() {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

inline fun View.invisible() {
    if (visibility != View.INVISIBLE)
        visibility = View.INVISIBLE
}

inline fun View.gone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}

/**
 * 能否获取焦点
 */
val View.canTakeFocus: Boolean
    get() = isFocusable && isVisible && isEnabled


inline fun View.runContextCatchingBlock(block: Context.() -> Unit) {
    context.run {
        tryUi {
            block(this)
        }
    }
}
