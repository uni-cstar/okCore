/**
 * 防抖动事件
 */
@file:JvmName("DebounceKt")

package bas.droid.core.view.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import bas.droid.core.view.ThrottleClickListener
import libcore.coroutines.throttleFirst
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
fun DebounceClick(threshold:Long = DEFAULT_CLICK_THRESHOLD,click: (View) -> Unit):View.OnClickListener{
    return ThrottleClickListener(threshold, click)
}

