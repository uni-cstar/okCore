package droid.core.util

import android.view.View
import android.view.ViewGroup
import libcore.lang.orDefault

/**
 * 从ViewGroup移除自身
 */
inline fun View?.removeFromParent() {
    this ?: return
    (this.parent as? ViewGroup)?.removeView(this)
}


inline fun View.updateLayoutSize(width: Int, height: Int) {
    updateLayoutParamsOrDefault {
        this.width = width
        this.height = height
    }
}

@JvmOverloads
inline fun View.updateLayoutWidth(
    width: Int,
    defHeightOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    updateLayoutParamsOrDefault(defHeightOfNull = defHeightOfNull) {
        if (this.width == width)
            return
        this.width = width
    }
}

@JvmOverloads
inline fun View.updateLayoutHeight(
    height: Int,
    defWidthOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    updateLayoutParamsOrDefault(defWidthOfNull = defWidthOfNull) {
        if (this.height == height)
            return
        this.height = height
    }
}

inline fun View.updateLayoutParamsOrDefault(
    defWidthOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    defHeightOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    block: ViewGroup.LayoutParams.() -> Unit
) {
    val lp = layoutParams.orDefault {
        ViewGroup.LayoutParams(
            defWidthOfNull,
            defHeightOfNull
        )
    }
    block(lp)
    layoutParams = lp
}
