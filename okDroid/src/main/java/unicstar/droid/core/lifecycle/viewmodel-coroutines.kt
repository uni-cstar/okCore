/**
 * ViewModel相关的协程方法
 * @note 文件名规则：UCS+文件名第一个分类+Kt (供java使用而已)
 */
@file:JvmName("UCSViewModelKt")
@file:JvmMultifileClass
package unicstar.droid.core.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import libcore.coroutines.launchRetryable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(context, start, block)
}

/**
 * launch方法的增强版，支持重试，类似RxJava的retryWhen效果
 * 当捕获了[block]执行过程中抛出的[RetryException]时，将会重新执行[block]
 */
fun ViewModel.launchRetryable(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launchRetryable(context, start, block)
}

/**
 * 带延迟的launch（感觉没啥用）
 */
fun ViewModel.launchDelay(
    delay: Long,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context, start) {
        delay(delay)
        block.invoke(this)
    }
}