/**
 * Created by Lucio on 2021/11/10.
 * 协程相关
 */

package unics.okcore.coroutines

import unics.okcore.exception.RetryException
import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 可关闭的[CoroutineScope]
 */
class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

fun IOCoroutineContext(): CoroutineContext {
    return SupervisorJob() + Dispatchers.IO
}

fun IOCoroutineScope(context: CoroutineContext = IOCoroutineContext()): CloseableCoroutineScope {
    return CloseableCoroutineScope(context)
}


fun MainCoroutineContext() = SupervisorJob() + Dispatchers.Main.immediate

fun MainCoroutineScope(context: CoroutineContext = MainCoroutineContext()): CloseableCoroutineScope {
    return CloseableCoroutineScope(context)
}

/**
 * 在io线程执行
 */
suspend inline fun <T> ioInvoke(
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}

/**
 * 主线程执行
 */
suspend inline fun <T> mainInvoke(
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

/**
 * [launch]方法的增强版，支持重试，类似RxJava的retryWhen效果
 * 当捕获了[block]执行过程中抛出的[RetryException]时，将会重新执行[block]
 */
fun CoroutineScope.launchRetryable(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context, start) {
        var relaunch = true
        while (relaunch) {
            try {
                block.invoke(this)
                relaunch = false
            } catch (e: unics.okcore.exception.RetryException) {
                //nothing
            }
        }
    }

}


/**
 * [async]方法的增强版，支持重试，类似RxJava的retryWhen效果
 * 当捕获了[block]执行过程中抛出的[RetryException]时，将会重新执行[block]
 */
fun <T> CoroutineScope.asyncRetryable(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return async(context, start) {
        var result: Any? = null
        var reinvoke = true
        while (reinvoke) {
            try {
                result = block.invoke(this)
                reinvoke = false
            } catch (e: unics.okcore.exception.RetryException) {
            }
        }
        result as T
    }
}
