package unics.okcore.coroutines

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * 限流：在指定时间内只响应最开始的那个数据流
 * @param thresholdMillis 限流时间，即在该时间内产生的数据只发射最开始那个数据
 *
 * @see kotlinx.coroutines.flow.debounce 该方法为在指定时间内发射最新的那个数据，刚好与本方法相反
 */
fun <T> Flow<T>.throttleFirst(thresholdMillis: Long): Flow<T> = flow {
    var lastEmitTime = 0L // 上次发射数据的时间
    // 收集数据
    collect { upstream ->
        // 当前时间
        val currentTime = System.currentTimeMillis()
        println("current timestamp= $currentTime lastEmitTime=${lastEmitTime}")
        // 时间差超过阈值则发送数据并记录时间
        if (currentTime - lastEmitTime >= thresholdMillis) {
            lastEmitTime = currentTime
            emit(upstream)
        }
    }
}

/**
 * 结合旧值产生新值
 * @param initial 初始值
 * @param reduce 代码块，用于重新产生新值，其中old指旧值，value指当前值
 */
fun <T, R> Flow<T>.flatReduce(initial: R, reduce: suspend (old: R, value: T) -> R): Flow<R> =
    ReduceFlowImpl(initial, this, reduce)

/**
 * 并发流，用于两个流同时执行，但[remoteFlow]的优先级高于[localFlow]；用于诸如请求本地数据和服务器数据的情况
 * 如果发射顺序是 local 然后是 remote，则会向下发射 local和remote的结果
 * 如果发射顺序先是remote，则只会向下发射remote的结果。
 * @param localFlow 本地流
 * @param remoteFlow 远程流
 */
@OptIn(FlowPreview::class)
fun <R> flowOfMeditor(localFlow: Flow<R>, remoteFlow: Flow<R>): Flow<R> =
    flowOf(localFlow.map {
        AbortResult<R>(it, false)
    }, remoteFlow.map {
        AbortResult(it, true)
    }).flattenMerge()
        .transformWhile {
            emit(it.data)
            !it.isAbort
        }

/**
 * 用于包装数据结果
 * @param data 真实数据
 * @param isAbort 是否中断流
 */
private data class AbortResult<T>(val data: T, val isAbort: Boolean)

/**
 * 结合旧值产新值
 */
private class ReduceFlowImpl<T, R>(
    private var initial: R,
    private val upstream: Flow<T>,
    @JvmField val reducer: suspend (R, T) -> R
) : Flow<R> {
    override suspend fun collect(collector: FlowCollector<R>) {
        upstream.collect { value ->
            initial = reducer(initial, value)
            collector.emit(initial)
        }
    }
}