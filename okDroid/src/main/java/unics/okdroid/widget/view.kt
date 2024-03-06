package unics.okdroid.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import unics.okcore.coroutines.throttleFirst
import unics.okdroid.tools.activity.activityContext
import unics.okdroid.tools.ui.tryUi
import java.lang.reflect.Method

typealias ViewClickBlock = ((View) -> Unit)

/**
 * 获取控件的[Activity]上下文
 */
val View.activityContext: Activity?
    get() {
        return context.activityContext
    }

inline fun View.runContextCatchingBlock(block: Context.() -> Unit) {
    context.run {
        tryUi {
            block(this)
        }
    }
}

inline var View.isVisibleOrNot: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }


/**
 * 从ViewGroup移除自身
 */
inline fun View?.removeFromParent() {
    this ?: return
    (this.parent as? ViewGroup)?.removeView(this)
}

fun View.updateSize(width: Int, height: Int) {
    layoutParams = layoutParams?.also {
        it.width = width
        it.height = height
    } ?: ViewGroup.LayoutParams(width, height)
}

fun View.updateHeight(height: Int, defWidthOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
    layoutParams = layoutParams?.also {
        it.height = height
    } ?: ViewGroup.LayoutParams(defWidthOfNull, height)
}

fun View.updateWidth(width: Int, defHeightOfNull: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
    layoutParams = layoutParams?.also {
        it.width = width
    } ?: ViewGroup.LayoutParams(width, defHeightOfNull)
}

private var canTakeFocusMethod: Method? = null

/**
 * view是否可以获取焦点
 */
@SuppressLint("SoonBlockedPrivateApi")
fun View.canTakeFocusCompat(): Boolean {
    return try {
        val method = canTakeFocusMethod ?: View::class.java.getDeclaredMethod("canTakeFocus").also {
            it.isAccessible = true
            canTakeFocusMethod = it
        }
        method.invoke(this) as? Boolean ?: this.canTakeFocusCustom()
    } catch (e: Throwable) {
        e.printStackTrace()
        this.canTakeFocusCustom()
    }
}

fun View.canTakeFocusCustom(): Boolean {
    return this.isFocusable && this.isVisible && this.isEnabled
}

const val DEFAULT_CLICK_THRESHOLD = 300L

/**
 * 防抖动点击流 ，效果等同于[set]
 */
fun View.debounceClickFlow(
    threshold: Long = DEFAULT_CLICK_THRESHOLD,
    click: suspend (View) -> Unit
) = callbackFlow {
    setOnClickListener {
        println("view response click")
        val result = trySend(Unit)
        if (result.isSuccess) {
            return@setOnClickListener
        }
        result.exceptionOrNull()?.printStackTrace()
    }
    awaitClose {
        setOnClickListener(null)
    }
}.throttleFirst(threshold).onEach {
    click.invoke(this)
}

fun View.debounceClick(
    lifecycleOwner: LifecycleOwner,
    threshold: Long = DEFAULT_CLICK_THRESHOLD,
    click: suspend (View) -> Unit
) {
//    lifecycleOwner.lifecycleScope.launch {
//        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
//            debounceClickFlow(threshold, click).collect()
//        }
//    }
    debounceClickFlow(threshold, click).launchIn(lifecycleOwner.lifecycleScope)
}

/**
 * 防抖动点击，效果等同于[debounceClickFlow]
 *
 * 没有使用[JvmOverloads]方式提供扩展，不方便调用
 */
fun View.onDebounceClick(
    click: (View) -> Unit
) {
    onDebounceClick(DEFAULT_CLICK_THRESHOLD, click)
}

/**
 * @param threshold 限定时间
 */
fun View.onDebounceClick(
    threshold: Long,
    click: (View) -> Unit
) {
    setOnClickListener(ThrottleClickListener(threshold, click))
}

/**
 * 放抖动点击监听器
 */
fun DebounceClick(
    threshold: Long = DEFAULT_CLICK_THRESHOLD,
    click: (View) -> Unit
): View.OnClickListener {
    return ThrottleClickListener(threshold, click)
}

